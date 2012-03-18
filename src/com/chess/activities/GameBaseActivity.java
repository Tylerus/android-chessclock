package com.chess.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.chess.R;
import com.chess.core.*;
import com.chess.core.interfaces.GameActivityFace;
import com.chess.engine.Board2;
import com.chess.engine.Move;
import com.chess.engine.MoveParser2;
import com.chess.lcc.android.GameEvent;
import com.chess.lcc.android.LccHolder;
import com.chess.utilities.CommonUtils;
import com.chess.utilities.MobclixAdViewListenerImpl;
import com.chess.utilities.MobclixHelper;
import com.chess.views.GamePanelView;
import com.chess.views.NewBoardView;
import com.mobclix.android.sdk.MobclixIABRectangleMAdView;

import java.util.Timer;

/**
 * GameBaseActivity class
 *
 * @author alien_roger
 * @created at: 05.03.12 21:18
 */
public abstract class GameBaseActivity extends CoreActivityActionBar implements View.OnClickListener, GameActivityFace {

	protected final static int DIALOG_DRAW_OFFER = 4;
	protected final static int DIALOG_ABORT_OR_RESIGN = 5;
	public final static int CALLBACK_GAME_STARTED = 10;
	public final static int CALLBACK_REPAINT_UI = 0;
	public final static int CALLBACK_GAME_REFRESH = 9;
	public final static int CALLBACK_COMP_MOVE = 2;
	public final static int CALLBACK_PLAYER_MOVE = 3;


	protected NewBoardView newBoardView;
	protected TextView whitePlayerLabel;
	protected TextView blackPlayerLabel;
	protected TextView thinking;
	//	protected TextView movelist;
	protected Timer onlineGameUpdate = null;
	protected boolean msgShowed;
	protected boolean isMoveNav;
	protected boolean chat;

	protected com.chess.model.Game game;

	protected TextView whiteClockView;
	protected TextView blackClockView;
	protected TextView analysisTxt;
	protected ViewGroup statusBarLay;

	protected AlertDialog adPopup;
	protected TextView endOfGameMessage;
	protected LinearLayout adViewWrapper;

	protected DrawOfferDialogListener drawOfferDialogListener;
	protected AbortGameDialogListener abortGameDialogListener;

	protected CharSequence[] menuOptionsItems;
	protected GamePanelView gamePanelView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (CommonUtils.needFullScreen(this)) {
			setFullscreen();
			savedInstanceState = new Bundle();
			savedInstanceState.putBoolean(AppConstants.SMALL_SCREEN, true);
		}
		super.onCreate(savedInstanceState);
	}

	protected void widgetsInit() {
		statusBarLay = (ViewGroup) findViewById(R.id.statusBarLay);

		whitePlayerLabel = (TextView) findViewById(R.id.white);
		blackPlayerLabel = (TextView) findViewById(R.id.black);
		thinking = (TextView) findViewById(R.id.thinking);
//		movelist = (TextView) findViewById(R.id.movelist);

		whiteClockView = (TextView) findViewById(R.id.whiteClockView);
		blackClockView = (TextView) findViewById(R.id.blackClockView);

		analysisTxt = (TextView) findViewById(R.id.analysisTxt);

		endOfGameMessage = (TextView) findViewById(R.id.endOfGameMessage);

		newBoardView = (NewBoardView) findViewById(R.id.boardview);
		newBoardView.setFocusable(true);
		newBoardView.setBoardFace((Board2) getLastNonConfigurationInstance());

		gamePanelView = (GamePanelView) findViewById(R.id.gamePanelView);
		newBoardView.setGamePanelView(gamePanelView);


		lccHolder = mainApp.getLccHolder();
	}

	protected void onPostCreate() {
		if (MobclixHelper.isShowAds(mainApp)) {
			setRectangleAdview(new MobclixIABRectangleMAdView(this));
			getRectangleAdview().setRefreshTime(-1);
			getRectangleAdview().addMobclixAdViewListener(new MobclixAdViewListenerImpl(true, mainApp));
			mainApp.setForceRectangleAd(false);
		}

		update(CALLBACK_REPAINT_UI);
	}

	protected void init() { // TODO call super from inheritance

		drawOfferDialogListener = new DrawOfferDialogListener();
		abortGameDialogListener = new AbortGameDialogListener();

	}

	protected abstract void onDrawOffered(int whichButton);

	private class DrawOfferDialogListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			onDrawOffered(whichButton);

		}
	}

	protected abstract void onAbortOffered(int whichButton);

	private class AbortGameDialogListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			onAbortOffered(whichButton);
		}
	}


	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_DRAW_OFFER:
				return new AlertDialog.Builder(this)
						.setTitle(R.string.drawoffer)
						.setMessage(getString(R.string.are_you_sure_q))
						.setPositiveButton(getString(R.string.ok), drawOfferDialogListener)
						.setNegativeButton(getString(R.string.cancel), drawOfferDialogListener)
						.create();
			case DIALOG_ABORT_OR_RESIGN:
				return new AlertDialog.Builder(this)
						.setTitle(R.string.abort_resign_game)
						.setMessage(getString(R.string.are_you_sure_q))
						.setPositiveButton(R.string.ok, abortGameDialogListener)
						.setNegativeButton(R.string.cancel, abortGameDialogListener)
						.create();
			default:
				break;
		}
		return super.onCreateDialog(id);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return newBoardView.getBoardFace();
	}

	protected void getOnlineGame(final String game_id) {
		if (appService != null && appService.getRepeatableTimer() != null) {
			appService.getRepeatableTimer().cancel();
			appService.setRepeatableTimer(null);
		}
		mainApp.setGameId(game_id);
	}

	protected void LoadPrev(int code) {
		if (newBoardView.getBoardFace() != null && MainApp.isTacticsGameMode(newBoardView.getBoardFace())) {
			newBoardView.getBoardFace().setTacticCanceled(true);
			onBackPressed();
		} else {
			onBackPressed();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		newBoardView.requestFocus();
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		if (isMoveNav) {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					openOptionsMenu();
				}
			}, 10);
			isMoveNav = false;
		}
		super.onOptionsMenuClosed(menu);
	}

	@Override
	protected void onResume() {
		if (MobclixHelper.isShowAds(mainApp) && mainApp.getTabHost() != null && !mainApp.getTabHost().getCurrentTabTag().equals("tab4") && adViewWrapper != null && getRectangleAdview() != null) {
			adViewWrapper.addView(getRectangleAdview());
			if (mainApp.isForceRectangleAd()) {
				getRectangleAdview().getAd();
			}
		}


		super.onResume();

		registerReceiver(gameMoveReceiver, new IntentFilter(IntentConstants.ACTION_GAME_MOVE));
		registerReceiver(gameEndMessageReceiver, new IntentFilter(IntentConstants.ACTION_GAME_END));
		registerReceiver(gameInfoMessageReceived, new IntentFilter(IntentConstants.ACTION_GAME_INFO));
		registerReceiver(showGameEndPopupReceiver, new IntentFilter(IntentConstants.ACTION_SHOW_GAME_END_POPUP));

		MobclixHelper.pauseAdview(mainApp.getBannerAdview(), mainApp);

		disableScreenLock();
	}

	@Override
	protected void onPause() {
		System.out.println("LCCLOG2: GAME ONPAUSE");
		unregisterReceiver(gameMoveReceiver);
		unregisterReceiver(gameEndMessageReceiver);
		unregisterReceiver(gameInfoMessageReceived);
		unregisterReceiver(showGameEndPopupReceiver);

		super.onPause();
		System.out.println("LCCLOG2: GAME ONPAUSE adViewWrapper="
				+ adViewWrapper + ", getRectangleAdview() " + getRectangleAdview());
		if (adViewWrapper != null && getRectangleAdview() != null) {
			System.out.println("LCCLOG2: GAME ONPAUSE 1");
			getRectangleAdview().cancelAd();
			System.out.println("LCCLOG2: GAME ONPAUSE 2");
			adViewWrapper.removeView(getRectangleAdview());
			System.out.println("LCCLOG2: GAME ONPAUSE 3");
		}
		lccHolder.setActivityPausedMode(true);
		lccHolder.getPausedActivityGameEvents().clear();

		newBoardView.stopThinking = true;

		if (onlineGameUpdate != null)
			onlineGameUpdate.cancel();

		enableScreenLock();
	}


	protected BroadcastReceiver gameMoveReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			LccHolder.LOG.info("LCCLOG ANDROID: receive broadcast intent, action=" + intent.getAction());
			game = (com.chess.model.Game) intent.getSerializableExtra(AppConstants.OBJECT);
			update(CALLBACK_GAME_REFRESH);
		}
	};

	protected BroadcastReceiver gameEndMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			LccHolder.LOG.info("LCCLOG ANDROID: receive broadcast intent, action=" + intent.getAction());

			final com.chess.live.client.Game game = lccHolder.getGame(mainApp.getGameId());
			Integer newWhiteRating = null;
			Integer newBlackRating = null;
			switch (game.getGameTimeConfig().getGameTimeClass()) {
				case BLITZ: {
					newWhiteRating = game.getWhitePlayer().getBlitzRating();
					newBlackRating = game.getBlackPlayer().getBlitzRating();
					break;
				}
				case LIGHTNING: {
					newWhiteRating = game.getWhitePlayer().getQuickRating();
					newBlackRating = game.getBlackPlayer().getQuickRating();
					break;
				}
				case STANDARD: {
					newWhiteRating = game.getWhitePlayer().getStandardRating();
					newBlackRating = game.getBlackPlayer().getStandardRating();
					break;
				}
			}
			/*final String whiteRating =
								   (newWhiteRating != null && newWhiteRating != 0) ?
								   newWhiteRating.toString() : mainApp.getCurrentGame().values.get("white_rating");
								 final String blackRating =
								   (newBlackRating != null && newBlackRating != 0) ?
								   newBlackRating.toString() : mainApp.getCurrentGame().values.get("black_rating");*/
			whitePlayerLabel.setText(game.getWhitePlayer().getUsername() + "(" + newWhiteRating + ")");
			blackPlayerLabel.setText(game.getBlackPlayer().getUsername() + "(" + newBlackRating + ")");
			newBoardView.finished = true;

			if (MobclixHelper.isShowAds(mainApp)) {
				final LayoutInflater inflater = (LayoutInflater) coreContext.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.ad_popup,
						(ViewGroup) findViewById(R.id.layout_root));
				showGameEndPopup(layout, intent.getExtras().getString(AppConstants.TITLE) + ": " + intent.getExtras().getString(AppConstants.MESSAGE));

				final View newGame = layout.findViewById(R.id.newGame);
				newGame.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (adPopup != null) {
							try {
								adPopup.dismiss();
							} catch (Exception e) {
							}
							adPopup = null;
						}
						// TODO determine which activity to run. move to upper level
						Toast.makeText(coreContext,"Should start online New Game Activity",Toast.LENGTH_SHORT).show();
//						startActivity(new Intent(coreContext, OnlineNewGame.class));
					}
				});
				newGame.setVisibility(View.VISIBLE);

				final View home = layout.findViewById(R.id.home);
				home.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (adPopup != null) {
							try {
								adPopup.dismiss();
							} catch (Exception e) {
							}
							adPopup = null;
						}
						startActivity(new Intent(coreContext, Tabs.class));
					}
				});
				home.setVisibility(View.VISIBLE);
			}

			endOfGameMessage.setText(/*intent.getExtras().getString(AppConstants.TITLE) + ": " +*/ intent.getExtras().getString(AppConstants.MESSAGE));
			//mainApp.ShowDialog(Game.this, intent.getExtras().getString(AppConstants.TITLE), intent.getExtras().getString(AppConstants.MESSAGE));
			findViewById(R.id.endOfGameButtons).setVisibility(View.VISIBLE);
			findViewById(R.id.newGame).setOnClickListener(GameBaseActivity.this);
			findViewById(R.id.home).setOnClickListener(GameBaseActivity.this);
			getSoundPlayer().playGameEnd();
			onGameEndMsgReceived();
		}
	};

	protected abstract void onGameEndMsgReceived();

	protected BroadcastReceiver gameInfoMessageReceived = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			LccHolder.LOG.info("LCCLOG ANDROID: receive broadcast intent, action=" + intent.getAction());
			mainApp.ShowDialog(coreContext, intent.getExtras()
					.getString(AppConstants.TITLE), intent.getExtras().getString(AppConstants.MESSAGE));
		}
	};

	public TextView getWhiteClockView() {
		return whiteClockView;
	}

	public TextView getBlackClockView() {
		return blackClockView;
	}

	@Override
	public void switch2Analysis(boolean isAnalysis) {
		if (isAnalysis) {
			analysisTxt.setVisibility(View.VISIBLE);
			whitePlayerLabel.setVisibility(View.INVISIBLE);
			blackPlayerLabel.setVisibility(View.INVISIBLE);
		} else {
			analysisTxt.setVisibility(View.INVISIBLE);
			whitePlayerLabel.setVisibility(View.VISIBLE);
			blackPlayerLabel.setVisibility(View.VISIBLE);
		}
	}


	protected void executePausedActivityGameEvents() {
		if (/*lccHolder.isActivityPausedMode() && */lccHolder.getPausedActivityGameEvents().size() > 0) {
			//boolean fullGameProcessed = false;
			GameEvent gameEvent = lccHolder.getPausedActivityGameEvents().get(GameEvent.Event.Move);
			if (gameEvent != null &&
					(lccHolder.getCurrentGameId() == null
							|| lccHolder.getCurrentGameId().equals(gameEvent.getGameId()))) {
				//lccHolder.processFullGame(lccHolder.getGame(gameEvent.getGameId().toString()));
				//fullGameProcessed = true;
				lccHolder.getPausedActivityGameEvents().remove(gameEvent);
				//lccHolder.getAndroid().processMove(gameEvent.getGameId(), gameEvent.moveIndex);
				game = new com.chess.model.Game(lccHolder.getGameData(
						gameEvent.getGameId().toString(), gameEvent.getMoveIndex()), true);
				update(CALLBACK_GAME_REFRESH);
			}

			gameEvent = lccHolder.getPausedActivityGameEvents().get(GameEvent.Event.DrawOffer);
			if (gameEvent != null &&
					(lccHolder.getCurrentGameId() == null
							|| lccHolder.getCurrentGameId().equals(gameEvent.getGameId()))) {
				/*if (!fullGameProcessed)
											{
											  lccHolder.processFullGame(lccHolder.getGame(gameEvent.getGameId().toString()));
											  fullGameProcessed = true;
											}*/
				lccHolder.getPausedActivityGameEvents().remove(gameEvent);
				lccHolder.getAndroid().processDrawOffered(gameEvent.getDrawOffererUsername());
			}

			gameEvent = lccHolder.getPausedActivityGameEvents().get(GameEvent.Event.EndOfGame);
			if (gameEvent != null &&
					(lccHolder.getCurrentGameId() == null || lccHolder.getCurrentGameId().equals(gameEvent.getGameId()))) {
				/*if (!fullGameProcessed)
											{
											  lccHolder.processFullGame(lccHolder.getGame(gameEvent.getGameId().toString()));
											  fullGameProcessed = true;
											}*/
				lccHolder.getPausedActivityGameEvents().remove(gameEvent);
				lccHolder.getAndroid().processGameEnd(gameEvent.getGameEndedMessage());
			}
		}
	}

	protected BroadcastReceiver showGameEndPopupReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			if (!MobclixHelper.isShowAds(mainApp)) {
				return;
			}

			final LayoutInflater inflater = (LayoutInflater) coreContext.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View layout = inflater.inflate(R.layout.ad_popup, (ViewGroup) findViewById(R.id.layout_root));
			showGameEndPopup(layout, intent.getExtras().getString(AppConstants.MESSAGE));

			final Button ok = (Button) layout.findViewById(R.id.home);
			ok.setText(getString(R.string.okay));
			ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (adPopup != null) {
						try {
							adPopup.dismiss();
						} catch (Exception e) {
						}
						adPopup = null;
					}
					if (intent.getBooleanExtra(AppConstants.FINISHABLE, false)) {
						finish();
					}
				}
			});
			ok.setVisibility(View.VISIBLE);
		}
	};

	protected void showGameEndPopup(final View layout, final String message) {
		if (!MobclixHelper.isShowAds(mainApp)) {
			return;
		}

		if (adPopup != null) {
			try {
				adPopup.dismiss();
			} catch (Exception e) {
				System.out.println("MOBCLIX: EXCEPTION IN showGameEndPopup");
				e.printStackTrace();
			}
			adPopup = null;
		}

		try {
			if (adViewWrapper != null && getRectangleAdview() != null) {
				adViewWrapper.removeView(getRectangleAdview());
			}
			adViewWrapper = (LinearLayout) layout.findViewById(R.id.adview_wrapper);
			System.out.println("MOBCLIX: GET WRAPPER " + adViewWrapper);
			adViewWrapper.addView(getRectangleAdview());

			adViewWrapper.setVisibility(View.VISIBLE);
			//showGameEndAds(adViewWrapper);

			TextView endOfGameMessagePopup = (TextView) layout.findViewById(R.id.endOfGameMessage);
			endOfGameMessagePopup.setText(message);

			adPopup.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialogInterface) {
					if (adViewWrapper != null && getRectangleAdview() != null) {
						adViewWrapper.removeView(getRectangleAdview());
					}
				}
			});
			adPopup.setOnDismissListener(new DialogInterface.OnDismissListener() {
				public void onDismiss(DialogInterface dialogInterface) {
					if (adViewWrapper != null && getRectangleAdview() != null) {
						adViewWrapper.removeView(getRectangleAdview());
					}
				}
			});
		} catch (Exception e) {
			System.out.println("MOBCLIX: EXCEPTION IN showGameEndPopup");
			e.printStackTrace();
		}

		new Handler().postDelayed(new Runnable() {
			public void run() {
				AlertDialog.Builder builder;
				//Context mContext = getApplicationContext();
				builder = new AlertDialog.Builder(coreContext);
				builder.setView(layout);
				adPopup = builder.create();
				adPopup.setCancelable(true);
				adPopup.setCanceledOnTouchOutside(true);
				try {
					adPopup.show();
				} catch (Exception e) {
				}
			}
		}, 1500);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// restoring from analysis mode        /// TODO fix
			if (newBoardView.getBoardFace().isAnalysis()) {
				newBoardView.setBoardFace(new Board2(this));
				newBoardView.getBoardFace().setInit(true);
				newBoardView.getBoardFace().setMode(extras.getInt(AppConstants.GAME_MODE));

				if (mainApp.getCurrentGame().values.get("game_type").equals("2"))
					newBoardView.getBoardFace().setChess960(true);

				if (!isUserColorWhite()) {
					newBoardView.getBoardFace().setReside(true);
				}
				String[] Moves = {};
				if (mainApp.getCurrentGame().values.get("move_list").contains("1.")) {
					Moves = mainApp.getCurrentGame().values.get("move_list")
							.replaceAll("[0-9]{1,4}[.]", "").replaceAll("  ", " ").substring(1).split(" ");
					newBoardView.getBoardFace().setMovesCount(Moves.length);
				}

				String FEN = mainApp.getCurrentGame().values.get("starting_fen_position");
				if (!FEN.equals("")) {
					newBoardView.getBoardFace().genCastlePos(FEN);
					MoveParser2.FenParse(FEN, newBoardView.getBoardFace().getBoard());
				}

				int i;
				for (i = 0; i < newBoardView.getBoardFace().getMovesCount(); i++) {

					int[] moveFT = mainApp.isLiveChess() ?
							MoveParser2.parseCoordinate(newBoardView.getBoardFace().getBoard(), Moves[i]) :
							MoveParser2.Parse(newBoardView.getBoardFace().getBoard(), Moves[i]);
					if (moveFT.length == 4) {
						Move m;
						if (moveFT[3] == 2)
							m = new Move(moveFT[0], moveFT[1], 0, 2);
						else
							m = new Move(moveFT[0], moveFT[1], moveFT[2], moveFT[3]);
						newBoardView.getBoardFace().makeMove(m, false);
					} else {
						Move m = new Move(moveFT[0], moveFT[1], 0, 0);
						newBoardView.getBoardFace().makeMove(m, false);
					}
				}
				update(CALLBACK_REPAINT_UI);
				newBoardView.getBoardFace().takeBack();
				newBoardView.invalidate();

				//last move anim
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1300);
							newBoardView.getBoardFace().takeNext();
							update.sendEmptyMessage(0);
						} catch (Exception e) {
						}
					}

					private Handler update = new Handler() {
						@Override
						public void dispatchMessage(Message msg) {
							super.dispatchMessage(msg);
							update(CALLBACK_REPAINT_UI);
							newBoardView.invalidate();
						}
					};
				}).start();
			} else {
				LoadPrev(MainApp.loadPrev);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.home) {
			startActivity(new Intent(this, HomeScreenActivity.class));
		}
	}

//	@Override
//	public void update(int code) {
//		//To change body of implemented methods use File | Settings | File Templates.
//	}


	@Override
	public void switch2Chat() {

	}


	@Override
	public Context getMeContext() {
		return coreContext;
	}

	@Override
	public void showSubmitButtonsLay(boolean show) {

	}
}
