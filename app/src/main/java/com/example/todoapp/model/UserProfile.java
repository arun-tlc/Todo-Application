package com.example.todoapp.model;

public class UserProfile {

    private String name;
    private String title;

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
