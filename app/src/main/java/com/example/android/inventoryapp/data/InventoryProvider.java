package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;


public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG=InventoryProvider.class.getSimpleName();
    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS=100;
    /**
     * URI matcher code for the content URI for a single book in the table
     */
    private static final int BOOK_ID=101;
    private static final UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    private InventoryDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper=new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase stock=dbHelper.getReadableDatabase();

        Cursor cursor;

        int match=sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor=stock.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                selection=InventoryEntry._ID + "=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor=stock.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match=sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final int match=sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                if (values != null) {
                    return insertBook(uri, values);
                }
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        String title=values.getAsString(InventoryEntry.COLUMN_PRODUCT);
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Enter title");
        }

        String author=values.getAsString(InventoryEntry.COLUMN_AUTHOR);
        if (author == null || author.isEmpty()) {
            throw new IllegalArgumentException("Enter author");
        }

        Double price=values.getAsDouble(InventoryEntry.COLUMN_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Enter valid price");
        }

        Integer quantity=values.getAsInteger(InventoryEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Enter valid quantity");
        }

        String supplier=values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME);
        if (supplier == null || supplier.isEmpty()) {
            throw new IllegalArgumentException("Enter supplier's name");
        }

        String phone=values.getAsString(InventoryEntry.COLUMN_SUPPLIER_PHONE);
        if (phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException("Enter supplier's phone number");
        }

        SQLiteDatabase stock=dbHelper.getWritableDatabase();

        long id=stock.insert(InventoryEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase stock=dbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match=sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                rowsDeleted=stock.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection=InventoryEntry._ID + "=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted=stock.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int match=sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                if (values != null) {
                    return updateBook(uri, values, selection, selectionArgs);
                }
            case BOOK_ID:
                selection=InventoryEntry._ID + "=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                if (values != null) {
                    return updateBook(uri, values, selection, selectionArgs);
                }
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT)) {
            String title=values.getAsString(InventoryEntry.COLUMN_PRODUCT);
            if (title == null || title.isEmpty()) {
                throw new IllegalArgumentException("Enter title");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_AUTHOR)) {
            String author=values.getAsString(InventoryEntry.COLUMN_AUTHOR);
            if (author == null || author.isEmpty()) {
                throw new IllegalArgumentException("Enter author");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_PRICE)) {
            Double price=values.getAsDouble(InventoryEntry.COLUMN_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Enter valid price");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_QUANTITY)) {
            Integer quantity=values.getAsInteger(InventoryEntry.COLUMN_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Enter valid quantity");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER_NAME)) {
            String supplier=values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME);
            if (supplier == null || supplier.isEmpty()) {
                throw new IllegalArgumentException("Enter supplier's name");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER_PHONE)) {
            String phone=values.getAsString(InventoryEntry.COLUMN_SUPPLIER_PHONE);
            if (phone == null || phone.isEmpty()) {
                throw new IllegalArgumentException("Enter supplier's phone number");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase stock=dbHelper.getWritableDatabase();

        int rowsUpdated=stock.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
