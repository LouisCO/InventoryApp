package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {
    private InventoryContract() {
    }

    public static final String CONTENT_AUTHORITY="com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS="books";

    public final static class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        // The MIME type of the {@link #CONTENT_URI} for a inventory list
        public static final String CONTENT_LIST_TYPE=ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        // The MIME type of the {@link #CONTENT_URI} for a single book
        public static final String CONTENT_ITEM_TYPE=
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public final static String TABLE_NAME="books";
        public final static String _ID=BaseColumns._ID;
        public final static String COLUMN_PRODUCT="product";
        public final static String COLUMN_AUTHOR="author";
        public final static String COLUMN_PRICE="price";
        public final static String COLUMN_QUANTITY="quantity";
        public final static String COLUMN_SUPPLIER_NAME="supplier_name";
        public final static String COLUMN_SUPPLIER_PHONE="supplier_phone_number";

    }
}
