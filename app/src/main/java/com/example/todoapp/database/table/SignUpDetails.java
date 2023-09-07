package com.example.todoapp.database.table;

public class SignUpDetails {

    public static final String TABLE_NAME = "credential";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL);",
                    TABLE_NAME, COLUMN_ID, COLUMN_EMAIL, COLUMN_PASSWORD);
}
