package com.tps.tp4stephanenguyen;

import android.provider.BaseColumns;

public final class JobContract {
    private JobContract() {}
    public static final class JobEntry implements BaseColumns {
        public static final String TABLE_NAME = "jobs";
//        public static final String COLUMN_ID = "id";
        public static final String COLUMN_JOB_TITLE = "title";
    }
}
