package com.chess.db.tasks;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.chess.backend.entity.api.LessonItem;
import com.chess.backend.interfaces.TaskUpdateInterface;
import com.chess.backend.statics.StaticData;
import com.chess.backend.tasks.AbstractUpdateTask;
import com.chess.db.DbDataManager1;
import com.chess.db.DbScheme;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 25.07.13
 * Time: 15:42
 */
public class SaveLessonsLessonTask extends AbstractUpdateTask<LessonItem.Data, Long> {

	private final long lessonId;
	private ContentResolver contentResolver;
	protected static String[] sArguments2 = new String[2];
	protected static String[] sArguments3 = new String[3];
	private String username;

	public SaveLessonsLessonTask(TaskUpdateInterface<LessonItem.Data> taskFace, LessonItem.Data currentItem,
								  ContentResolver resolver, String username) {
		super(taskFace);
		this.username = username;
		this.item = currentItem;
		lessonId = item.getId();
		this.contentResolver = resolver;
	}

	@Override
	protected Integer doTheTask(Long... ids) {


		DbDataManager1.saveMentorLessonToDb(contentResolver, item.getLesson(), lessonId);

		saveLessonPositions(item.getPositions());

		DbDataManager1.saveUserLessonToDb(contentResolver, item.getUserLesson(), lessonId, username);

		result = StaticData.RESULT_OK;

		return result;
	}

	private void saveLessonPositions(List<LessonItem.MentorPosition> positions) {
		for (LessonItem.MentorPosition position : positions) {
			position.setLessonId(lessonId);

			final String[] arguments = sArguments2;
			arguments[0] = String.valueOf(position.getLessonId());
			arguments[1] = String.valueOf(position.getPositionNumber());


			// TODO implement beginTransaction logic for performance increase
			Uri uri = DbScheme.uriArray[DbScheme.Tables.LESSONS_POSITIONS.ordinal()];
			Cursor cursor = contentResolver.query(uri, DbDataManager1.PROJECTION_ITEM_ID_AND_NUMBER,
					DbDataManager1.SELECTION_ITEM_ID_AND_NUMBER, arguments, null);

			ContentValues values = DbDataManager1.putLessonsPositionToValues(position);

			if (cursor.moveToFirst()) {
				contentResolver.update(ContentUris.withAppendedId(uri, DbDataManager1.getId(cursor)), values, null, null);
			} else {
				contentResolver.insert(uri, values);
			}

			cursor.close();

			saveLessonPositionsMoves(position.getPossibleMoves(), position.getPositionNumber());
		}
	}

	private void saveLessonPositionsMoves(List<LessonItem.MentorPosition.PossibleMove> moves, int positionNumber) {
		// TODO remove temp solution after server will fix it

		int i = 0;
		for (LessonItem.MentorPosition.PossibleMove possibleMove : moves) {
			possibleMove.setLessonId(lessonId);
			possibleMove.setPositionNumber(positionNumber);

			final String[] arguments = sArguments3;
			arguments[0] = String.valueOf(possibleMove.getLessonId());
			arguments[1] = String.valueOf(possibleMove.getPositionNumber());
//			arguments[2] = String.valueOf(possibleMove.getMoveNumber());
			arguments[2] = String.valueOf(i++);


			// TODO implement beginTransaction logic for performance increase
			Uri uri = DbScheme.uriArray[DbScheme.Tables.LESSONS_POSITION_MOVES.ordinal()];
			Cursor cursor = contentResolver.query(uri, DbDataManager1.PROJECTION_ITEM_ID_POSITION_NUMBER,
					DbDataManager1.SELECTION_ITEM_ID_POSITION_NUMBER, arguments, null);

			ContentValues values = DbDataManager1.putLessonsPositionMoveToValues(possibleMove);

			if (cursor.moveToFirst()) {
				contentResolver.update(ContentUris.withAppendedId(uri, DbDataManager1.getId(cursor)), values, null, null);
			} else {
				contentResolver.insert(uri, values);
			}

			cursor.close();
		}
	}
}

