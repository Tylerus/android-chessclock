package com.chess.model;

import com.chess.backend.statics.AppConstants;

import java.io.Serializable;
import java.util.HashMap;

public class GameItem implements Serializable {

	public static int GAME_DATA_ELEMENTS_COUNT = 14;

	public static final String STARTING_FEN_POSITION = "starting_fen_position";
	public static final String WHITE_RATING = "white_rating";
	public static final String BLACK_RATING = "black_rating";
	public static final String ENCODED_MOVE_STRING = "encoded_move_string";
	public static final String HAS_NEW_MESSAGE = "has_new_message";

	public HashMap<String, String> values;

	public GameItem(String[] values, boolean isLiveChess) {
		this.values = new HashMap<String, String>();
		final String gameId = isLiveChess ? values[0] : values[0].split("[+]")[1];
		this.values.put(GameListItem.GAME_ID, gameId);
		this.values.put(GameListItem.GAME_TYPE, values[1]);
		this.values.put(GameListItem.TIMESTAMP, values[2]);
		this.values.put("game_name", values[3]);
		this.values.put(AppConstants.WHITE_USERNAME, values[4].trim());
		this.values.put(AppConstants.BLACK_USERNAME, values[5].trim());
		this.values.put(STARTING_FEN_POSITION, values[6]);
		this.values.put(AppConstants.MOVE_LIST, values[7]);
		this.values.put("user_to_move", values[8]);
		this.values.put(WHITE_RATING, values[9]);
		this.values.put(BLACK_RATING, values[10]);
		this.values.put(ENCODED_MOVE_STRING, values[11]);
		this.values.put(HAS_NEW_MESSAGE, values[12]);
		this.values.put("seconds_remaining", values[13]);
		//this.values.put("move_list_coordinate", values[14]);
	}
}
