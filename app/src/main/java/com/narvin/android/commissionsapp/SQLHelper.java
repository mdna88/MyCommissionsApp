package com.narvin.android.commissionsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.id;

/**
 * Created by michaeldnarvaez on 7/4/16.
 */
public class SQLHelper {

    //Column names
    public static final String KEY_ID = CommissionContract.CommissionEntry.KEY_ID;
    public static final String TYPE = CommissionContract.CommissionEntry.TYPE;
    public static final String COMMISSION_NAME = CommissionContract.CommissionEntry.COMMISSION_NAME;
    public static final String COMMISSION_VALUE = CommissionContract.CommissionEntry.COMMISSION_VALUE;
    public static final String QUANTITY = CommissionContract.CommissionEntry.QUANTITY;

    //Data info
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sales";
    private static final String TABLE_NAME = CommissionContract.CommissionEntry.TABLE_NAME;

    private static final String CREATE_COMMISSIONS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY, " + TYPE + " TEXT, " + COMMISSION_NAME + " TEXT, "
            + COMMISSION_VALUE + " REAL, " + QUANTITY + " INTEGER" + ")";

    private DBHelper mDbHelper;
    private SQLiteDatabase myDb;

    public SQLHelper(Context context) {

        mDbHelper = new DBHelper(context);
        myDb = mDbHelper.getWritableDatabase();
    }

    public String getQuantity(int id) {

        Cursor cursor = myDb.rawQuery("SELECT " + QUANTITY + " FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = ? ",
        new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        String quantity = cursor.getString(cursor.getColumnIndex(QUANTITY));
        cursor.close();

        return quantity;
    }

    // Delete one item record
    public Integer deleteItem(String id) {

        return myDb.delete(TABLE_NAME, KEY_ID + " = ?", new String[]{id});

    }

    public void deleteAllEntries() {
        myDb.delete(TABLE_NAME, null, null);
    }

    public void clearQuantity(String id) {
        myDb.execSQL("UPDATE " + TABLE_NAME + " SET " + QUANTITY + "= 0 " + " WHERE " + KEY_ID + " = ? ",
                new String[]{String.valueOf(id)});

    }

    public void clearAllQuantity() {
        myDb.execSQL("UPDATE " + TABLE_NAME + " SET " + QUANTITY + "= 0 ");

    }

    public static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_COMMISSIONS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

}

