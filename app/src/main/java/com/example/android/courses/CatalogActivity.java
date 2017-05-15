package com.example.android.courses;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.courses.CourseContract.CourseEntry;


/**
 * Displays list of courses that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    CourseCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        /**empty view setup*/
        // Find the ListView which will be populated with the course data
        ListView courseListView = (ListView) findViewById(R.id.list);
        // Find and set empty view on the ListView
        View emptyView = findViewById(R.id.empty_view);
        courseListView.setEmptyView(emptyView);

        /**list item (normal) view setup*/
        //setup adapter to create list item for each row of course data in Cursor.
        mCursorAdapter = new CourseCursorAdapter(this, null);//null b/c no course data yet
        // Attach cursor adapter to the ListView
        courseListView.setAdapter(mCursorAdapter);

        /**Setup the item click listener*/
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific course with id that was clicked on,
                Uri currentPetUri = ContentUris.withAppendedId(CourseEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                startActivity(intent);
            }
        });

        // start loader
        getLoaderManager().initLoader(0, null, this);
    }

    private void deleteAllCourses(){
        int rowsDeleted = getContentResolver().delete(CourseEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from course database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on delete menu option
        if(item.getItemId()==R.id.action_delete_all_entries){
                deleteAllCourses();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    // Called when a new Loader needs to be created
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                CourseEntry._ID,
                CourseEntry.COLUMN_COURSE_NAME,
                CourseEntry.COLUMN_COURSE_ROOM,
        };
        //create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, CourseEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.
        mCursorAdapter.swapCursor(data);
    }

    @Override
    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.
        mCursorAdapter.swapCursor(null);
    }
}