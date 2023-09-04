package com.example.todoapp.dao.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.todoapp.dao.ProjectDao;
import com.example.todoapp.database.DataBaseHelper;
import com.example.todoapp.database.table.ProjectContract;
import com.example.todoapp.model.Project;

import java.util.ArrayList;
import java.util.List;

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
    public void updateProjectsOrder(final Project project) {

        final ContentValues values = new ContentValues();

        values.put(ProjectContract.COLUMN_ORDER, project.getProjectOrder());
        database.update(ProjectContract.TABLE_NAME, values, String.format("%s = ?",
                ProjectContract.COLUMN_ID), new String[]{String.valueOf(project.getId())});
    }

    @Override
    public void delete(final Project projectToRemove) {
        database.delete(ProjectContract.TABLE_NAME, String.format("%s = ?",
                ProjectContract.COLUMN_ID), new String[]{String.valueOf(projectToRemove.getId())});
    }

    @Override
    public long insert(final Project project) {
        final ContentValues values = new ContentValues();

        values.put(ProjectContract.COLUMN_NAME, project.getLabel());
        values.put(ProjectContract.COLUMN_USER_ID, project.getUserId());
        values.put(ProjectContract.COLUMN_ORDER, project.getProjectOrder());

        return database.insert(ProjectContract.TABLE_NAME, null, values);
    }

    @SuppressLint("Range")
    @Override
    public List<Project> getAllProjects() {
        final SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        final List<Project> projects = new ArrayList<>();

        try (final Cursor cursor = sqLiteDatabase.query(ProjectContract.TABLE_NAME, null,
                null, null, null, null,
                ProjectContract.COLUMN_ORDER)) {

            if (null != cursor && cursor.moveToFirst()) {
                do {
                    final Long projectId = cursor.getLong(cursor.getColumnIndex(ProjectContract.COLUMN_ID));
                    final String projectName = cursor.getString(cursor.getColumnIndex(ProjectContract.COLUMN_NAME));
                    final Long userId = cursor.getLong(cursor.getColumnIndex(ProjectContract.COLUMN_USER_ID));
                    final Project project = new Project();

                    project.setId(projectId);
                    project.setLabel(projectName);
                    project.setUserId(userId);
                    projects.add(project);
                } while (cursor.moveToNext());
            }
        }

        return projects;
    }
}
