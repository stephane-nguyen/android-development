package com.tps.tp4stephanenguyen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.job.JobScheduler;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton addJob;
    private ListView jobListView;
    private ArrayAdapter<String> jobAdapter;
    private Map<Long, Long> itemIdToJobIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Listview
        jobListView = findViewById(R.id.jobListView);
        jobAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        jobListView.setAdapter(jobAdapter);

        handleAddJobButton();
        loadJobTitles();
        onPressItemInListView();
    }

    private void loadJobTitles() {
        List<String> jobTitles = getAllJobTitles();
        List<Long> jobIds = getAllJobIds();

        jobAdapter.clear();
        for (int i = 0; i < jobTitles.size(); i++) {
            String jobTitle = jobTitles.get(i);
            long itemId = i;
            long jobId = jobIds.get(i);
            jobAdapter.add(jobTitle);

            // Store the mapping between itemId and databaseId
            itemIdToJobIdMap.put(itemId, jobId);
        }

        jobAdapter.notifyDataSetChanged();
    }
    public List<String> getAllJobTitles() {
        List<String> jobTitles = new ArrayList<>();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columnToRetrieve = {JobContract.JobEntry.COLUMN_JOB_TITLE};

        Cursor cursor = db.query(
                JobContract.JobEntry.TABLE_NAME,
                columnToRetrieve,
                null,
                null,
                null,
                null,
                null
        );

        // Iterate through the cursor and add job titles to the list
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String jobTitle = cursor.getString(cursor.getColumnIndex(JobContract.JobEntry.COLUMN_JOB_TITLE));
                jobTitles.add(jobTitle);
            }
            cursor.close();
        }

        db.close();

        return jobTitles;
    }

    public List<Long> getAllJobIds() {
        List<Long> jobIds = new ArrayList<>();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {JobContract.JobEntry._ID};

        Cursor cursor = db.query(
                JobContract.JobEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long jobId = cursor.getLong(cursor.getColumnIndex(JobContract.JobEntry._ID));
                jobIds.add(jobId);
            }
            cursor.close();
        }
        db.close();

        return jobIds;
    }


    // Update name dialog
    private void showEditNameDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setText(getUserName());

        new AlertDialog.Builder(this)
            .setTitle("Edit Name")
            .setView(editText)
            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newName = editText.getText().toString();
                    saveUserName(newName);
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    // User shared preferences
    public void saveUserName(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("ToDoApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", name);
        editor.apply();
    }

    public String getUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("ToDoApp", MODE_PRIVATE);
        return sharedPreferences.getString("userName", "user1");
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.edit_name_menu_item) {
            showEditNameDialog();
            return true;
        } else if (itemId == R.id.exportJobs) {
            exportJobsToTxt();
            return true;
        } else if (itemId == R.id.importJobs) {
            importJobsFromTxt();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }



    // Add job button
    private void handleAddJobButton() {
        addJob = findViewById(R.id.addJobButton);
        addJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddJobDialog();
            }
        });
    }

    private void showAddJobDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.add_job))
            .setView(editText)
            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String jobTitle = editText.getText().toString();
                    saveJob(jobTitle);
                    addJobToListView(jobTitle);
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void addJobToListView(String jobTitle) {
        jobAdapter.add(jobTitle);
        jobAdapter.notifyDataSetChanged();
    }
    private void saveJob(String jobTitle) {
        SQLiteDatabase db = initWriteDb();

        ContentValues values = new ContentValues();
        values.put(JobContract.JobEntry.COLUMN_JOB_TITLE, jobTitle);

        long newRowId = db.insert(JobContract.JobEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error with saving job", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Job saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    private void onPressItemInListView() {
        jobListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long itemId) {
                long jobId = itemIdToJobIdMap.get(itemId);
                deleteJob(jobId);

                jobAdapter.remove(jobAdapter.getItem(position));
                jobAdapter.notifyDataSetChanged();
                return true; // true to consume the long-click event
            }
        });

    }
    public void deleteJob(long jobId) {
        SQLiteDatabase db = initWriteDb();

        String selection = JobContract.JobEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(jobId) };

        int rowCount = db.delete(JobContract.JobEntry.TABLE_NAME, selection, selectionArgs);
        if (rowCount == -1) {
            Toast.makeText(this, "Error deleting " + JobContract.JobEntry._ID, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Job has been deleted", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    // Import and export files in format .txt
    private void importJobsFromTxt() {
        try {
            File file = new File(getExternalFilesDir(null), "jobs.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                jobAdapter.add(line);
                saveJob(line);
            }

            reader.close();

            jobAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Jobs imported from jobs.txt", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error importing jobs", Toast.LENGTH_SHORT).show();
        }
    }

    private void exportJobsToTxt() {
        try {
            File file = new File(getExternalFilesDir(null), "jobs.txt");
            FileWriter writer = new FileWriter(file);

            for (int i = 0; i < jobAdapter.getCount(); i++) {
                String jobTitle = jobAdapter.getItem(i);
                writer.write(jobTitle + "\n");
            }

            writer.close();

            Toast.makeText(this, "Jobs exported to jobs.txt", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error exporting jobs", Toast.LENGTH_SHORT).show();
        }
    }


    private SQLiteDatabase initWriteDb() {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db;
    }
}