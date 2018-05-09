package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;

public class InventoryActivity extends AppCompatActivity {

    private InventoryDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Setup FAB to insert dummy data
        FloatingActionButton fab=findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                insertData();
                displayStockInfo();
            }
        });

        dbHelper=new InventoryDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayStockInfo();
    }

    private void displayStockInfo() {
        SQLiteDatabase stock=dbHelper.getReadableDatabase();

        String[] projection={
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE
        };

        TextView displayView=findViewById(R.id.text_view_book);

        try (Cursor cursor=stock.query(
                InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        )) {
            displayView.setText(getString(R.string.stock_info, cursor.getCount()));
            displayView.append(InventoryEntry._ID + " - " +
                    InventoryEntry.COLUMN_PRODUCT + " - " +
                    InventoryEntry.COLUMN_PRICE + " - " +
                    InventoryEntry.COLUMN_QUANTITY + " - " +
                    InventoryEntry.COLUMN_SUPPLIER_NAME + " - " +
                    InventoryEntry.COLUMN_SUPPLIER_PHONE + "\n");

            while (cursor.moveToNext()) {

                displayView.append("\n" +
                        cursor.getString(cursor.getColumnIndex(InventoryEntry._ID)) + "\t" +
                        cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT)) + "\t" +
                        cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE)) + "\t\t" +
                        cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY)) + "\t" +
                        cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME)) + "\t" +
                        cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE))
                );
            }
        }

    }

    private void insertData() {

        SQLiteDatabase stock=dbHelper.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT, "Code Complete");
        values.put(InventoryEntry.COLUMN_PRICE, 29.97);
        values.put(InventoryEntry.COLUMN_QUANTITY, 10);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Books Inc.");
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, "555-000-111");

        long newRowId=stock.insert(InventoryEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error with saving product", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product saved with row id: " +
                    newRowId, Toast.LENGTH_SHORT).show();
        }

    }
}
