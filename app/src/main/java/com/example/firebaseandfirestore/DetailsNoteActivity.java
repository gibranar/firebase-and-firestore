package com.example.firebaseandfirestore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class DetailsNoteActivity extends AppCompatActivity {

    private ImageButton backButton;
    private Button saveButton, deleteButton;
    private EditText titleEditText, contentEditText;
    private TextView titleTextView;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String noteId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        // Initialize views
        titleTextView = findViewById(R.id.noteTitle);
        titleEditText = findViewById(R.id.noteTitle);
        contentEditText = findViewById(R.id.noteContent);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Retrieve noteId from Intent
        noteId = getIntent().getStringExtra("noteId");

        // Set up onClickListener untuk tombol back
        backButton.setOnClickListener(v -> onBackPressed()); // Kembali ke aktivitas sebelumnya

        // Ambil data note berdasarkan noteId
        fetchNoteDetails();

        // Set up onClickListener untuk tombol Save
        saveButton.setOnClickListener(v -> updateNote());

        // Set up onClickListener untuk tombol Delete
        deleteButton.setOnClickListener(v -> deleteNote());
    }

    // Fetch note details from Firestore
    private void fetchNoteDetails() {
        if (noteId != null) {
            db.collection("notes").document(noteId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Note note = documentSnapshot.toObject(Note.class);
                    if (note != null) {
                        titleEditText.setText(note.getTitle());  // Set title in EditText
                        contentEditText.setText(note.getText()); // Set content in EditText
                    }
                }
            }).addOnFailureListener(e -> Toast.makeText(DetailsNoteActivity.this, "Error loading note", Toast.LENGTH_SHORT).show());
        }
    }

    // Update note in Firestore
    private void updateNote() {
        String updatedTitle = titleEditText.getText().toString().trim();
        String updatedContent = contentEditText.getText().toString().trim();

        if (updatedTitle.isEmpty() || updatedContent.isEmpty()) {
            Toast.makeText(this, "Title and Content cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map with updated values
        db.collection("notes").document(noteId).update("title", updatedTitle, "text", updatedContent)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DetailsNoteActivity.this, "Note updated", Toast.LENGTH_SHORT).show();
                    finish();  // Close the activity after updating
                }).addOnFailureListener(e -> Toast.makeText(DetailsNoteActivity.this, "Error updating note", Toast.LENGTH_SHORT).show());
    }

    // Delete note from Firestore
    private void deleteNote() {
        db.collection("notes").document(noteId).delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(DetailsNoteActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity after deletion
        }).addOnFailureListener(e -> Toast.makeText(DetailsNoteActivity.this, "Error deleting note", Toast.LENGTH_SHORT).show());
    }
}