package com.narvin.android.commissionsapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.narvin.android.commissionsapp.CommissionContract.CommissionEntry.TABLE_NAME;

/**
 * Created by michaeldnarvaez on 2/9/17.
 */
public class CommissionsProvider extends ContentProvider {

    //DataBaseHelper Object
    private SQLHelper.DBHelper mDBHelper;

    //Uri matcher integers
    private static final  int COMMISSIONS = 100;
    private static final  int COMMISSIONS_ID = 101;

    //Uri Matcher Object
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Static initializer, Content Uri Patterns
    static {

        //Pattern for the whole database
        sUriMatcher.addURI(CommissionContract.CONTENT_AUTHORITY,
                CommissionContract.PATH_COMMISSIONS, COMMISSIONS);

        //Pattern for one row from the database
        sUriMatcher.addURI(CommissionContract.CONTENT_AUTHORITY,
                CommissionContract.PATH_COMMISSIONS + "/#", COMMISSIONS_ID);

    }

    @Override
    public boolean onCreate() {

        //Database helper instantiation
        mDBHelper = new SQLHelper.DBHelper(getContext());
        return true;

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //Get readable SQLite database
        SQLiteDatabase dataBase = mDBHelper.getReadableDatabase();

        //the result of the query wil be stored in this cursor
        Cursor cursor;


        //Checks if the URi passed in matches one of the predetermined patterns
        int match = sUriMatcher.match(uri);

        switch (match) {
            case COMMISSIONS:

                cursor = dataBase.query(TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);

                cursor.setNotificationUri(getContext().getContentResolver(), uri);

                break;

            case COMMISSIONS_ID:

                selection = CommissionContract.CommissionEntry.KEY_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                cursor = dataBase.query(TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);

                cursor.setNotificationUri(getContext().getContentResolver(), uri);

                break;

            default:
                throw new IllegalArgumentException("Cannot query URI " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COMMISSIONS:
                return CommissionContract.CommissionEntry.CONTENT_LIST_TYPE;
            case COMMISSIONS_ID:
                return CommissionContract.CommissionEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COMMISSIONS:
                return insertNewItem(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Inserts new item into database
     */
    private Uri insertNewItem(Uri uri, ContentValues values) {

        // Get writable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        // Insert the new item with the given values
        long id = database.insert(TABLE_NAME, null, values);

        if (id == -1) {
            Log.e("Resolver", "Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COMMISSIONS:

                // Delete all rows that match the selection and selection args
                return database.delete(TABLE_NAME, selection, selectionArgs);

            case COMMISSIONS_ID:
                // Delete a single row given by the ID in the URI
                selection = CommissionContract.CommissionEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return database.delete(TABLE_NAME, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Insert the new item with the given values
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COMMISSIONS:

                return updateItem(uri, values, selection, selectionArgs);

            case COMMISSIONS_ID:

                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = CommissionContract.CommissionEntry.KEY_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                return updateItem(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update items in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more items).
     * Return the number of rows that were successfully updated.
     */
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // check that the name is not null.
        if (values.containsKey(CommissionContract.CommissionEntry.COMMISSION_NAME)) {
            String name = values.getAsString(CommissionContract.CommissionEntry.COMMISSION_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Name required");
            }
        }

        // check that the value is valid.
        if (values.containsKey(CommissionContract.CommissionEntry.COMMISSION_VALUE)) {
            Integer value = values.getAsInteger(CommissionContract.CommissionEntry.COMMISSION_VALUE);
            if (value == null) {
                throw new IllegalArgumentException("Value required");
            }
        }

        // check that the quantity is valid.
        if (values.containsKey(CommissionContract.CommissionEntry.QUANTITY)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer quantity = values.getAsInteger(CommissionContract.CommissionEntry.QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Invalid quantity");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(CommissionContract.CommissionEntry.TABLE_NAME,
                values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}
