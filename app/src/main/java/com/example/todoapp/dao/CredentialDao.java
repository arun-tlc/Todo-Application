package com.example.todoapp.dao;

import com.example.todoapp.model.Credential;

public interface CredentialDao {

    long insert(final Credential signUpDetail);
    void open();
    void close();
    boolean checkCredentials(final Credential loginDetail);
    long updatePassword(final Credential userDetail);
    boolean checkEmailExists(final String email);
}
