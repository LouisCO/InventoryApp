package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER=0;
    Uri currentUri;

    @BindView(R.id.edit_title)
    EditText titleEditText;
    @BindView(R.id.edit_author)
    EditText authorEditText;
    @BindView(R.id.edit_price)
    EditText priceEditText;
    @BindView(R.id.edit_quantity)
    EditText quantityEditText;
    @BindView(R.id.edit_supplier)
    EditText supplierEditText;
    @BindView(R.id.edit_phone)
    EditText phoneEditText;
    @BindView(R.id.minus_button)
    Button subtractBook;
    @BindView(R.id.plus_button)
    Button addBook;
    @BindView(R.id.order_button)
    FloatingActionButton callSupplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent=getIntent();
        currentUri=intent.getData();

        if (currentUri == null) {
            setTitle("Add a book");
        } else {
            setTitle("Edit book");
        }
    }

    private void saveBook() {

        String titleString=titleEditText.getText().toString().trim();
        String priceString=priceEditText.getText().toString().trim();
        Double price=Double.parseDouble(priceString);
        String quantityString=quantityEditText.getText().toString().trim();
        int quantity=Integer.parseInt(quantityString);
        String supplierString=supplierEditText.getText().toString().trim();
        String phoneString=phoneEditText.getText().toString().trim();

        ContentValues values=new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT, titleString);
        values.put(InventoryEntry.COLUMN_PRICE, price);
        values.put(InventoryEntry.COLUMN_QUANTITY, quantity);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, phoneString);

        Uri newUri=getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.saving_error),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.product_saved),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveBook();
                finish();
                return true;
            case R.id.delete:
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
