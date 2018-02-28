package com.example.fengzi113.habit;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fengzi113.habit.data.HabitContract;

import static com.example.fengzi113.habit.data.HabitContract.HabitEntry.CONTENT_URI;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER = 0;
    HabitCursorAdapter mHabitCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.bt_tianjia);
//        FloatingActionButton fab =  (FloatingActionButton) findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mHabitCursorAdapter = new HabitCursorAdapter(this, null);
        listView.setAdapter(mHabitCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(CONTENT_URI,id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertHabit() {


                ContentValues values = new ContentValues();
                values.put(HabitContract.HabitEntry.COLUMN_HABIT_THING, "跑步");
                values.put(HabitContract.HabitEntry.COLUMN_HABIT_DURATION, "2");

                Uri newUri = getContentResolver().insert(CONTENT_URI, values);

            }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
                        case R.id.action_insert_dummy_data:
                            // Do nothing for now
                            insertHabit();
                            return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteall();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void deleteall(){

        int rowsDeleted = getContentResolver().delete(CONTENT_URI,null,null);
        Toast.makeText(this, R.string.delete_all,Toast.LENGTH_LONG).show();

    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                HabitContract.HabitEntry._ID,
                HabitContract.HabitEntry.COLUMN_HABIT_THING,
                HabitContract.HabitEntry.COLUMN_HABIT_DURATION
        };

        return new CursorLoader(this,
                CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        mHabitCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mHabitCursorAdapter.swapCursor(null);

    }
}