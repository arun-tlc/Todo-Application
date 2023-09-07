package com.example.todoapp.database.table;

public class UserContract {

    public static final String TABLE_NAME = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT);",
                    TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_DESCRIPTION);
}
