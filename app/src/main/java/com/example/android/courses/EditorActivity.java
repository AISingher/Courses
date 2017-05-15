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
package com.example.android.courses;

import android.app.AlertDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.courses.CourseContract.CourseEntry;

/**
 * Allows user to create a new course or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Content URI for the existing course (null if it's a new course) */
    private Uri mCurrentCourseUri;

    private EditText mNameEditText,mRoomEditText,mTeacherEditText,mTimeEditText,mDayEditText;

    private boolean mCourseHasChanged = false;


    /**listener for user touching view to edit*/
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mCourseHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent to figure out if we're creating a new course or editing an existing one.
        Intent intent = getIntent();
        mCurrentCourseUri = intent.getData();

        if (mCurrentCourseUri == null) {
            // app bar = "Add a Course"
            setTitle("Add A Course");

            // Invalidate the options menu, so the "Delete" option is gone, deleting while inserting?
            invalidateOptionsMenu();
        }
        else { //otherwise existing course
            // app bar = "Edit Course"
            setTitle("Edit Course");

            // Initialize a loader to read the course data from the database and display the current values in the editor
            getLoaderManager().initLoader(0, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_course_name);
        mRoomEditText = (EditText) findViewById(R.id.edit_course_room);
        mTeacherEditText = (EditText) findViewById(R.id.edit_course_teacher);
        mTimeEditText = (EditText) findViewById(R.id.edit_course_time);
        mDayEditText = (EditText) findViewById(R.id.edit_course_day);

        //listeners to see if user touched/modified input
        mNameEditText.setOnTouchListener(mTouchListener);
        mRoomEditText.setOnTouchListener(mTouchListener);
        mTeacherEditText.setOnTouchListener(mTouchListener);
        mTimeEditText.setOnTouchListener(mTouchListener);
        mDayEditText.setOnTouchListener(mTouchListener);
    }

    // user input from editor, give to CourseProvider to save course into database
    private void saveCourse(){
        String nameString=mNameEditText.getText().toString().trim();
        String roomString=mRoomEditText.getText().toString().trim();
        String teacherString=mTeacherEditText.getText().toString().trim();
        String timeString=mTimeEditText.getText().toString().trim();
        String dayString=mDayEditText.getText().toString().trim();

        //if all the fields in the editor are blank, return without creating a new course.
        if (mCurrentCourseUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(roomString) &&
                TextUtils.isEmpty(teacherString) && TextUtils.isEmpty(timeString) && TextUtils.isEmpty(dayString)){
            return;
        }

        ContentValues values = new ContentValues();

        values.put(CourseEntry.COLUMN_COURSE_NAME, nameString);
        values.put(CourseEntry.COLUMN_COURSE_ROOM, roomString);
        values.put(CourseEntry.COLUMN_COURSE_TEACHER, teacherString);
        values.put(CourseEntry.COLUMN_COURSE_TIME, timeString);
        values.put(CourseEntry.COLUMN_COURSE_DAY, dayString);

        // Determine if this is a new or existing course by checking if mCurrentPetUri is null or not
        if (mCurrentCourseUri == null) {
            // new course, insert into provider
            Uri newUri = getContentResolver().insert(CourseEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Insert course failed", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Insert course successful", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            // Otherwise existing course, update with mCurrentPetUri and pass in new ContentValues.
            int rowsAffected = getContentResolver().update(mCurrentCourseUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Update course failed", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Update course successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    //called for invalidateOptionsMenu() to update menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new course, hide the "Delete" menu item.
        if (mCurrentCourseUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveCourse();//save new course to database
                finish();//exit activity
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the course hasn't changed, continue with navigating up to CatalogActivity
                if (!mCourseHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the course hasn't changed, continue with handling back button press
        if (!mCourseHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // projection for editor inputs
        String[] projection = {
                CourseEntry._ID,
                CourseEntry.COLUMN_COURSE_NAME,
                CourseEntry.COLUMN_COURSE_ROOM,
                CourseEntry.COLUMN_COURSE_TEACHER,
                CourseEntry.COLUMN_COURSE_TIME,
                CourseEntry.COLUMN_COURSE_DAY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, mCurrentCourseUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // cursor is null or less than 1 row
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        //get data from cursor row, only 1 row
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_NAME));
            String room = cursor.getString(cursor.getColumnIndex( CourseEntry.COLUMN_COURSE_ROOM));
            String teacher = cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_TEACHER));
            String time = cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_TIME));
            String day = cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_DAY));

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mRoomEditText.setText(room);
            mTeacherEditText.setText(teacher);
            mTimeEditText.setText(time);
            mDayEditText.setText(day);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mRoomEditText.setText("");
        mTeacherEditText.setText("");
        mTimeEditText.setText("");
        mDayEditText.setText("");

    }

    /**dialog warning user unsaved changes if leaving editor*/
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
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

    /** confirm deletion of course*/
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteCourse();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

    /**delete course in database*/
    private void deleteCourse() {
        // Only perform the delete if this is an existing course.
        if (mCurrentCourseUri != null) {
            // Call the ContentResolver to delete the course at the given content URI.
            int rowsDeleted = getContentResolver().delete(mCurrentCourseUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Delete course failed", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Delete course successful", Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }
}