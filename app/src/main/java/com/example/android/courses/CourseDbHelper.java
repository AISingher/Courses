package com.example.android.courses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CourseDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME= "schedule.db";

    public CourseDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES=
                "CREATE TABLE " + CourseContract.CourseEntry.TABLE_NAME + "(" +
                        CourseContract.CourseEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        CourseContract.CourseEntry.COLUMN_COURSE_NAME+" TEXT NOT NULL,"+
                        CourseContract.CourseEntry.COLUMN_COURSE_ROOM+" TEXT NOT NULL,"+
                        CourseContract.CourseEntry.COLUMN_COURSE_TEACHER+" TEXT NOT NULL,"+
                        CourseContract.CourseEntry.COLUMN_COURSE_TIME+" TEXT NOT NULL,"+
                        CourseContract.CourseEntry.COLUMN_COURSE_DAY+" TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_ENTRIES=
                "DROP TABLE IF EXISTS " + CourseContract.CourseEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
