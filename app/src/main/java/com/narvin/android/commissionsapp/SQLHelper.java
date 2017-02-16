package com.narvin.android.commissionsapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Commissions SQLite Database
 */
public class SQLHelper {

    //Column names
    private static final String KEY_ID = CommissionContract.CommissionEntry.KEY_ID;
    private static final String TYPE = CommissionContract.CommissionEntry.TYPE;
    private static final String COMMISSION_NAME = CommissionContract.CommissionEntry.COMMISSION_NAME;
    private static final String COMMISSION_VALUE = CommissionContract.CommissionEntry.COMMISSION_VALUE;
    private static final String QUANTITY = CommissionContract.CommissionEntry.QUANTITY;

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

    static class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context) {
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

