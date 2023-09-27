package com.example.todoapp;

import com.example.todoapp.model.UserProfile;

public class UserProfileSingleton {

    private static UserProfileSingleton instance;
    private UserProfile userProfile;

    private UserProfileSingleton() {}

    public static UserProfileSingleton getInstance() {
        return null == instance ? instance = new UserProfileSingleton() : instance;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(final UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
