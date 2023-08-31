package com.example.todoapp.database.table;
public class ItemContract {

    public static final String TABLE_NAME = "items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PROJECT_ID = "project_id";
    public static final String COLUMN_STATUS = "status";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_NON_COMPLETED = "non_completed";
    public static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT " +
                            "NOT NULL, %s INTEGER NOT NULL, %s TEXT NOT NULL CHECK (%s IN ('%s', '%s')," +
                            " FOREIGN KEY(%s) REFERENCES %s(%s));",
                    TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_PROJECT_ID, COLUMN_STATUS,
                    COLUMN_STATUS, STATUS_COMPLETED, STATUS_NON_COMPLETED,
                    COLUMN_PROJECT_ID, ProjectContract.TABLE_NAME, ProjectContract.COLUMN_ID);
}
