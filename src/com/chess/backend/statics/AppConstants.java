package com.chess.backend.statics;

public class AppConstants {
	public static final String FACEBOOK_APP_ID = "2427617054";

	/*Sreen Features*/
	public static final String SMALL_SCREEN = "small_screen";
	// TODO split to GameType and Game Mode Constants

	public static final String GAME_MODE = "game_mode";
	public static final String TITLE = "title";

	public static final String USER_TOKEN = "user_token";
	public static final String CHALLENGE_INITIAL_TIME = "initial_time";
	public static final String CHALLENGE_BONUS_TIME = "bonus_time";
	public static final String CHALLENGE_MIN_RATING = "min_rating";
	public static final String CHALLENGE_MAX_RATING = "max_rating";
	public static final String SAVED_COMPUTER_GAME = "saving";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String API_VERSION = "api_version";
	public static final String PREF_COMPUTER_STRENGTH = "strength";
	public static final String PREF_ACTION_AFTER_MY_MOVE = "aim";
	public static final String USER_PREMIUM_STATUS = "premium_status";
	public static final String PREF_SOUNDS = "enableSounds";
	public static final String PREF_SHOW_SUBMIT_MOVE_LIVE = "ssblive";
	public static final String PREF_SHOW_SUBMIT_MOVE = "ssb";
	public static final String PREF_NOTIFICATION = "notifE";
	public static final String PREF_BOARD_COORDINATES = "coords";
	public static final String PREF_BOARD_SQUARE_HIGHLIGHT = "highlights";
	public static final String PREF_BOARD_TYPE = "boardBitmap";
	public static final String PREF_PIECES_SET = "piecesBitmap";
    public static final String PREF_VIDEO_SKILL_LEVEL = "video_skill_level";
    public static final String PREF_VIDEO_CATEGORY = "video_category";

	public static final String FULLSCREEN_AD_ALREADY_SHOWED = "fullscreen_ad_showed";
	public static final String ONLINE_GAME_LIST_TYPE = "gamestype";
	public static final String USER_SESSION_ID = "user_session_id";
	public static final String FIRST_TIME_START = "first_time_start";
	public static final String START_DAY = "start_day";
	public static final String LAST_ACTIVITY_PAUSED_TIME = "last_activity_aause_time";
	public static final String ADS_SHOW_COUNTER = "ads_show_counter";
	public static final String WHITE_USERNAME = "white_username";
	public static final String BLACK_USERNAME = "black_username";


	public static final String DESCRIPTION = "description";
	public static final String SKILL_LEVEL = "skill_level";
	public static final String OPENING = "opening";
	public static final String AUTHOR_USERNAME = "author_username";
	public static final String AUTHOR_CHESS_TITLE = "author_chess_title";
	public static final String AUTHOR_FIRST_GAME = "author_first_name";
	public static final String AUTHOR_LAST_NAME = "author_last_name";
	public static final String MINUTES = "minutes";
	public static final String PUBLISH_TIMESTAMP = "publish_timestamp";
	public static final String VIEW_URL = "view";

	public static final String SCORE = "score";
	public static final String USER_RATING_CHANGE = "user_rating_change";
	public static final String USER_RATING = "user_rating";
	public static final String PROBLEM_RATING_CHANGE = "problem_rating_change";
	public static final String PROBLEM_RATING = "problem_rating";

	public static final String ID = "id";
	public static final String FEN = "fen";
	public static final String MOVE_LIST = "move_list";
	public static final String ATTEMPT_CNT = "attempt_count";
	public static final String PASSED_CNT = "passed_count";
	public static final String RATING = "rating";
	public static final String AVG_SECONDS = "average_seconds";
	public static final String STOP = "stop";

	public final static int GAME_MODE_COMPUTER_VS_HUMAN_WHITE = 0;
	public final static int GAME_MODE_COMPUTER_VS_HUMAN_BLACK = 1;
	public final static int GAME_MODE_HUMAN_VS_HUMAN = 2;
	public final static int GAME_MODE_COMPUTER_VS_COMPUTER = 3;
//	public final static int GAME_MODE_LIVE_OR_ECHESS = 4;  // TODO refactor game modes to inheritance
	public final static int GAME_MODE_VIEW_FINISHED_ECHESS = 5;
//	public final static int GAME_MODE_TACTICS = 6;


	public static final String DEFAULT_GAMEBOARD_CASTLE = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
	public static final String REQUEST_DRAW = "Request draw: ";
	public static final String DECLINE_DRAW = "Decline draw: ";
	public static final String OFFERDRAW = "OFFERDRAW";
	public static final String ACCEPTDRAW = "ACCEPTDRAW";

	public static final String LCCLOG_RESIGN_GAME_BY_FAIR_PLAY_RESTRICTION = "LCCLOG: resign game by fair play restriction: ";
	public static final String RESIGN_GAME = "Resign game: ";

	/* LCC LOG */
	public static final String LCCLOG_RESIGN_GAME = "LCCLOG: resign game: ";
	public static final String LCCLOG_ABORT_GAME = "LCCLOG: abort game: ";

	public static final String GAME_LISTENER_IGNORE_OLD_GAME_ID = "GAME LISTENER: ignore old game id=";

	/* Messages */
	public static final String OPPONENT = "opponent";
	public static final String WARNING = ", warning: ";
	public static final String CHALLENGE = ", challenge: ";
	public static final String LISTENER = ": listener=";

	/* Stuff */
	public static final String EMAIL_MOBILE_CHESS_COM = "mobile@chess.com";
	public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";


	public static final String UTF_8 = "UTF-8";
    public static final String CURRENT_LOCALE = "current locale of screen";
}
