package com.chess.db.tasks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.chess.backend.entity.api.FriendsItem;
import com.chess.backend.interfaces.TaskUpdateInterface;
import com.chess.backend.statics.AppData;
import com.chess.backend.statics.StaticData;
import com.chess.backend.tasks.AbstractUpdateTask;
import com.chess.db.DbDataManager1;
import com.chess.db.DbScheme;

import java.util.ArrayList;
import java.util.List;


public class SaveFriendsListTask extends AbstractUpdateTask<FriendsItem.Data, Long> {

	private final String userName;

	private ContentResolver contentResolver;
	protected static String[] arguments = new String[2];

	public SaveFriendsListTask(TaskUpdateInterface<FriendsItem.Data> taskFace, List<FriendsItem.Data> currentItems,
							   ContentResolver resolver) {
		super(taskFace, new ArrayList<FriendsItem.Data>());
		this.itemList.addAll(currentItems);

		this.contentResolver = resolver;
		AppData appData = new AppData(getTaskFace().getMeContext());
		userName = appData.getUsername();
	}

	@Override
	protected Integer doTheTask(Long... ids) {

		synchronized (itemList) {
			for (FriendsItem.Data currentItem : itemList) { // if
				final String[] arguments2 = arguments;
				arguments2[0] = String.valueOf(userName);
				arguments2[1] = String.valueOf(currentItem.getUserId());

				// TODO implement beginTransaction logic for performance increase
				Uri uri = DbScheme.uriArray[DbScheme.Tables.FRIENDS.ordinal()];
				Cursor cursor = contentResolver.query(uri, DbDataManager1.PROJECTION_USER_ID, DbDataManager1.SELECTION_USER_ID, arguments2, null);

				ContentValues values = DbDataManager1.putFriendItemToValues(currentItem, userName);

				DbDataManager1.updateOrInsertValues(contentResolver, cursor, uri, values);

			}
		}
		result = StaticData.RESULT_OK;

		return result;
	}

}
