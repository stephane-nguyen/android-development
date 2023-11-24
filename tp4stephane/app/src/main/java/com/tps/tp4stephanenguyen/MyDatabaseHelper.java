package com.tps.tp4stephanenguyen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MYBASE";

    MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_JOB_TABLE = "CREATE TABLE " +
                JobContract.JobEntry.TABLE_NAME + " (" +
                JobContract.JobEntry._ID + " INTEGER PRIMARY KEY," +
                JobContract.JobEntry.COLUMN_JOB_TITLE + " TEXT NOT NULL" +
                ");";
        db.execSQL(SQL_CREATE_JOB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + JobContract.JobEntry.TABLE_NAME);
        onCreate(db);
    }
}