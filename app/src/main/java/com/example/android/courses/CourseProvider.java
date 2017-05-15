package com.example.android.courses;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class CourseProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = CourseProvider.class.getSimpleName();

    private CourseDbHelper mDbHelper;//database helper object

    /** URI matcher code for the content URI for the course table */
    private static final int COURSES=100;
    /** URI matcher code for the content URI for a single course in the courses table */
    private static final int COURSES_ID=101;


    /**
     * UriMatcher object to match a content URI to a corresponding code.  */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    //static initializer
    static{//add URI's
        sUriMatcher.addURI(CourseContract.CONTENT_AUTHORITY, CourseContract.PATH_COURSES,COURSES);//all rows of courses
        sUriMatcher.addURI(CourseContract.CONTENT_AUTHORITY, CourseContract.PATH_COURSES + "/#",COURSES_ID);//single row id of course
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new CourseDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match=sUriMatcher.match(uri);

        switch(match){
            case COURSES:
                cursor=database.query(CourseContract.CourseEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case COURSES_ID:
                selection= CourseContract.CourseEntry._ID +"=?";//where
                selectionArgs=new String[]{
                        String.valueOf(ContentUris.parseId(uri))};//fill in ?
                cursor=database.query(CourseContract.CourseEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);//run query
                break;
            default:
            throw new IllegalArgumentException("Cannot query unknown uri"+uri);
        }

        //uri data changes then can use this to know to update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match=sUriMatcher.match(uri);
        switch(match){
            case COURSES:
                return insertCourse(uri,contentValues);
            default:
                throw new IllegalArgumentException("Cannot insert unknown uri"+uri);
        }
    }

    private Uri insertCourse(Uri uri, ContentValues values){//insert helper method

        /**Check error cases for user input*/
        String name = values.getAsString(CourseContract.CourseEntry.COLUMN_COURSE_NAME);

        if (name == null) {//null course name
            throw new IllegalArgumentException("Course requires a name");
        }

        /**insert to database*/
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Insert the new course with the given values
        long id=db.insert(CourseContract.CourseEntry.TABLE_NAME, null, values);

        if(id==-1){//insert failed
            Log.e(LOG_TAG,"Failed to insert for"+uri);
            return null;
        }

        //notify listeners that data has changed for course content uri
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id); //new uri with id of newly inserted row
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COURSES:
                return updateCourse(uri, contentValues, selection, selectionArgs);
            case COURSES_ID:
                selection = CourseContract.CourseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateCourse(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot update unknown uri" + uri);
        }
    }

    private int updateCourse(Uri uri, ContentValues values, String selection, String[] selectionArgs) {//update helper method

        /**same error test cases as in insert*/
        if (values.containsKey(CourseContract.CourseEntry.COLUMN_COURSE_NAME)) {
            String name = values.getAsString(CourseContract.CourseEntry.COLUMN_COURSE_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Course requires a name");
            }
        }

        // If there are no values to update
        if (values.size() == 0) {
            return 0;
        }

        /**update to database*/

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated=db.update(CourseContract.CourseEntry.TABLE_NAME,values,selection,selectionArgs);

        // If >1 rows updated, notify listeners that data has changed for course content uri
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COURSES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(CourseContract.CourseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COURSES_ID:
                // Delete a single row given by the ID in the URI
                selection = CourseContract.CourseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(CourseContract.CourseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete unknown uri " + uri);
        }
        // If >1 rows updated, notify listeners that data has changed for course content uri
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
            // Return the number of rows deleted
            return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COURSES:
                return CourseContract.CourseEntry.CONTENT_LIST_TYPE;
            case COURSES_ID:
                return CourseContract.CourseEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}