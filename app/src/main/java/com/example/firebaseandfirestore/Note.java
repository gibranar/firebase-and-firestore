package com.example.firebaseandfirestore;

public class Note {
    private String id; // ID unik dari Firestore
    private String title;
    private String text;
    private String user_email;

    public Note() {
    }

    public Note(String id, String title, String text, String user_email) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.user_email = user_email;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getUserEmail() {
        return user_email;
    }

    public void setId(String id) {
        this.id = id;
    }
}
