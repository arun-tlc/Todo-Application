package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.model.UserProfile;

public class ProfileActivity extends AppCompatActivity {

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
        final UserProfile userProfile = new UserProfile();

        userProfile.setName(getIntent().getStringExtra("Exist Name"));
        userProfile.setTitle(getIntent().getStringExtra("Exist Title"));
        userName.setText(userProfile.getName());
        userTitle.setText(userProfile.getTitle());
        profileIcon.setText(userProfile.getProfileIconText());
        backToMenu.setOnClickListener(view -> onBackPressed());
        cancelButton.setOnClickListener(view -> {
            onBackPressed();
        });

        saveButton.setOnClickListener(view -> {
            final Intent resultantIntent = new Intent();

            userProfile.setName(userName.getText().toString());
            userProfile.setTitle(userTitle.getText().toString());
            profileIcon.setText(userProfile.getProfileIconText());
            resultantIntent.putExtra("User Name", userProfile.getName());
            resultantIntent.putExtra("User Title", userProfile.getTitle());
            setResult(RESULT_OK, resultantIntent);
            finish();
        });
    }
}
