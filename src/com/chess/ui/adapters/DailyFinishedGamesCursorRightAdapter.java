package com.chess.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.chess.R;
import com.chess.backend.RestHelper;
import com.chess.backend.image_load.ProgressImageView;
import com.chess.backend.statics.StaticData;
import com.chess.db.DbConstants;
import com.chess.model.BaseGameItem;

public class DailyFinishedGamesCursorRightAdapter extends ItemsCursorAdapter {

	protected static final String CHESS_960 = " (960)";
	private final int imageSize;
	private final String drawStr;
	private final String lossStr;
	private final String winStr;


	public DailyFinishedGamesCursorRightAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		imageSize = (int) (resources.getDimension(R.dimen.list_item_image_size_big) / resources.getDisplayMetrics().density);
		lossStr = context.getString(R.string.loss);
		winStr = context.getString(R.string.won);
		drawStr = context.getString(R.string.draw);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = inflater.inflate(R.layout.new_daily_games_item, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.playerImg = (ProgressImageView) view.findViewById(R.id.playerImg);
		holder.playerTxt = (TextView) view.findViewById(R.id.playerNameTxt);
		holder.gameInfoTxt = (TextView) view.findViewById(R.id.timeLeftTxt);

		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) convertView.getTag();

		String gameType = StaticData.SYMBOL_EMPTY;
		if (getInt(cursor, DbConstants.V_GAME_TYPE) == BaseGameItem.CHESS_960) {
			gameType = CHESS_960;
		}

		// get player side, and choose opponent
		String avatarUrl;
		String opponentName;
		if (getInt(cursor, DbConstants.V_I_PLAY_AS) == RestHelper.P_BLACK) {
			avatarUrl = getString(cursor, DbConstants.V_WHITE_AVATAR);
			opponentName = getString(cursor, DbConstants.V_WHITE_USERNAME) + gameType;
		} else {
			avatarUrl = getString(cursor, DbConstants.V_BLACK_AVATAR);
			opponentName = getString(cursor, DbConstants.V_BLACK_USERNAME) + gameType;
		}

		holder.playerTxt.setText(opponentName + gameType);
		imageLoader.download(avatarUrl, holder.playerImg, imageSize);

		String result = context.getString(R.string.loss);
		if (getInt(cursor, DbConstants.V_GAME_SCORE) == BaseGameItem.GAME_WON) {
			result = context.getString(R.string.won);
		} else if (getInt(cursor, DbConstants.V_GAME_SCORE) == BaseGameItem.GAME_DRAW) {
			result = context.getString(R.string.draw);
		}
		holder.gameInfoTxt.setText(result);

	}

	protected class ViewHolder {
		public ProgressImageView playerImg;
		public TextView playerTxt;
		public TextView gameInfoTxt;
	}
}
