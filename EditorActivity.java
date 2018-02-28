/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.fengzi113.habit;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fengzi113.habit.data.HabitContract;

@SuppressWarnings("ALL")
public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXITED_LOADER = 0;
    private EditText mThingEditText;
    private EditText mDurationEditText;
    private Uri mCurrentUri;

    private static final String TAG = "EditorActivity";
    private boolean mHabitHasChanged = false;

    //    表格监控
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mHabitHasChanged = true;
            return false;
        }
    };

    //    返回监控
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mHabitHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    //    退出确认
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_exit);
        builder.setPositiveButton(R.string.bt_discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.bt_keep, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //    删除确认
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("DELETE ?");

        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePet() {

        int rowsDeleted = getContentResolver().
                delete(mCurrentUri, null, null);
        // TODO: Implement this method
        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, R.string.delete_successful, Toast.LENGTH_SHORT).show();
        }
        finish();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent mIntent = getIntent();
        mCurrentUri = mIntent.getData();

        if (mCurrentUri == null) {

            setTitle(getString(R.string.title_add));
            invalidateOptionsMenu();

        } else {
            setTitle(getString(R.string.title_edit));
            getLoaderManager().initLoader(EXITED_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mThingEditText = (EditText) findViewById(R.id.tv_thing);
        mDurationEditText = (EditText) findViewById(R.id.tv_duration);

        mThingEditText.setOnTouchListener(mOnTouchListener);
        mDurationEditText.setOnTouchListener(mOnTouchListener);

        Log.v(TAG, "======== onCreate initLoader ========");

    }


    //    隐藏心新习惯删除菜单选项
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_del);
            menuItem.setVisible(false);

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    private void savePet() {

        Log.v(TAG, "======== savePet ========");

        String thingString = mThingEditText.getText().toString().trim();
        String durationString = mDurationEditText.getText().toString().trim();

        if (mCurrentUri == null &&
                TextUtils.isEmpty(durationString)) {
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationString);
        } catch (NumberFormatException e) {
            duration = 0;
        }

        ContentValues values = new ContentValues();
        values.put(HabitContract.HabitEntry.COLUMN_HABIT_THING, thingString);
        values.put(HabitContract.HabitEntry.COLUMN_HABIT_DURATION, duration);

        // 新宠物
        if (mCurrentUri == null) {

            Uri newUri = getContentResolver().insert(HabitContract.HabitEntry.CONTENT_URI, values);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.insert_failed,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.insert_successful,
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            // 更新现有宠物
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, R.string.update_failed,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.update_successful,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Do nothing for now
                savePet();
                finish();
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_del:
                // Do nothing for now
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mHabitHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                HabitContract.HabitEntry._ID,
                HabitContract.HabitEntry.COLUMN_HABIT_THING,
                HabitContract.HabitEntry.COLUMN_HABIT_DURATION
        };

        Log.v(TAG, "======== onCreateLoader ========");

        return new CursorLoader(this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        Log.v(TAG, "======== onLoadFinished ========");

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {

            // Find the columns of pet attributes that we're interested in
            int thingColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_THING);
            int durationColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_DURATION);

            // Extract out the value from the Cursor for the given column index
            String thing = cursor.getString(thingColumnIndex);
            int duration = cursor.getInt(durationColumnIndex);

            // Update the views on the screen with the values from the database
            mThingEditText.setText(thing);
            mDurationEditText.setText(Integer.toString(duration));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.v(TAG, "======== onLoaderReset ========");
        // If the loader is invalidated, clear out all the data from the input fields.
        mThingEditText.setText("");
        mDurationEditText.setText("");
    }
}