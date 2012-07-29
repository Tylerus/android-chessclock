package com.chess.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chess.R;
import com.chess.backend.statics.AppConstants;
import com.chess.backend.statics.AppData;
import com.chess.backend.statics.StaticData;
import com.chess.model.PopupItem;
import com.chess.ui.engine.ChessBoard;
import com.chess.ui.engine.Move;
import com.chess.ui.fragments.PopupCustomViewFragment;
import com.chess.ui.interfaces.GameCompActivityFace;
import com.chess.ui.views.ChessBoardCompView;
import com.chess.utilities.MopubHelper;

/**
 * GameTacticsScreenActivity class
 *
 * @author alien_roger
 * @created at: 08.02.12 7:17
 */
public class GameCompScreenActivity extends GameBaseActivity implements GameCompActivityFace {

    private static final String END_GAME_TAG = "end game popup";

    private MenuOptionsDialogListener menuOptionsDialogListener;
	private ChessBoardCompView boardView;
	protected TextView thinking;
	private int[] compStrengthArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.boardview_comp);

		init();
		widgetsInit();
	}

	@Override
	protected void widgetsInit() {
		super.widgetsInit();

		thinking = (TextView) findViewById(R.id.thinking);

		boardView = (ChessBoardCompView) findViewById(R.id.boardview);
		boardView.setFocusable(true);
		boardView.setGamePanelView(gamePanelView);

		boardView.setBoardFace(new ChessBoard(this));
		boardView.setGameActivityFace(this);
		setBoardView(boardView);

		getBoardFace().setInit(true);
		getBoardFace().setMode(extras.getInt(AppConstants.GAME_MODE));
		getBoardFace().genCastlePos(AppConstants.DEFAULT_GAMEBOARD_CASTLE);
		boardView.setGameActivityFace(this);


		gamePanelView.turnCompMode();

		if (AppData.haveSavedCompGame(this)) { // if load game
			loadSavedGame();

			if (AppData.isComputerVsHumanBlackGameMode(getBoardFace()))
				getBoardFace().setReside(true);

		} else {
			if (AppData.isComputerVsHumanBlackGameMode(boardView.getBoardFace())) {
				getBoardFace().setReside(true);
				boardView.invalidate();
				boardView.computerMove(compStrengthArray[AppData.getCompStrength(getContext())]);
			}
			if (AppData.isComputerVsComputerGameMode(getBoardFace())) {
				boardView.computerMove(compStrengthArray[AppData.getCompStrength(getContext())]);
			}
		}

	}

	public void init() {
		menuOptionsItems = new CharSequence[]{
				getString(R.string.ngwhite),
				getString(R.string.ngblack),
				getString(R.string.emailgame),
				getString(R.string.settings)};

		compStrengthArray = getResources().getIntArray(R.array.comp_strength);

		menuOptionsDialogListener = new MenuOptionsDialogListener(menuOptionsItems);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (AppData.isComputerVsComputerGameMode(boardView.getBoardFace())) {
			boardView.stopThinking();
		}
	}

	@Override
	public String getWhitePlayerName() {
		return null;
	}

	@Override
	public String getBlackPlayerName() {
		return null;
	}

	@Override
	public void showOptions() {
		boardView.stopThinking(); // stopThinking = true;

		new AlertDialog.Builder(this)
				.setTitle(R.string.options)
				.setItems(menuOptionsItems, menuOptionsDialogListener).show();
	}

	@Override
	public void showSubmitButtonsLay(boolean show) {

	}

	@Override
	public void showChoosePieceDialog(int col, int row) {
	}

	@Override
	public void switch2Analysis(boolean isAnalysis) {
		if(isAnalysis){
			boardView.stopThinking();
		}else {
			boardView.think();
		}
		super.switch2Analysis(isAnalysis);
	}

    @Override
    public void updateAfterMove() {
    }

	@Override
	public void invalidateGameScreen() {
		switch (boardView.getBoardFace().getMode()) {
			case AppConstants.GAME_MODE_COMPUTER_VS_HUMAN_WHITE: {	//w - human; b - comp
				topPlayerlabel.setText(AppData.getUserName(this));
				topPlayerTimer.setText(getString(R.string.Computer));
				break;
			}
			case AppConstants.GAME_MODE_COMPUTER_VS_HUMAN_BLACK: {	//w - comp; b - human
				topPlayerlabel.setText(getString(R.string.Computer));
				topPlayerTimer.setText(AppData.getUserName(this));
				break;
			}
			case AppConstants.GAME_MODE_HUMAN_VS_HUMAN: {	//w - human; b - human
				topPlayerlabel.setText(getString(R.string.Human));
				topPlayerTimer.setText(getString(R.string.Human));
				break;
			}
			case AppConstants.GAME_MODE_COMPUTER_VS_COMPUTER: {	//w - comp; b - comp
				topPlayerlabel.setText(getString(R.string.Computer));
				topPlayerTimer.setText(getString(R.string.Computer));
				break;
			}
		}

		boardView.addMove2Log(getBoardFace().getMoveListSAN());
	}

	@Override
	public void onPlayerMove() {
		topPlayerlabel.setVisibility(View.VISIBLE);
		topPlayerTimer.setVisibility(View.VISIBLE);
		thinking.setVisibility(View.GONE);
	}

	@Override
	public void onCompMove() {
		topPlayerlabel.setVisibility(View.GONE);
		topPlayerTimer.setVisibility(View.GONE);
		thinking.setVisibility(View.VISIBLE);

	}

    @Override
	protected void restoreGame(){
		boardView.setBoardFace(new ChessBoard(this));
		boardView.setGameActivityFace(this);
		getBoardFace().setInit(true);
		getBoardFace().setMode(extras.getInt(AppConstants.GAME_MODE));
		getBoardFace().genCastlePos(AppConstants.DEFAULT_GAMEBOARD_CASTLE);
		loadSavedGame();
	}

	private void loadSavedGame(){
		int i;
		String[] moves = preferences.getString(AppConstants.SAVED_COMPUTER_GAME, StaticData.SYMBOL_EMPTY).split("[|]");
		for (i = 1; i < moves.length; i++) {
			String[] move = moves[i].split(":");
			getBoardFace().makeMove(new Move(
					Integer.parseInt(move[0]),
					Integer.parseInt(move[1]),
					Integer.parseInt(move[2]),
					Integer.parseInt(move[3])), false);
		}

		playLastMoveAnimation();
	}

	@Override
	public void newGame() {
		boardView.stopThinking();
		onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.game_comp, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_new_game: // TODO move to action bar
				newGame();
				break;
			case R.id.menu_options:	 // TODO move to action bar
				showOptions();
				break;
			case R.id.menu_reside:
				boardView.flipBoard();
				break;
			case R.id.menu_hint:
				boardView.showHint();
				break;
			case R.id.menu_previous:
				boardView.moveBack();
				break;
			case R.id.menu_next:
				boardView.moveForward();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Boolean isUserColorWhite() {
		return AppData.isComputerVsHumanWhiteGameMode(boardView.getBoardFace());
	}

	private class MenuOptionsDialogListener implements DialogInterface.OnClickListener {
		final CharSequence[] items;
		private final int NEW_GAME_WHITE = 0;
		private final int NEW_GAME_BLACK = 1;
		private final int EMAIL_GAME = 2;
		private final int SETTINGS = 3;

		private MenuOptionsDialogListener(CharSequence[] items) {
			this.items = items;
		}

		@Override
		public void onClick(DialogInterface dialogInterface, int i) {
			showToast(items[i].toString());
			switch (i) {
				case NEW_GAME_WHITE: {
					boardView.setBoardFace(new ChessBoard(GameCompScreenActivity.this));
					getBoardFace().setMode(AppConstants.GAME_MODE_COMPUTER_VS_HUMAN_WHITE);
					getBoardFace().genCastlePos(AppConstants.DEFAULT_GAMEBOARD_CASTLE);
					boardView.invalidate();
					invalidateGameScreen();
					break;
				}
				case NEW_GAME_BLACK: {
					// TODO encapsulate
					boardView.setBoardFace(new ChessBoard(GameCompScreenActivity.this));
					getBoardFace().setMode(AppConstants.GAME_MODE_COMPUTER_VS_HUMAN_BLACK);
					getBoardFace().setReside(true);
					getBoardFace().genCastlePos(AppConstants.DEFAULT_GAMEBOARD_CASTLE);
					boardView.invalidate();
					invalidateGameScreen();
					boardView.computerMove(compStrengthArray[AppData.getCompStrength(getContext())]);
					break;
				}
				case EMAIL_GAME: {
					String moves = StaticData.SYMBOL_EMPTY;
					String userName = AppData.getUserName(getContext());
					Intent emailIntent = new Intent(Intent.ACTION_SEND);
					emailIntent.setType(AppConstants.MIME_TYPE_TEXT_PLAIN);
					emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Chess Game on Android - Chess.com");
					emailIntent.putExtra(Intent.EXTRA_TEXT, "[Site \"Chess.com Android\"]\n [White \""
							+ userName + "\"]\n [White \""
							+ userName + "\"]\n [Result \"X-X\"]\n \n \n "
							+ moves + " \n \n Sent from my Android");
					startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail) /*"Send mail..."*/));
					break;
				}

				case SETTINGS: {
					startActivity(new Intent(getContext(), PreferencesScreenActivity.class));
					break;
				}
			}
		}
	}

    @Override
    protected void showGameEndPopup(View layout, String message) {

        TextView endGameTitleTxt = (TextView) layout.findViewById(R.id.endGameTitleTxt);
        TextView endGameReasonTxt = (TextView) layout.findViewById(R.id.endGameReasonTxt);
        TextView yourRatingTxt = (TextView) layout.findViewById(R.id.yourRatingTxt);
//		endGameTitleTxt.setText(R.string.game_over); // already set to game over
        endGameReasonTxt.setText(message);

        LinearLayout adViewWrapper = (LinearLayout) layout.findViewById(R.id.adview_wrapper);
        MopubHelper.showRectangleAd(adViewWrapper, this);
        PopupItem popupItem = new PopupItem();
        popupItem.setCustomView(layout);

        endPopupFragment = PopupCustomViewFragment.newInstance(popupItem);
        endPopupFragment.show(getSupportFragmentManager(), END_GAME_TAG);

        layout.findViewById(R.id.newGamePopupBtn).setVisibility(View.GONE);
        layout.findViewById(R.id.rematchPopupBtn).setVisibility(View.GONE);
        layout.findViewById(R.id.homePopupBtn).setVisibility(View.GONE);
        Button reviewBtn = (Button) layout.findViewById(R.id.reviewPopupBtn);
        reviewBtn.setText(R.string.ok);
        reviewBtn.setOnClickListener(this);
        layout.findViewById(R.id.upgradeBtn).setOnClickListener(this);
    }
}