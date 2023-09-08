package com.example.todoapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.dao.UserDao;
import com.example.todoapp.dao.impl.UserDaoImpl;
import com.example.todoapp.model.UserProfile;
import com.google.android.material.snackbar.Snackbar;

public class ProfileActivity extends AppCompatActivity {

    private UserDao userDao;
    private UserProfile userProfile;
    private TextView profileIcon;
    private Button saveButton;
    private Button cancelButton;
    private EditText userTitle;
    private EditText userName;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        final ImageButton backToMenu = findViewById(R.id.backMenu);
        userTitle = findViewById(R.id.editTitle);
        userName = findViewById(R.id.editUserName);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);
        profileIcon = findViewById(R.id.userProfile);
        userDao = new UserDaoImpl(this);
        userProfile = UserProfileSingleton.getInstance().getUserProfile();

        if (null != userProfile) {
            userName.setText(userProfile.getName());
            userTitle.setText(userProfile.getTitle());
            profileIcon.setText(userProfile.getProfileIconText());
        }
        backToMenu.setOnClickListener(view -> onBackPressed());
        cancelButton.setOnClickListener(view -> onBackPressed());
        saveButton.setOnClickListener(view -> updateUserDetails());

    }

    private void updateUserDetails() {
        final Intent resultantIntent = new Intent();
        final String editedName = userName.getText().toString();
        final String editedTitle = userTitle.getText().toString();

        if (!userProfile.getName().equals(editedName)
                || !userProfile.getTitle().equals(editedTitle)) {

            userProfile.setName(editedName);
            userProfile.setTitle(editedTitle);
            profileIcon.setText(userProfile.getProfileIconText());
            final long updatedUserRows = userDao.update(userProfile);

            if (0 > updatedUserRows) {
                showSnackBar(getString(R.string.fail));
            } else {
                showSnackBar(getString(R.string.update_success));
                resultantIntent.putExtra(getString(R.string.user_name), userProfile.getName());
                resultantIntent.putExtra(getString(R.string.user_title), userProfile.getTitle());
                setResult(RESULT_OK, resultantIntent);
                finish();
            }
            applyFontToAllLayouts();
            applyFontSize();
            applyColorToComponent();
        } else {
            showSnackBar(getString(R.string.details_not_modified));
        }
    }

    private void applyColorToComponent() {
        final int defaultColor = TypeFaceUtil.getSelectedDefaultColor();

        if (defaultColor == R.color.green) {
            profileIcon.setBackgroundColor(getResources().getColor(R.color.green));
            saveButton.setBackgroundColor(getResources().getColor(R.color.green));
            cancelButton.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (defaultColor == R.color.blue) {
            profileIcon.setBackgroundColor(getResources().getColor(R.color.blue));
            saveButton.setBackgroundColor(getResources().getColor(R.color.blue));
            cancelButton.setBackgroundColor(getResources().getColor(R.color.blue));
        }
    }

    private void applyFontToAllLayouts() {
        TypeFaceUtil.applyFontToView(getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private void applyFontSize() {
        TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private void showSnackBar(final String message) {
        final View parentLayout = findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userDao.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userDao.close();
    }
}
