package com.example.android.courses;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class CourseContract {
    private CourseContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.courses";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_COURSES = "courses";

    public static class CourseEntry implements BaseColumns {

        /** The content URI to access the course data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_COURSES);

        /**
         * The MIME type for a list of courses.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSES;

        /**
         * The MIME type for a single course.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSES;

        /** Name of database table for courses */
        public static final String TABLE_NAME = "courses";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_COURSE_NAME = "name";
        public static final String COLUMN_COURSE_ROOM = "room";
        public static final String COLUMN_COURSE_TEACHER = "teacher";
        public static final String COLUMN_COURSE_TIME = "time";
        public static final String COLUMN_COURSE_DAY = "day";

    }
}



