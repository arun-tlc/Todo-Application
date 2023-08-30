package com.example.todoapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todoapp.database.table.ItemContract;
import com.example.todoapp.database.table.ProjectContract;
import com.example.todoapp.database.table.UserContract;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TodoApp";
    private static final int DATABASE_VERSION = 1;

    public DataBaseHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(UserContract.CREATE_TABLE);
        db.execSQL(ProjectContract.CREATE_TABLE);
        db.execSQL(ItemContract.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", UserContract.TABLE_NAME));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", ProjectContract.TABLE_NAME));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", ItemContract.TABLE_NAME));
        onCreate(db);
    }
}
