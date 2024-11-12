package com.example.firebaseandfirestore;

public class Note {
    private String title;
    private String text;

    public Note() {
        // Diperlukan untuk Firestore
    }

    public Note(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
