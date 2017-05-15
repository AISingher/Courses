package com.example.android.courses;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.courses.CourseContract.CourseEntry;


/** adapter for list item view to display course data using a cursor*/
public class CourseCursorAdapter extends CursorAdapter {

    public CourseCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override //new blank list item view
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override //binds course data to list item layout via cursor
    public void bindView(View view, Context context, Cursor cursor) {
        // Find views to populate in inflated template
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView= (TextView) view.findViewById(R.id.summary);
        // Extract properties from cursor
        String courseName = cursor.getString(cursor.getColumnIndexOrThrow(CourseEntry.COLUMN_COURSE_NAME));
        String courseSummary = cursor.getString(cursor.getColumnIndexOrThrow(CourseEntry.COLUMN_COURSE_ROOM));

        // Populate fields with extracted properties
        nameTextView.setText(courseName);
        summaryTextView.setText(courseSummary);
    }
}