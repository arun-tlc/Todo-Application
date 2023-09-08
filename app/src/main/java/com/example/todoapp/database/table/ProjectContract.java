    package com.example.todoapp.database.table;

    public class ProjectContract {

        public static final String TABLE_NAME = "projects";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_ORDER = "project_order";
        public static final String CREATE_TABLE =
                String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT " +
                                "NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, FOREIGN KEY(%s) " +
                                "REFERENCES %s(%s));",
                        TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_USER_ID, COLUMN_ORDER,
                        COLUMN_USER_ID, UserContract.TABLE_NAME, UserContract.COLUMN_ID);
    }
