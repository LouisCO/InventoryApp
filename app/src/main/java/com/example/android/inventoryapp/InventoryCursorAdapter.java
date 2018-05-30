package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryCursorAdapter extends CursorAdapter {

    @BindView(R.id.book_title)
    TextView title;
    @BindView(R.id.book_author)
    TextView author;
    @BindView(R.id.book_price)
    TextView price;
    @BindView(R.id.book_quantity)
    TextView quantity;
    @BindView(R.id.sale_button)
    ImageButton sale;

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        ButterKnife.bind(this, view);

        final int id=cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        String bookTitle=cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT));
        String bookAuthor=cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_AUTHOR));
        Double bookPrice=cursor.getDouble(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRICE));
        String bookQuantity=cursor.getString((cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_QUANTITY)));
        final int bookQty=Integer.parseInt(bookQuantity);

        title.setText(bookTitle);
        author.setText(bookAuthor);
        price.setText(bookPrice.toString());
        quantity.setText(bookQuantity);

        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookQty > 0) {
                    ContentValues values=new ContentValues();
                    Uri uri=ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                    values.put(InventoryEntry.COLUMN_QUANTITY, bookQty - 1);
                    view.getContext().getContentResolver().update(uri, values, null,
                            null);
                } else
                    Toast.makeText(context, R.string.out_of_stock,
                            Toast.LENGTH_SHORT).show();
            }
        });
    }
}
