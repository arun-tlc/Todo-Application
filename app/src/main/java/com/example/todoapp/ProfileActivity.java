package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.dao.UserDao;
import com.example.todoapp.dao.impl.UserDaoImpl;
import com.example.todoapp.model.UserProfile;

public class ProfileActivity extends AppCompatActivity {

    private UserDao userDao;
    private UserProfile userProfile;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        final ImageButton backToMenu = findViewById(R.id.backMenu);
        final EditText userTitle = findViewById(R.id.editTitle);
        final EditText userName = findViewById(R.id.editUserName);
        final Button cancelButton = findViewById(R.id.cancelButton);
        final Button saveButton = findViewById(R.id.saveButton);
        final TextView profileIcon = findViewById(R.id.userProfile);
        userDao = new UserDaoImpl(this);
        userProfile = userDao.getUserProfile();

        if (null != userProfile) {
            userName.setText(userProfile.getName());
            userTitle.setText(userProfile.getTitle());
            profileIcon.setText(userProfile.getProfileIconText());
        } else {
            userProfile = new UserProfile();

            userProfile.setName(getIntent().getStringExtra("Exist Name"));
            userProfile.setTitle(getIntent().getStringExtra("Exist Title"));
        }
        backToMenu.setOnClickListener(view -> onBackPressed());
        cancelButton.setOnClickListener(view -> {
            onBackPressed();
        });

        saveButton.setOnClickListener(view -> {
            final Intent resultantIntent = new Intent();

            userProfile.setName(userName.getText().toString());
            userProfile.setTitle(userTitle.getText().toString());
            profileIcon.setText(userProfile.getProfileIconText());
            final long userId = null != userProfile.getId() ? userDao.update(userProfile)
                    : userDao.insert(userProfile);

            if (-1 == userId) {
                Toast.makeText(this,R.string.fail, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
            }
            userProfile.setId(userId);
            resultantIntent.putExtra("User Name", userProfile.getName());
            resultantIntent.putExtra("User Title", userProfile.getTitle());
            setResult(RESULT_OK, resultantIntent);
            finish();
        });
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
