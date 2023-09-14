package com.example.todoapp.model;

public class ResetPasswordRequest {

    private String email;
    private String password;
    private String oldHint;
    private String newHint;

    public ResetPasswordRequest(final Credential credential, final String userHint) {
        this.email = credential.getEmail();
        this.password = credential.getPassword();
        this.oldHint = credential.getHint();
        this.newHint = userHint;
    }
}
