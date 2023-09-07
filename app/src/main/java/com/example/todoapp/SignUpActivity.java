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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.dao.CredentialDao;
import com.example.todoapp.dao.UserDao;
import com.example.todoapp.dao.impl.CredentialDaoImpl;
import com.example.todoapp.dao.impl.UserDaoImpl;
import com.example.todoapp.model.UserProfile;
import com.google.android.material.snackbar.Snackbar;

public class SignUpActivity extends AppCompatActivity {

    private UserDao userDao;
    private CredentialDao credentialDao;
    private EditText userName;
    private EditText userEmail;
    private EditText userPassword;
    private EditText confirmPassword;
    private boolean isPasswordVisible;
    private ImageView passwordVisibility;
    private ImageView confirmPasswordVisibility;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        userName = findViewById(R.id.signUpName);
        userEmail = findViewById(R.id.signUpEmail);
        userPassword = findViewById(R.id.signUpPassword);
        confirmPassword = findViewById(R.id.signUpConfirmPassword);
        passwordVisibility = findViewById(R.id.passwordVisibility);
        confirmPasswordVisibility = findViewById(R.id.confirmPasswordVisibility);
        final Button createAccount = findViewById(R.id.createAccount);
        final TextView signIn = findViewById(R.id.signInTextView);
        userDao = new UserDaoImpl(this);
        credentialDao = new CredentialDaoImpl(this);

        signIn.setOnClickListener(view -> {
            final Intent intent = new Intent(SignUpActivity.this,
                    LoginActivity.class);

            startActivity(intent);
        });
        passwordVisibility.setOnClickListener(view -> togglePasswordActivity(userPassword,
                passwordVisibility));
        confirmPasswordVisibility.setOnClickListener(view -> togglePasswordActivity(confirmPassword,
                confirmPasswordVisibility));
        createAccount.setOnClickListener(view -> {
            final UserProfile userProfile = new UserProfile();
            final String password = confirmPassword.getText().toString().trim();

            userProfile.setName(userName.getText().toString().trim());
            userProfile.setEmail(userEmail.getText().toString().trim());
            userProfile.setPassword(userPassword.getText().toString().trim());

            if (TextUtils.isEmpty(userProfile.getName()) || TextUtils.isEmpty(
                    userProfile.getEmail()) || TextUtils.isEmpty(userProfile.getPassword())) {
                showSnackBar(getString(R.string.fields_fill));
            } else if (! password.equals(userProfile.getPassword())) {
                showSnackBar(getString(R.string.password_mismatch));
            }
            final long userId = userDao.insert(userProfile);
            final long credentialId = credentialDao.insert(userProfile);

            if (-1 != userId && -1 != credentialId) {
                showSnackBar(getString(R.string.account));
                userName.setText("");
                userEmail.setText("");
                userPassword.setText("");
                confirmPassword.setText("");
            } else {
                showSnackBar(getString(R.string.fail));
            }
            finish();
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
        final Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT);

        snackbar.setTextColor(getResources().getColor(R.color.black));
        snackbar.setBackgroundTint(backGroundColor);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userDao.open();
        credentialDao.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userDao.close();
        credentialDao.close();
    }
}
