package com.example.todoapp.dao.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.todoapp.dao.ItemDao;
import com.example.todoapp.database.DataBaseHelper;
import com.example.todoapp.database.table.ItemContract;
import com.example.todoapp.model.TodoItem;

import java.util.ArrayList;
import java.util.List;

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
        values.put(ItemContract.COLUMN_STATUS, String.valueOf(todoItem.getStatus()).toLowerCase());
        values.put(ItemContract.COLUMN_ORDER, todoItem.getItemOrder());

        return database.insert(ItemContract.TABLE_NAME, null, values);
    }

    @SuppressLint("Range")
    @Override
    public List<TodoItem> getAllTodoItems() {
        final SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        final List<TodoItem> todoItems = new ArrayList<>();

        try (final Cursor cursor = sqLiteDatabase.query(ItemContract.TABLE_NAME, null,
                null, null, null, null, null)) {

            if (null != cursor && cursor.moveToFirst()) {
                do {
                    final Long itemId = cursor.getLong(cursor.getColumnIndex(
                            ItemContract.COLUMN_ID));
                    final String itemName = cursor.getString(cursor.getColumnIndex(
                            ItemContract.COLUMN_NAME));
                    final Long projectId = cursor.getLong(cursor.getColumnIndex(
                            ItemContract.COLUMN_PROJECT_ID));
                    final String status = cursor.getString(cursor.getColumnIndex(
                            ItemContract.COLUMN_STATUS));
                    final TodoItem todoItem = new TodoItem(itemName);

                    todoItem.setId(itemId);
                    todoItem.setParentId(projectId);
                    todoItem.setStatus(TodoItem.StatusType.valueOf(status.toLowerCase()));
                    todoItems.add(todoItem);
                } while (cursor.moveToNext());
            }
        }

        return todoItems;
    }

    @SuppressLint("Range")
    @Override
    public List<TodoItem> getTodoItemsForProject(final Long projectId) {
        final SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        final List<TodoItem> todoItems = new ArrayList<>();

        try (final Cursor cursor = sqLiteDatabase.query(ItemContract.TABLE_NAME, null,
                String.format("%s = ?", ItemContract.COLUMN_PROJECT_ID),
                new String[]{String.valueOf(projectId)}, null, null,
                ItemContract.COLUMN_ORDER)) {

            if (null != cursor && cursor.moveToFirst()) {
                do {
                    final Long itemId = cursor.getLong(cursor.getColumnIndex(
                            ItemContract.COLUMN_ID));
                    final String itemName = cursor.getString(cursor.getColumnIndex(
                            ItemContract.COLUMN_NAME));
                    final String status = cursor.getString(cursor.getColumnIndex(
                            ItemContract.COLUMN_STATUS));
                    final TodoItem todoItem = new TodoItem(itemName);

                    todoItem.setId(itemId);
                    todoItem.setParentId(projectId);
                    todoItem.setStatus(TodoItem.StatusType.valueOf(status.toUpperCase()));
                    todoItems.add(todoItem);
                } while (cursor.moveToNext());
            }
        }

        return todoItems;
    }

    @Override
    public long delete(final Long id) {
        return database.delete(ItemContract.TABLE_NAME,String.format("%s = ?",
                        ItemContract.COLUMN_ID), new String[]{String.valueOf(id)});
    }

    @Override
    public void updateItemsStatus(final TodoItem todoItem) {
        final ContentValues values = new ContentValues();

        values.put(ItemContract.COLUMN_STATUS, String.valueOf(todoItem.getStatus()).toLowerCase());
        database.update(ItemContract.TABLE_NAME, values, String.format("%s = ?",
                ItemContract.COLUMN_ID), new String[]{String.valueOf(todoItem.getId())});
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
    public void updateItemsOrder(final TodoItem todoItem) {
        final ContentValues values = new ContentValues();

        values.put(ItemContract.COLUMN_ORDER, todoItem.getItemOrder());
        database.update(ItemContract.TABLE_NAME, values, String.format("%s = ?",
                ItemContract.COLUMN_ID), new String[]{String.valueOf(todoItem.getId())});
    }
}
