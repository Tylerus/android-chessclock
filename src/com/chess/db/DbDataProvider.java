
package com.chess.db;

import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.chess.backend.exceptions.DbDataProviderException;
import com.chess.utilities.Distribution;

import java.util.Map;

import static com.chess.db.DbScheme.Tables;

/**
 * @author alien_roger
 * @created 27.10.12
 * @modified 27.10.12
 */
public class DbDataProvider extends ContentProvider {

    private static final UriMatcher uriMatcher;

    private static final UriMatcher uriMatcherIds;

    public static final String SLASH_NUMBER = "/#";

    public static final String EQUALS = " = ";

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcherIds = new UriMatcher(UriMatcher.NO_MATCH);

        for (int i = 0; i < Tables.values().length; i++) {
            String table = Tables.values()[i].name();
            uriMatcher.addURI(DbScheme.PROVIDER_NAME, table, i);
        }

        for (int i = 0; i < Tables.values().length; i++) {
            String table = Tables.values()[i].name();
            uriMatcherIds.addURI(DbScheme.PROVIDER_NAME, table + SLASH_NUMBER, i);
        }
    }

    public static final String VND_ANDROID_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String VND_ANDROID_CURSOR_ITEM = "vnd.android.cursor.item/";

    private DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DatabaseHelper(context);
        appDataBase = dbHelper.getWritableDatabase();

        return (appDataBase != null);
    }

    @Override
    public String getType(Uri uri) {
        for (int i = 0; i < Tables.values().length; i++) {
            if (uriMatcher.match(uri) == i) {
                return VND_ANDROID_CURSOR_DIR + DbScheme.PROVIDER_NAME;
            } else if (uriMatcherIds.match(uri) == i) {
                return VND_ANDROID_CURSOR_ITEM + DbScheme.PROVIDER_NAME;
            }

        }
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();

        boolean found = false;
        for (int i = 0; i < Tables.values().length; i++) {
            if (uriMatcher.match(uri) == i) {
                sqlBuilder.setTables(Tables.values()[i].name());
                found = true;
            } else if (uriMatcherIds.match(uri) == i) {
                sqlBuilder.setTables(Tables.values()[i].name());
                sqlBuilder.appendWhere(DbScheme._ID + EQUALS + uri.getPathSegments().get(1));
                found = true;
            }

            if (found) {
                Cursor c = sqlBuilder.query(appDataBase, projection, selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            }
        }
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        boolean found = false;
        for (int i = 0; i < Tables.values().length; i++) {
            if (uriMatcher.match(uri) == i) {
                count = appDataBase.update(Tables.values()[i].name(), values, selection,
                        selectionArgs);
                found = true;
            } else if (uriMatcherIds.match(uri) == i) {
                count = appDataBase.update(Tables.values()[i].name(), values, DbScheme._ID + EQUALS
                        + uri.getPathSegments().get(1)
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                found = true;
            }

            if (found) {
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
        }
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        // uri not found by default
        Boolean uriFound = false;
        Distribution.setFlagValue("uriFound", uriFound.toString());
        Distribution.setFlagValue("uri", uri.toString());

        // serializing values for debug
        StringBuilder valuesStr = new StringBuilder();
        boolean firstKey = true;
        for (Map.Entry<String, Object> entry : values.valueSet()) {
            if (firstKey) {
                firstKey = false;
                valuesStr.append("{");
            } else {
                valuesStr.append(", ");
            }
            valuesStr.append("[key=\"").append(entry.getKey()).append("\", value=\"")
                    .append(entry.getValue()).append("\"]");
        }
        valuesStr.append("}");
        Distribution.setFlagValue("insertedValues", valuesStr.toString());

        // loop on tables
        for (int i = 0; i < Tables.values().length; i++) {
            if (uriMatcher.match(uri) == i || uriMatcherIds.match(uri) == i) {

                // table uri found
                uriFound = true;
                Distribution.setFlagValue("uriFound", uriFound.toString());

                // inserting values
                try {
                    long rowID = appDataBase.insertOrThrow(Tables.values()[i].name(), "", values);

                    // ---if added successfully---
                    if (rowID > 0) {
                        Uri _uri = ContentUris.withAppendedId(DbScheme.uriArray[i], rowID);
                        getContext().getContentResolver().notifyChange(_uri, null);
                        return _uri;
                    }
                    // if adding failed
                    else {

                        // making and logging exception
                        Distribution.setFlagValue("rowID", String.valueOf(rowID));
                        Distribution.enableLogging();
                        DbDataProviderException dbDataProviderException = new DbDataProviderException(
                                "Adding of row failed");
                        dbDataProviderException.logHandled();
                    }

                }
                // adding failed
                catch (SQLException e) {

                    // logging exception
                    DbDataProviderException dbDataProviderException = new DbDataProviderException(
                            "Adding of row failed", e);
                    dbDataProviderException.logHandled();
                }
            }
        }

        // uri not found
        if (!uriFound) {

            // making and logging exception
            DbDataProviderException dbDataProviderException = new DbDataProviderException(
                    "Inserted uri not found");
            dbDataProviderException.logHandled();
            // throw new IllegalArgumentException("Unsupported URI: " + uri); //
            // TODO: remove this line
        }

        // throw new SQLException("Failed to insert row into " + uri +
        // ", values = " + valuesStr.toString()); // TODO investigate real error
        // for samsung galaxy sII

        // TODO: refactor method after diagnostic

        return DbScheme.uriArray[Tables.NOTIFICATION_YOUR_MOVE.ordinal()];
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        boolean found = false;
        for (int i = 0; i < Tables.values().length; i++) {
            if (uriMatcher.match(uri) == i) {
                count = appDataBase.delete(Tables.values()[i].name(), selection, selectionArgs);
                found = true;
            } else if (uriMatcherIds.match(uri) == i) {
                String id = uri.getPathSegments().get(1);
                count = appDataBase.delete(Tables.values()[i].name(), DbScheme._ID + EQUALS + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                found = true;
            }

            if (found) {
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
        }
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    /**
     * Retrieve version of DB to sync data, and exclude null data request from
     * DB
     * 
     * @return DATABASE_VERSION integer value
     */
    public static int getDbVersion() {
        return DbScheme.DATABASE_VERSION;
    }

    private SQLiteDatabase appDataBase;

    public SQLiteDatabase getDbHandle() {
        return appDataBase;
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    // private static class DatabaseHelper extends SQLiteOpenHelper {
    public static class DatabaseHelper extends SQLiteOpenHelper { // TODO
                                                                  // restore
        private Context context;

        DatabaseHelper(Context context) {
            super(context, DbScheme.DATABASE_NAME, null, DbScheme.DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Init static tables first
            DbScheme dbScheme = new DbScheme();
            dbScheme.createMainTables();
            dbScheme.createUserStatsTables();
            dbScheme.createGameStatsTables();
            dbScheme.createNotificationsTables();
            dbScheme.createThemesTables();

            for (String createTableCall : dbScheme.createTablesArray) {
                db.execSQL(createTableCall);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // new AppData(context).clearPreferences(); // clear all values, to
            // avoid class cast exceptions

            Log.w("Content provider database", "Upgrading database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data");
            // TODO handle backup data
            for (int i = 0; i < Tables.values().length; i++) {
                db.execSQL("DROP TABLE IF EXISTS " + Tables.values()[i]);
            }

            onCreate(db);
        }
    }
}
