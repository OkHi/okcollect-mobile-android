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


    public long insertAddressList(ContentValues values) {
        displayLog(" insertAddressList  with values method called");

        long insertId = 0;

        try {
            try {
                openWrite();
            } catch (Exception e) {
                displayLog("insertAddressList openWrite error " + e.toString());
            }
            displayLog("after write before insert");
            insertId = database.insert(io.okcollect.android.utilities.Constants.TABLE_NAME_RUNLIST, null, values);
            displayLog("insertAddressList method executed " + insertId);


        } catch (SQLException sqle) {
            displayLog(" insertAddressList error " + sqle.toString());
        } finally {
            close();
        }
        return insertId;
    }

    private io.okcollect.android.datamodel.AddressItem cursorToAddressListItem(Cursor cursor) {

        io.okcollect.android.datamodel.AddressItem addressItem = new io.okcollect.android.datamodel.AddressItem();

        addressItem.setCustomername(cursor.getString(1));
        addressItem.setAffiliation(cursor.getString(2));
        addressItem.setPhonecustomer(cursor.getString(3));

        addressItem.setAflid(cursor.getString(4));
        addressItem.setUalid(cursor.getString(5));
        addressItem.setUnit(cursor.getString(6));
        addressItem.setDirection(cursor.getString(7));
        addressItem.setRoute(cursor.getString(8));

        addressItem.setPropname(cursor.getString(9));
        addressItem.setPropnumber(cursor.getString(10));
        addressItem.setFloor(cursor.getString(11));
        addressItem.setIsOddress(cursor.getInt(12));
        addressItem.setLat(cursor.getDouble(13));

        addressItem.setLng(cursor.getDouble(14));
        addressItem.setBranch(cursor.getString(15));
        addressItem.setImageurl(cursor.getString(16));
        addressItem.setDeliverynotes(cursor.getString(17));


        addressItem.setLocationNickname(cursor.getString(18));
        addressItem.setTraditionalBuildingName(cursor.getString(19));
        addressItem.setBusinessName(cursor.getString(20));
        addressItem.setTraditionalStreetNumber(cursor.getString(21));
        addressItem.setTraditionalStreetName(cursor.getString(22));

        addressItem.setToTheDoor(cursor.getString(23));
        addressItem.setTraditionalBuildingNumber(cursor.getString(24));
        addressItem.setStreetName(cursor.getString(25));
        addressItem.setStreetNumber(cursor.getString(26));

        addressItem.setCustomeruserid(cursor.getString(27));
        addressItem.setAddressType(cursor.getString(28));
        addressItem.setInternalAddressType(cursor.getString(29));
        addressItem.setIsNewUser(cursor.getString(30));
        addressItem.setIsEmptyUal(cursor.getString(31));
        addressItem.setAcc(cursor.getDouble(32));
        addressItem.setAddressFrequency(cursor.getInt(33));
        addressItem.setCreatedon(cursor.getString(34));
        addressItem.setLastused(cursor.getString(35));
        addressItem.setLocationName(cursor.getString(35));
        addressItem.setUniqueId(cursor.getString(36));

        return addressItem;

    }

    public long insertStuff(String propname, String affiliation) {
        displayLog("insertStuff  method called " + propname + " value " + affiliation);
        long insertId = 0;
        try {
            try {
                openWrite();
            } catch (Exception e) {
                displayLog("insertStuff openWrite error " + e.toString());
            }

            ContentValues values = new ContentValues();
            values.put(io.okcollect.android.utilities.Constants.COLUMN_PROPERTY, propname);
            values.put(io.okcollect.android.utilities.Constants.COLUMN_VALUE, affiliation);

            insertId = database.insert(io.okcollect.android.utilities.Constants.TABLE_NAME_STUFF, null, values);

            displayLog("insertStuff method executed " + insertId);
        } catch (SQLException sqle) {
            displayLog("insertStuff error " + sqle.toString());
        } finally {
            close();
        }
        return insertId;
    }

    public String getPropertyValue(String propertyname) {

        String carupgradedelivered = "";
        Cursor cursor;

        String[] columns = {io.okcollect.android.utilities.Constants.COLUMN_VALUE};
        String selection = io.okcollect.android.utilities.Constants.COLUMN_PROPERTY + " = ? ";
        String[] selectionArgs = {"" + propertyname};

        try {
            try {
                openRead();
            } catch (Exception e) {
                displayLog("getPropertyValue openRead error " + e.toString());
            }

            cursor = database.query(io.okcollect.android.utilities.Constants.TABLE_NAME_STUFF, columns, selection, selectionArgs, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                carupgradedelivered = cursor.getString(0);
                cursor.moveToNext();
            }

            cursor.close();

        } catch (SQLException sqle) {
            displayLog("getPropertyValue(String propertyname) error " + sqle.toString());
        } finally {
            close();
        }

        if ((carupgradedelivered.startsWith("07")) && (carupgradedelivered.length() == 10)) {
            carupgradedelivered = "+2547" + carupgradedelivered.substring(2);
        } else {
            carupgradedelivered = carupgradedelivered;
        }

        return carupgradedelivered;
    }

    private void displayLog(String log) {
        ////Log.i(TAG, log);
    }
}
