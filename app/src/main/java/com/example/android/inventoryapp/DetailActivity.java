package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER=0;
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
    ImageButton subtractBook;
    @BindView(R.id.plus_button)
    ImageButton addBook;
    @BindView(R.id.order_button)
    FloatingActionButton callSupplier;
    int quantity=0;
    double price=0.0;
    private Uri currentUri;
    private boolean changedProduct=false;
    private View.OnTouchListener touchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            changedProduct=true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent=getIntent();
        currentUri=intent.getData();

        if (currentUri == null) {
            setTitle(getString(R.string.add_book));
        } else {
            setTitle(getString(R.string.edit_book));
            getLoaderManager().initLoader(BOOK_LOADER, null, this);
        }

        titleEditText.setOnTouchListener(touchListener);
        authorEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);
        supplierEditText.setOnTouchListener(touchListener);
        phoneEditText.setOnTouchListener(touchListener);
        subtractBook.setOnTouchListener(touchListener);
        addBook.setOnTouchListener(touchListener);

        subtractOne();
        addOne();
        orderBook();
    }

    private void orderBook() {
        callSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneString=phoneEditText.getText().toString().trim();
                if (TextUtils.isEmpty(phoneString)) {
                    Toast.makeText(DetailActivity.this, R.string.phone_error, Toast.LENGTH_SHORT).show();
                } else {
                    Intent call=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneString));
                    startActivity(call);
                }
            }
        });
    }

    private void changeQuantity(int amount) {
        String qtyString=quantityEditText.getText().toString().trim();
        if (qtyString.equals("")) {
            quantity=0;
        } else {
            quantity=Integer.parseInt(qtyString);
        }
        if (quantity + amount >= 0) {
            quantity+=amount;
            quantityEditText.setText(String.valueOf(quantity));
            changedProduct=true;
        } else {
            Toast.makeText(this, R.string.negative_qty, Toast.LENGTH_SHORT).show();
        }
    }

    private void addOne() {
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeQuantity(1);
            }
        });
    }

    private void subtractOne() {
        subtractBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeQuantity(-1);
            }
        });
    }

    private boolean saveBook() {

        String titleString=titleEditText.getText().toString().trim();
        String authorString=authorEditText.getText().toString().trim();
        String priceString=priceEditText.getText().toString().trim();
        String quantityString=quantityEditText.getText().toString().trim();
        String supplierString=supplierEditText.getText().toString().trim();
        String phoneString=phoneEditText.getText().toString().trim();

        if (currentUri == null && TextUtils.isEmpty(titleString) &&
                TextUtils.isEmpty(authorString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(supplierString) &&
                TextUtils.isEmpty(phoneString)) {
            Toast.makeText(this, R.string.no_changes, Toast.LENGTH_SHORT).show();
            NavUtils.navigateUpFromSameTask(DetailActivity.this);
            return false;
        }
        ContentValues values=new ContentValues();

        if (TextUtils.isEmpty(titleString)) {
            Toast.makeText(this, getString(R.string.title_error),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            values.put(InventoryEntry.COLUMN_PRODUCT, titleString);
        }

        if (TextUtils.isEmpty(authorString)) {
            Toast.makeText(this, getString(R.string.author_error),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            values.put(InventoryEntry.COLUMN_AUTHOR, authorString);
        }

        if (!TextUtils.isEmpty(priceString)) {
            price=Double.parseDouble(priceString);
        }
        values.put(InventoryEntry.COLUMN_PRICE, price);

        if (!TextUtils.isEmpty(quantityString)) {
            quantity=Integer.parseInt(quantityString);
        }
        values.put(InventoryEntry.COLUMN_QUANTITY, quantity);

        if (TextUtils.isEmpty(supplierString)) {
            Toast.makeText(this, getString(R.string.supplier_error),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierString);
        }

        if (TextUtils.isEmpty(phoneString)) {
            Toast.makeText(this, getString(R.string.phone_error),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, phoneString);
        }

        if (currentUri == null) {
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
        } else {
            int rowsAffected=getContentResolver().update(currentUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.updating_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.product_updated),
                        Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentUri == null) {
            MenuItem menuItem=menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (saveBook()) {
                    finish();
                }
                return true;
            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!changedProduct) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener=
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!changedProduct) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener=
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT,
                InventoryEntry.COLUMN_AUTHOR,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE};

        return new CursorLoader(this,
                currentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            String bookTitle=cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT));
            String bookAuthor=cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_AUTHOR));
            Double bookPrice=cursor.getDouble(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRICE));
            String bookQuantity=cursor.getString((cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_QUANTITY)));
            String bookSupplier=cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_SUPPLIER_NAME));
            String supplierPhone=cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_SUPPLIER_PHONE));

            titleEditText.setText(bookTitle);
            authorEditText.setText(bookAuthor);
            priceEditText.setText(Double.toString(bookPrice));
            quantityEditText.setText(bookQuantity);
            supplierEditText.setText(bookSupplier);
            phoneEditText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        titleEditText.setText("");
        titleEditText.setText("");
        authorEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierEditText.setText("");
        phoneEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (currentUri != null) {
            int rowsDeleted=getContentResolver().delete(currentUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.deleting_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.product_deleted),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
