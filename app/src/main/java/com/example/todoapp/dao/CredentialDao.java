package com.example.todoapp.dao;

import com.example.todoapp.model.UserProfile;

public interface CredentialDao {

    long insert(final UserProfile userProfile);
    void open();
    void close();
    boolean checkCredentials(final UserProfile userProfile);
    long updatePassword(final UserProfile userProfile);
    boolean checkEmailExists(final String email);
}
