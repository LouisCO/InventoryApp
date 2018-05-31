package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER=0;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.empty_view)
    View emptyView;
    private InventoryCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        ButterKnife.bind(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InventoryActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        listView.setEmptyView(emptyView);
        cursorAdapter=new InventoryCursorAdapter(this, null);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent=new Intent(InventoryActivity.this, DetailActivity.class);
                Uri currentUri=ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT,
                InventoryEntry.COLUMN_AUTHOR,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
        };
        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
