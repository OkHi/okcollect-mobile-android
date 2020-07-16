package io.okcollect.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by ramogiochola on 6/18/16.
 */

public class DataProvider {
    private static final String TAG = "DataProvider";

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public DataProvider(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void openRead() throws SQLException {
        database = dbHelper.getReadableDatabase();
    }

    public void openWrite() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertProperty(String propname, String value) {
        long insertId = 0;
        try {
            try {
                openWrite();
            } catch (Exception e) {
            }

            ContentValues values = new ContentValues();
            values.put(io.okcollect.android.utilities.Constants.COLUMN_PROPERTY, propname);
            values.put(io.okcollect.android.utilities.Constants.COLUMN_VALUE, value);

            insertId = database.insert(io.okcollect.android.utilities.Constants.TABLE_NAME_PROPERTY, null, values);

        } catch (SQLException sqle) {
        } finally {
            close();
        }
        return insertId;
    }

    public String getPropertyValue(String propertyname) {

        String value = "";
        Cursor cursor;

        String[] columns = {io.okcollect.android.utilities.Constants.COLUMN_VALUE};
        String selection = io.okcollect.android.utilities.Constants.COLUMN_PROPERTY + " = ? ";
        String[] selectionArgs = {"" + propertyname};

        try {
            try {
                openRead();
            } catch (Exception e) {
            }

            cursor = database.query(io.okcollect.android.utilities.Constants.TABLE_NAME_PROPERTY, columns, selection, selectionArgs, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                value = cursor.getString(0);
                cursor.moveToNext();
            }

            cursor.close();

        } catch (SQLException sqle) {
        } finally {
            close();
        }
        return value;
    }
}
