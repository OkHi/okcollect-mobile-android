package io.okcollect.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import io.okcollect.android.utilities.Constants;

/**
 * Created by ramogiochola on 6/18/16.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "io.okcollect.android.sdk.database.db";
    private static final int DATABASE_VERSION = 19;

    private static final String DATABASE_CREATE_PROPERTY =
            "create table " + Constants.TABLE_NAME_PROPERTY + " (" +
                    io.okcollect.android.utilities.Constants.COLUMN_ID + " integer primary key autoincrement, " +
                    io.okcollect.android.utilities.Constants.COLUMN_PROPERTY + " VARCHAR NOT NULL UNIQUE, " +
                    io.okcollect.android.utilities.Constants.COLUMN_VALUE + " VARCHAR , " +
                    " UNIQUE(" + io.okcollect.android.utilities.Constants.COLUMN_PROPERTY + ") ON CONFLICT REPLACE);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_PROPERTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_PROPERTY);
        onCreate(db);
    }
}
