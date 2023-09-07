package com.example.todoapp.model;

public class UserProfile {

    private Long id;
    private String name;
    private String title;
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public StringBuilder getProfileIconText() {
        final String[] nameWords = name.split(" ");
        final StringBuilder profileText = new StringBuilder();

        for (final String word : nameWords) {
            if (! word.isEmpty()) {
                profileText.append(Character.toUpperCase(word.charAt(0)));
            }
        }

        return profileText;
    }
}
