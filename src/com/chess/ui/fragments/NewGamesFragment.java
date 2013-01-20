package com.chess.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.chess.R;
import com.chess.backend.RestHelper;
import com.chess.backend.entity.LoadItem;
import com.chess.backend.entity.new_api.DailySeekItem;
import com.chess.backend.entity.new_api.FriendsItem;
import com.chess.backend.interfaces.ActionBarUpdateListener;
import com.chess.backend.interfaces.TaskUpdateInterface;
import com.chess.backend.statics.AppData;
import com.chess.backend.tasks.RequestJsonTask;
import com.chess.ui.views.NewGameCompView;
import com.chess.ui.views.NewGameDailyView;
import com.chess.ui.views.NewGameDefaultView;
import com.chess.ui.views.NewGameLiveView;


/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 12.01.13
 * Time: 9:04
 */
public class NewGamesFragment extends CommonLogicFragment implements View.OnTouchListener {

	private static final String NO_INVITED_FRIENDS_TAG = "no invited friends";
	private static final String ERROR_TAG = "send request failed popup";

	private static final int DAILY_BASE_ID = 0x00001000;
	private static final int LIVE_BASE_ID = 0x00002000;
	private static final int COMP_BASE_ID = 0x00003000;

	private final static int DAILY_RIGHT_BUTTON_ID = DAILY_BASE_ID + NewGameDailyView.RIGHT_BUTTON_ID;
	private final static int DAILY_LEFT_BUTTON_ID = DAILY_BASE_ID + NewGameDefaultView.LEFT_BUTTON_ID;
	private final static int DAILY_PLAY_BUTTON_ID = DAILY_BASE_ID + NewGameDefaultView.PLAY_BUTTON_ID;

	private final static int LIVE_LEFT_BUTTON_ID = LIVE_BASE_ID + NewGameDefaultView.LEFT_BUTTON_ID;
	private final static int LIVE_PLAY_BUTTON_ID = LIVE_BASE_ID + NewGameDefaultView.PLAY_BUTTON_ID;

	private final static int COMP_LEFT_BUTTON_ID = COMP_BASE_ID + NewGameDefaultView.LEFT_BUTTON_ID;
	private final static int COMP_PLAY_BUTTON_ID = COMP_BASE_ID + NewGameDefaultView.PLAY_BUTTON_ID;
	private NewGameDailyView dailyGamesSetupView;
	private NewGameLiveView liveGamesSetupView;
	private NewGameCompView compGamesSetupView;
	private CreateChallengeUpdateListener createChallengeUpdateListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.new_new_games_frame, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setupNewGameViews(view);
		createChallengeUpdateListener = new CreateChallengeUpdateListener();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		int id = v.getId();
		if (id == DAILY_RIGHT_BUTTON_ID) {
		} else if (id == DAILY_LEFT_BUTTON_ID) {
		} else if (id == DAILY_PLAY_BUTTON_ID) {
			// create challenge using formed configuration
			NewGameDailyView.NewDailyGameConfig newDailyGameConfig = dailyGamesSetupView.getNewDailyGameConfig();

			int color = newDailyGameConfig.getUserColor();
			int days = newDailyGameConfig.getDaysPerMove();
			int gameType = newDailyGameConfig.getGameType();
			String isRated = newDailyGameConfig.isRated()? RestHelper.V_TRUE: RestHelper.V_FALSE;
			String opponentName = newDailyGameConfig.getOpponentName();

			LoadItem loadItem = new LoadItem();
			loadItem.setLoadPath(RestHelper.CMD_SEEKS);
			loadItem.setRequestMethod(RestHelper.POST);
			loadItem.addRequestParams(RestHelper.P_LOGIN_TOKEN, AppData.getUserToken(getActivity()));
			loadItem.addRequestParams(RestHelper.P_DAYS_PER_MOVE, days);
			loadItem.addRequestParams(RestHelper.P_USER_SIDE, color);
			loadItem.addRequestParams(RestHelper.P_IS_RATED, isRated);
			loadItem.addRequestParams(RestHelper.P_GAME_TYPE, gameType);
			loadItem.addRequestParams(RestHelper.P_OPPONENT, opponentName);

			new RequestJsonTask<DailySeekItem>(createChallengeUpdateListener).executeTask(loadItem);


		} else if (id == LIVE_LEFT_BUTTON_ID) {
		} else if (id == LIVE_PLAY_BUTTON_ID) {
			// create new live game with defined parameters
			liveGamesSetupView.getNewLiveGameConfig();
		} else if (id == COMP_LEFT_BUTTON_ID) {

		} else if (id == COMP_PLAY_BUTTON_ID) {
			compGamesSetupView.getNewCompGameConfig();
		}

	}

	private class CreateChallengeUpdateListener extends ActionBarUpdateListener<DailySeekItem> {

		public CreateChallengeUpdateListener() {
			super(getInstance(), DailySeekItem.class);
		}

		@Override
		public void updateData(DailySeekItem returnedObj) {
			showSinglePopupDialog(R.string.congratulations, R.string.onlinegamecreated);
		}

		@Override
		public void errorHandle(String resultMessage) {
			showPopupDialog(getString(R.string.error), resultMessage, ERROR_TAG);
		}
	}

	private void setupNewGameViews(View view) {
		// Daily Games setup
		dailyGamesSetupView = (NewGameDailyView) view.findViewById(R.id.dailyGamesSetupView);

		NewGameDefaultView.ConfigItem dailyConfig = new NewGameDefaultView.ConfigItem();
		dailyConfig.setBaseId(DAILY_BASE_ID);
		dailyConfig.setHeaderIcon(R.drawable.ic_daily_game);
		dailyConfig.setHeaderText(R.string.new_daily_chess);
		dailyConfig.setTitleText(R.string.new_per_turn);
		dailyConfig.setLeftButtonText(R.string.days); // TODO set properly
		dailyConfig.setRightButtonText(R.string.random);

		dailyGamesSetupView.setConfig(dailyConfig);
		dailyGamesSetupView.findViewById(DAILY_RIGHT_BUTTON_ID).setOnClickListener(this);
		dailyGamesSetupView.findViewById(DAILY_LEFT_BUTTON_ID).setOnClickListener(this);
		dailyGamesSetupView.findViewById(DAILY_PLAY_BUTTON_ID).setOnClickListener(this);

		// Live Games setup
		liveGamesSetupView = (NewGameLiveView) view.findViewById(R.id.liveGamesSetupView);

		NewGameDefaultView.ConfigItem liveConfig = new NewGameDefaultView.ConfigItem();
		liveConfig.setBaseId(LIVE_BASE_ID);
		liveConfig.setHeaderIcon(R.drawable.ic_live_game);
		liveConfig.setHeaderText(R.string.new_live_chess);
		liveConfig.setTitleText(R.string.new_time);
		liveConfig.setLeftButtonText(R.string.days); // TODO set properly

		liveGamesSetupView.setConfig(liveConfig);
		liveGamesSetupView.findViewById(LIVE_LEFT_BUTTON_ID).setOnClickListener(this);
		liveGamesSetupView.findViewById(LIVE_PLAY_BUTTON_ID).setOnClickListener(this);

		// Comp Games setup
		compGamesSetupView = (NewGameCompView) view.findViewById(R.id.compGamesSetupView);

		NewGameDefaultView.ConfigItem compConfig = new NewGameDefaultView.ConfigItem();
		compConfig.setBaseId(COMP_BASE_ID);
		compConfig.setHeaderIcon(R.drawable.ic_comp_game);
		compConfig.setHeaderText(R.string.new_comp_chess);
		compConfig.setTitleText(R.string.new_difficulty);
		compConfig.setLeftButtonText(R.string.days); // TODO set properly

		compGamesSetupView.setConfig(compConfig);
		compGamesSetupView.findViewById(COMP_LEFT_BUTTON_ID).setOnClickListener(this);
		compGamesSetupView.findViewById(COMP_PLAY_BUTTON_ID).setOnClickListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d("TEST", "event " + event.getX());

		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
