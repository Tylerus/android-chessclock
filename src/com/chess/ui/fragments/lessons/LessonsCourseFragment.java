package com.chess.ui.fragments.lessons;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.chess.R;
import com.chess.backend.LoadHelper;
import com.chess.backend.entity.LoadItem;
import com.chess.backend.entity.new_api.LessonCourseItem;
import com.chess.backend.entity.new_api.LessonCourseListItem;
import com.chess.backend.entity.new_api.LessonListItem;
import com.chess.backend.tasks.RequestJsonTask;
import com.chess.db.DbDataManager;
import com.chess.db.DbHelper;
import com.chess.db.tasks.SaveLessonsCourseTask;
import com.chess.model.PopupItem;
import com.chess.ui.adapters.LessonsItemAdapter;
import com.chess.ui.fragments.CommonLogicFragment;
import com.chess.ui.fragments.popup_fragments.PopupCustomViewFragment;
import com.chess.ui.fragments.upgrade.UpgradeFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 18.07.13
 * Time: 21:36
 */
public class LessonsCourseFragment extends CommonLogicFragment implements AdapterView.OnItemClickListener {

	private static final String COURSE_ID = "course_id";
	private static final String CATEGORY_ID = "category_id";
	private static final String COURSE_COMPLETE_TAG = "course_complete popup";

	private CourseUpdateListener courseUpdateListener;
	private ChessUpdateListener<LessonCourseItem.Data> courseSaveListener;

	private LessonsItemAdapter lessonsItemAdapter;
	private LessonCourseItem.Data courseItem;
	private int courseId;
	private int categoryId;
	private boolean need2update = true;

	private TextView courseTitleTxt;
	private TextView courseDescriptionTxt;
	private ListView listView;
	private Cursor lessonsListCursor;
	private LessonListItem selectedLessonItem;
	private int selectedLessonPosition;
	private PopupCustomViewFragment completedPopupFragment;

	public LessonsCourseFragment() {}

	public static LessonsCourseFragment createInstance(int courseId, int categoryId) {
		LessonsCourseFragment fragment = new LessonsCourseFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(COURSE_ID, courseId);
		bundle.putInt(CATEGORY_ID, categoryId);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			courseId = getArguments().getInt(COURSE_ID);
			categoryId = getArguments().getInt(CATEGORY_ID);
		} else {
			courseId = savedInstanceState.getInt(COURSE_ID);
			categoryId = savedInstanceState.getInt(CATEGORY_ID);
		}

		lessonsItemAdapter = new LessonsItemAdapter(getActivity(), null);
		courseUpdateListener = new CourseUpdateListener();
		courseSaveListener = new ChessUpdateListener<LessonCourseItem.Data>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.new_white_list_view_frame, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setTitle(R.string.lessons);

		listView = (ListView) view.findViewById(R.id.listView);
		// Set header
		View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.new_lessons_course_header_view, null, false);
		if (isNeedToUpgradePremium()) {
			headerView.findViewById(R.id.upgradeBtn).setOnClickListener(this);
		} else {
			headerView.findViewById(R.id.upgradeView).setVisibility(View.GONE);
		}

		courseTitleTxt = (TextView) headerView.findViewById(R.id.courseTitleTxt);
		courseDescriptionTxt = (TextView) headerView.findViewById(R.id.courseDescriptionTxt);
		listView.addHeaderView(headerView);
		listView.setAdapter(lessonsItemAdapter);
		listView.setOnItemClickListener(this);

		// adjust action bar icons
		getActivityFace().showActionMenu(R.id.menu_search_btn, true);
		getActivityFace().showActionMenu(R.id.menu_notifications, false);
		getActivityFace().showActionMenu(R.id.menu_games, false);

		setTitlePadding(ONE_ICON);
	}

	@Override
	public void onStart() {
		super.onStart();

		if (need2update) {

			Cursor cursor = DbDataManager.executeQuery(getContentResolver(), DbHelper.getLessonCourseById(courseId));

			if (cursor != null && cursor.moveToFirst()) {  // if we have saved course
				courseItem = DbDataManager.getLessonsCourseItemFromCursor(cursor);

				lessonsListCursor = DbDataManager.executeQuery(getContentResolver(), DbHelper.getLessonsListByCourseId(courseId, getUsername()));
				if (lessonsListCursor.moveToFirst()) { // if we have saved lessons
					List<LessonListItem> lessons = new ArrayList<LessonListItem>();
					do {
						lessons.add(DbDataManager.getLessonsListItemFromCursor(lessonsListCursor));
					} while(lessonsListCursor.moveToNext());
					courseItem.setLessons(lessons);

					fillCourseData();
				}
			}
			// update anyway
			LoadItem loadItem = LoadHelper.getLessonsByCourseId(getUserToken(), courseId);
			new RequestJsonTask<LessonCourseItem>(courseUpdateListener).executeTask(loadItem);
		} else {
			courseTitleTxt.setText(courseItem.getCourseName());
			courseDescriptionTxt.setText(courseItem.getDescription());

			List<LessonListItem> lessons = courseItem.getLessons();
			lessonsItemAdapter.setItemsList(lessons);

			verifyCompletedSelectedLesson();
		}
	}

	private void verifyCompletedSelectedLesson() {
		boolean lessonCompleted = DbDataManager.isLessonCompleted(getContentResolver(), selectedLessonItem.getId(), courseId, getUsername());
		if (lessonCompleted) {
			lessonsItemAdapter.updateCompletedLessonAtPosition(selectedLessonPosition -1 ); // reduce number due header addition

			LoadItem loadItem = LoadHelper.getLessonsByCourseId(getUserToken(), courseId);
			new RequestJsonTask<LessonCourseItem>(courseUpdateListener).executeTask(loadItem);
		}
	}

	private void verifyAllLessonsCompleted() {
		if (lessonsItemAdapter.isAllLessonsCompleted()) { // show when the whole course completed
			// calculate course scores

			// don't show popup if user already saw it for this course ?
			int lessonsTotalScores = 0;
			for (LessonListItem lessonListItem : courseItem.getLessons()) {
//				lessonListItem.
				lessonsTotalScores += 10;
			}

			lessonsTotalScores  /= courseItem.getLessons().size();

			View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.new_course_complete_popup, null, false);
			popupView.findViewById(R.id.shareBtn).setOnClickListener(this);

			TextView coursePopupTitleTxt = (TextView) popupView.findViewById(R.id.courseTitleTxt);
			TextView coursePercentTxt = (TextView) popupView.findViewById(R.id.coursePercentTxt);
			TextView courseRatingTxt = (TextView) popupView.findViewById(R.id.courseRatingTxt);
			TextView courseRatingChangeTxt = (TextView) popupView.findViewById(R.id.courseRatingChangeTxt);

			coursePopupTitleTxt.setText(courseItem.getCourseName());
			coursePercentTxt.setText("100%");
			int userRating = 1234;
			courseRatingTxt.setText("" + userRating);
			courseRatingChangeTxt.setText("(+12)");

			PopupItem popupItem = new PopupItem();
			popupItem.setCustomView((LinearLayout) popupView);

			completedPopupFragment = PopupCustomViewFragment.createInstance(popupItem);
			completedPopupFragment.show(getFragmentManager(), COURSE_COMPLETE_TAG);

			{ // mark course as completed in DB
				LessonCourseListItem.Data course = new LessonCourseListItem.Data();
				course.setId(courseId);
				course.setName(courseItem.getCourseName());
				course.setCategoryId(categoryId);
				course.setUser(getUsername());
				course.setCourseCompleted(true);

				DbDataManager.saveCourseListItemToDb(getContentResolver(), course);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(COURSE_ID, courseId);
		outState.putInt(CATEGORY_ID, categoryId);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		boolean headerAdded = listView.getHeaderViewsCount() > 0; // use to check if header added
//		int offset = headerAdded ? -1 : 0;

		if (position == 0) { // if listView header
			// see onClick(View) handle
		} else {
			selectedLessonPosition = position;
			selectedLessonItem = (LessonListItem) parent.getItemAtPosition(position);
			getActivityFace().openFragment(GameLessonFragment.createInstance(selectedLessonItem.getId(), courseId));
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);

		if (view.getId() == R.id.upgradeBtn) {
			getActivityFace().openFragment(new UpgradeFragment());
		} else if (view.getId() == R.id.shareBtn) {
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.course_completed_message, courseItem.getCourseName(), 100));
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chess.com Mentor Course completed!");
			startActivity(Intent.createChooser(shareIntent, getString(R.string.share_game)));

			completedPopupFragment.dismiss();
			completedPopupFragment = null;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_search_btn:
				getActivityFace().openFragment(new LessonsSearchFragment());
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class CourseUpdateListener extends ChessLoadUpdateListener<LessonCourseItem> {

		private CourseUpdateListener() {
			super(LessonCourseItem.class);
		}

		@Override
		public void updateData(LessonCourseItem returnedObj) {
			super.updateData(returnedObj);

			courseItem = returnedObj.getData();
			fillCourseData();
			verifyAllLessonsCompleted();

			new SaveLessonsCourseTask(courseSaveListener, courseItem, getContentResolver(), getUsername()).executeTask();
		}
	}

	private void fillCourseData() {
		courseItem.setId(courseId);

		courseTitleTxt.setText(courseItem.getCourseName());
		courseDescriptionTxt.setText(courseItem.getDescription());

		List<LessonListItem> lessons = courseItem.getLessons();
		lessonsItemAdapter.setItemsList(lessons);

		need2update = false;
	}

}
