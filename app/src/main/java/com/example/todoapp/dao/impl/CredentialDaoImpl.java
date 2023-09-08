package com.example.todoapp.dao.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.todoapp.dao.CredentialDao;
import com.example.todoapp.database.DataBaseHelper;
import com.example.todoapp.database.table.SignUpDetails;
import com.example.todoapp.model.UserProfile;

public class CredentialDaoImpl implements CredentialDao {

    private SQLiteDatabase database;
    private final DataBaseHelper dataBaseHelper;

    public CredentialDaoImpl(final Context context) {
        dataBaseHelper = new DataBaseHelper(context);
    }

    @Override
    public long insert(final UserProfile userProfile) {
        final ContentValues values = new ContentValues();

        values.put(SignUpDetails.COLUMN_EMAIL, userProfile.getEmail());
        values.put(SignUpDetails.COLUMN_PASSWORD, userProfile.getPassword());

        return database.insert(SignUpDetails.TABLE_NAME, null, values);
    }

    @Override
    public void open() {
        database = dataBaseHelper.getWritableDatabase();
    }

    @Override
    public void close() {
        dataBaseHelper.close();
    }

    @SuppressLint({"Recycle", "Range"})
    @Override
    public boolean checkCredentials(final UserProfile userProfile) {
        final SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.query(SignUpDetails.TABLE_NAME,
                new String[]{SignUpDetails.COLUMN_PASSWORD},
                String.format("%s = ?", SignUpDetails.COLUMN_EMAIL),
                new String[]{userProfile.getEmail()}, null, null, null);
        boolean result = false;

        if (null != cursor && cursor.moveToFirst()) {
           final String password = cursor.getString(cursor.getColumnIndex(
                   SignUpDetails.COLUMN_PASSWORD));

           if (password.equals(userProfile.getPassword())) {
               result = true;
           }
           cursor.close();
        }

        return result;
    }

    @Override
    public long updatePassword(final UserProfile userProfile) {
        final ContentValues values = new ContentValues();

        values.put(SignUpDetails.COLUMN_PASSWORD, userProfile.getPassword());

        return database.update(SignUpDetails.TABLE_NAME, values, String.format("%s = ?",
                SignUpDetails.COLUMN_EMAIL), new String[]{userProfile.getEmail()});
    }

    @Override
    public boolean checkEmailExists(final String email) {
        final SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.query(SignUpDetails.TABLE_NAME,
                new String[]{SignUpDetails.COLUMN_EMAIL},
                String.format("%s = ?", SignUpDetails.COLUMN_EMAIL), new String[]{email},
                null, null, null);
        final boolean emailExists = null != cursor && 0 < cursor.getCount();

        if (null != cursor) {
            cursor.close();
        }
        return emailExists;
    }
}