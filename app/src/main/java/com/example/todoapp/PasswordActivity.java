package com.example.todoapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.dao.CredentialDao;
import com.example.todoapp.dao.impl.CredentialDaoImpl;
import com.example.todoapp.model.UserProfile;
import com.google.android.material.snackbar.Snackbar;

public class PasswordActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText newPassword;
    private EditText confirmPassword;
    private CredentialDao credentialDao;
    private boolean isPasswordVisible;
    private ImageView newPasswordToggle;
    private ImageView confirmPasswordToggle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_page);

        final Button cancel = findViewById(R.id.cancelToCreatePassword);
        final Button resetPassword = findViewById(R.id.resetPassword);
        newPasswordToggle = findViewById(R.id.newPasswordToggle);
        confirmPasswordToggle = findViewById(R.id.confirmPasswordToggle);
        userEmail = findViewById(R.id.emailEdit);
        newPassword = findViewById(R.id.newPasswordEdit);
        confirmPassword = findViewById(R.id.confirmPasswordEdit);
        credentialDao = new CredentialDaoImpl(this);

        cancel.setOnClickListener(view -> {
            final Intent intent = new Intent(PasswordActivity.this,
                    LoginActivity.class);

            startActivity(intent);
        });
        newPasswordToggle.setOnClickListener(view -> togglePasswordActivity(newPassword,
                newPasswordToggle));
        confirmPasswordToggle.setOnClickListener(view -> togglePasswordActivity(confirmPassword,
                confirmPasswordToggle));
        resetPassword.setOnClickListener(view -> {
            final UserProfile userProfile = new UserProfile();
            final String password = confirmPassword.getText().toString().trim();

            userProfile.setEmail(userEmail.getText().toString().trim());
            userProfile.setPassword(newPassword.getText().toString().trim());

            if (TextUtils.isEmpty(userProfile.getEmail())
                    || TextUtils.isEmpty(userProfile.getPassword())) {
                showSnackBar(getString(R.string.fields_fill));
            } else if(! password.equals(userProfile.getPassword())) {
                showSnackBar(getString(R.string.password_mismatch));
            } else {
                final boolean emailExists = credentialDao.checkEmailExists(userProfile.getEmail());

                if (emailExists) {
                    final long updatedCredentialRows = credentialDao.updatePassword(userProfile);

                    if (0 < updatedCredentialRows) {
                        showSnackBar(getString(R.string.password_update));
                        userEmail.setText("");
                        newPassword.setText("");
                        confirmPassword.setText("");
                        finish();
                    } else {
                        showSnackBar(getString(R.string.fail));
                    }
                } else {
                    showSnackBar(getString(R.string.invalid_email));
                    userEmail.setText("");
                    newPassword.setText("");
                    confirmPassword.setText("");
                }
            }
        });
    }

    private void togglePasswordActivity(final EditText password, final ImageView visibilityToggle) {
        if (isPasswordVisible) {
            password.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = false;

            visibilityToggle.setImageResource(R.drawable.ic_visibility_on);
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisible = true;

            visibilityToggle.setImageResource(R.drawable.ic_visibility_off);
        }
        password.setSelection(password.getText().length());
    }

    private void showSnackBar(final String message) {
        final View parentLayout = findViewById(android.R.id.content);
        final int backGroundColor = Color.argb(200, 255, 255, 255);
        final Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG);

        snackbar.setTextColor(getResources().getColor(R.color.black));
        snackbar.setBackgroundTint(backGroundColor);
        snackbar.show();
    }
}