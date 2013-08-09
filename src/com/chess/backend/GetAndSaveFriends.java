package com.chess.backend;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import com.chess.backend.entity.LoadItem;
import com.chess.backend.entity.new_api.FriendsItem;
import com.chess.backend.exceptions.InternalErrorException;
import com.chess.backend.statics.AppData;
import com.chess.db.DbConstants;
import com.chess.db.DbDataManager;
import com.chess.utilities.AppUtils;

/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 11.06.13
 * Time: 22:44
 */
public class GetAndSaveFriends extends IntentService {

	protected static String[] arguments = new String[2];

	public GetAndSaveFriends() {
		super("GetAndSaveFriends");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		AppData appData = new AppData(this);

		LoadItem loadItem = LoadHelper.getFriends(appData.getUserToken());

		FriendsItem item = null;
		try {
			item  = RestHelper.requestData(loadItem, FriendsItem.class, AppUtils.getAppId(getApplicationContext()));
		} catch (InternalErrorException e) {
			e.logMe();
		}

		if (item != null) {
			String userName = appData.getUsername();
			ContentResolver contentResolver = getContentResolver();

			for (FriendsItem.Data currentItem : item.getData()) { // if
				final String[] arguments2 = arguments;
				arguments2[0] = String.valueOf(userName);
				arguments2[1] = String.valueOf(currentItem.getUserId());

				// TODO implement beginTransaction logic for performance increase
				Uri uri = DbConstants.uriArray[DbConstants.Tables.FRIENDS.ordinal()];
				Cursor cursor = contentResolver.query(uri, DbDataManager.PROJECTION_USER_ID, DbDataManager.SELECTION_USER_ID, arguments2, null);

				ContentValues values = DbDataManager.putFriendItemToValues(currentItem, userName);

				if (cursor.moveToFirst()) {
					contentResolver.update(ContentUris.withAppendedId(uri, DbDataManager.getId(cursor)), values, null, null);
				} else {
					contentResolver.insert(uri, values);
				}

				cursor.close();
			}
		}
	}
}
