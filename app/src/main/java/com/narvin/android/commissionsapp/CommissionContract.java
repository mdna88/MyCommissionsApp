package com.narvin.android.commissionsapp;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by michaeldnarvaez on 2/9/17.
 */

public class CommissionContract {

    public CommissionContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider
     */
    public static final String CONTENT_AUTHORITY = "com.narvin.android.commissionsapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    public static final String PATH_COMMISSIONS = "commissions";

    /**
     * Inner class that defines constant values for the items database table.
     */
    public static final class CommissionEntry implements BaseColumns {

        /**
         * The content URI to access the pet data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_COMMISSIONS);

        /**
         * Name of database table for pets
         */
        public static final String TABLE_NAME = "commissions";

        /**
         * Unique ID number for the pet (only for use in the database table).
         */
        public static final String KEY_ID = BaseColumns._ID;

        /**
         * Name of the commission item
         */
        public static final String COMMISSION_NAME = "name";

        /**
         * Value for commission item
         */
        public static final String COMMISSION_VALUE = "value";

        /**
         * Value for commission item
         */
        public static final String QUANTITY = "quantity";

        /**
         * Commission compensation type
         */
        public static final String TYPE = "type";

        /**
         * Possible values for the commission type
         */
        public static final int FIXED_TYPE = 0;
        public static final int PERCENTAGE_TYPE = 1;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMMISSIONS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMMISSIONS;
    }
}
