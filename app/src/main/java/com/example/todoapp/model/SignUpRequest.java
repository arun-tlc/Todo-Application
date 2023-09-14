package com.example.todoapp.model;

public class SignUpRequest {

    private String name;
    private String email;
    private String hint;
    private String title;
    private String password;

    public SignUpRequest(final UserProfile userProfile, final Credential credential) {
        this.name = userProfile.getName();
        this.email = userProfile.getEmail();
        this.hint = credential.getHint();
        this.title = userProfile.getTitle();
        this.password = credential.getPassword();
    }
}
