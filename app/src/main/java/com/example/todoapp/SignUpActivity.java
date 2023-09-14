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

import com.example.todoapp.backendservice.AuthenticationService;
import com.example.todoapp.dao.CredentialDao;
import com.example.todoapp.dao.UserDao;
import com.example.todoapp.dao.impl.CredentialDaoImpl;
import com.example.todoapp.dao.impl.UserDaoImpl;
import com.example.todoapp.model.Credential;
import com.example.todoapp.model.UserProfile;
import com.google.android.material.snackbar.Snackbar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignUpActivity extends AppCompatActivity {

    private UserDao userDao;
    private CredentialDao credentialDao;
    private EditText userName;
    private EditText userEmail;
    private EditText userPassword;
    private EditText confirmPassword;
    private EditText userTitle;
    private EditText userHint;
    private boolean isPasswordVisible;
    private ImageView passwordVisibility;
    private ImageView confirmPasswordVisibility;
    private AuthenticationService authenticationService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        userName = findViewById(R.id.signUpName);
        userEmail = findViewById(R.id.signUpEmail);
        userTitle = findViewById(R.id.signUpTitle);
        userHint = findViewById(R.id.userHint);
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
            final Credential signUpDetail = new Credential();
            final String password = confirmPassword.getText().toString().trim();
            final String hashPassword = hashPassword(userPassword.getText().toString().trim());

            userProfile.setName(userName.getText().toString().trim());
            userProfile.setEmail(userEmail.getText().toString().trim());
            userProfile.setTitle(userTitle.getText().toString().trim());
            signUpDetail.setHint(userHint.getText().toString().trim());
            signUpDetail.setEmail(userEmail.getText().toString().trim());
            signUpDetail.setPassword(userPassword.getText().toString().trim());

            if (TextUtils.isEmpty(userProfile.getName()) || TextUtils.isEmpty(signUpDetail.getEmail())
                    || TextUtils.isEmpty(signUpDetail.getPassword())
                    || TextUtils.isEmpty(signUpDetail.getHint()) || TextUtils.isEmpty(userProfile.getTitle())) {
                showSnackBar(getString(R.string.fields_fill));
            } else if (!password.equals(signUpDetail.getPassword())) {
                showSnackBar(getString(R.string.password_mismatch));
            } else {
                authenticationService = new AuthenticationService("http://192.168.1.9:8080/");

                authenticationService.signUp(userProfile, signUpDetail, new AuthenticationService.ApiResponseCallBack() {
                    @Override
                    public void onSuccess(String responseBody) {
                        showSnackBar(getString(R.string.account));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        showSnackBar(String.format("Request Failed : %s", errorMessage));
                    }
                });
            }
//            final long userId = userDao.insert(userProfile);
//            final long credentialId = credentialDao.insert(signUpDetail);
//
//            if (-1 != userId && -1 != credentialId) {
//                showSnackBar(getString(R.string.account));
//                userName.setText("");
//                userEmail.setText("");
//                userPassword.setText("");
//                confirmPassword.setText("");
//            } else {
//                showSnackBar(getString(R.string.fail));
//            }
//            finish();
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
