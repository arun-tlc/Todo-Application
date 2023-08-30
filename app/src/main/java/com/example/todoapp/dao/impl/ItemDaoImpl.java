package com.example.todoapp.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.todoapp.dao.ItemDao;
import com.example.todoapp.database.DataBaseHelper;
import com.example.todoapp.database.table.ItemContract;
import com.example.todoapp.model.TodoItem;

public class ItemDaoImpl implements ItemDao {

    private SQLiteDatabase database;
    private final DataBaseHelper dataBaseHelper;

    public ItemDaoImpl(final Context context) {
        this.dataBaseHelper = new DataBaseHelper(context);
    }

    @Override
    public long insert(final TodoItem todoItem) {
        final ContentValues values = new ContentValues();

        values.put(ItemContract.COLUMN_NAME, todoItem.getLabel());
        values.put(ItemContract.COLUMN_PROJECT_ID, todoItem.getParentId());
        values.put(ItemContract.COLUMN_STATUS, todoItem.getStatus());

        return database.insert(ItemContract.TABLE_NAME, null, values);
    }

    @Override
    public void open() {
        database = dataBaseHelper.getWritableDatabase();
    }

    @Override
    public void close() {
        dataBaseHelper.close();
    }
}
