package com.example.todoapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.dao.CredentialDao;
import com.example.todoapp.dao.impl.CredentialDaoImpl;
import com.example.todoapp.model.UserProfile;
import com.google.android.material.snackbar.Snackbar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private CredentialDao credentialDao;
    private EditText userEmail;
    private EditText userPassword;
    private ImageView passwordVisibilityToggle;
    private boolean isPasswordVisible;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_page);

       final TextView signUp = findViewById(R.id.signUpTextView);
       final TextView forgotPassword = findViewById(R.id.forgotPassword);
       final Button signIn = findViewById(R.id.signInButton);
       passwordVisibilityToggle = findViewById(R.id.passwordVisibilityToggle);
       userEmail = findViewById(R.id.emailEditText);
       userPassword = findViewById(R.id.passwordEditText);
       credentialDao = new CredentialDaoImpl(this);

       forgotPassword.setOnClickListener(view -> {
           final Intent intent = new Intent(LoginActivity.this,
                   PasswordActivity.class);

           startActivity(intent);
       });
       signUp.setOnClickListener(view -> {
           final Intent intent = new Intent(LoginActivity.this,
                   SignUpActivity.class);

           startActivity(intent);
       });
       passwordVisibilityToggle.setOnClickListener(view -> togglePasswordActivity());
       signIn.setOnClickListener(view -> {
           final UserProfile userProfile = new UserProfile();
           final String hashPassword = hashPassword(userPassword.getText().toString().trim());

           userProfile.setEmail(userEmail.getText().toString().trim());
           userProfile.setPassword(hashPassword);

           if (TextUtils.isEmpty(userProfile.getEmail())
                   || TextUtils.isEmpty(userProfile.getPassword())) {
               showSnackBar(getString(R.string.fields_fill));
           } else {
               final boolean isAuthenticated = credentialDao.checkCredentials(userProfile);

               if (isAuthenticated) {
                   showSnackBar(getString(R.string.successful_log_in));
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           final Intent intent = new Intent(LoginActivity.this,
                                   NavigationActivity.class);

                           intent.putExtra(getString(R.string.user_email), userProfile.getEmail());
                           startActivity(intent);
                       }
                   }, 300);
               } else {
                   showSnackBar(getString(R.string.invalid_detail));
                   clearInputFields();
               }
           }
       });
    }

    private String hashPassword(final String password) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance(getString(R.string.md5));

            messageDigest.update(password.getBytes());
            final byte[] digest = messageDigest.digest();
            final StringBuilder stringBuilder = new StringBuilder();

            for (final byte byteInput : digest) {
                stringBuilder.append(String.format(getString(R.string.format), byteInput));
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void clearInputFields() {
        userEmail.setText("");
        userPassword.setText("");
    }

    private void togglePasswordActivity() {
        if (isPasswordVisible) {
            userPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = false;

            passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility_on);
        } else {
            userPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisible = true;

            passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility_off);
        }
        userPassword.setSelection(userPassword.getText().length());
    }

    private void showSnackBar(final String message) {
        final View parentLayout = findViewById(android.R.id.content);
        final int backGroundColor = Color.argb(200, 255, 255, 255);
        final Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG);

        snackbar.setTextColor(getResources().getColor(R.color.black));
        snackbar.setBackgroundTint(backGroundColor);
        snackbar.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearInputFields();
    }
}
