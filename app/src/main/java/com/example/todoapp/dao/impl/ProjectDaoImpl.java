package com.example.todoapp.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.todoapp.dao.ProjectDao;
import com.example.todoapp.database.DataBaseHelper;
import com.example.todoapp.database.table.ProjectContract;
import com.example.todoapp.model.Project;

public class ProjectDaoImpl implements ProjectDao {

    private SQLiteDatabase database;
    private final DataBaseHelper dataBaseHelper;

    public ProjectDaoImpl(final Context context) {
        this.dataBaseHelper = new DataBaseHelper(context);
    }

    @Override
    public void open() {
        database = dataBaseHelper.getWritableDatabase();
    }

    @Override
    public void close() {
        dataBaseHelper.close();
    }

    @Override
    public long insert(final Project project) {
        final ContentValues values = new ContentValues();

        values.put(ProjectContract.COLUMN_NAME, project.getLabel());
        values.put(ProjectContract.COLUMN_USER_ID, project.getUserId());

        return database.insert(ProjectContract.TABLE_NAME, null, values);
    }
}
