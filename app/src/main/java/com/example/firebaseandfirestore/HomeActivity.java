package com.example.firebaseandfirestore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView userEmail;
    private Button addTaskButton;
    private ImageButton profileSettingsButton;
    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    private List<Note> notesList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notesRef = db.collection("notes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        mAuth = FirebaseAuth.getInstance();

        userEmail = findViewById(R.id.userEmail);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        addTaskButton = findViewById(R.id.addTaskButton);
        profileSettingsButton = findViewById(R.id.profileSettingsButton);

        // Set up RecyclerView
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesAdapter = new NotesAdapter(notesList, this);
        notesRecyclerView.setAdapter(notesAdapter);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            userEmail.setText("Welcome Back! \n" + email);
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddNoteActivity.class);
                startActivity(intent);
                finish();
            }
        });

        profileSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        fetchNotes();
    }

    private void fetchNotes() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();

            notesRef.whereEqualTo("user_email", email).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@NonNull QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                    if (e != null) {
                        Toast.makeText(HomeActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    notesList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Note note = document.toObject(Note.class);
                        note.setId(document.getId()); // Menyimpan ID dari Firestore
                        notesList.add(note);
                    }
                    notesAdapter.notifyDataSetChanged();
                }
            });
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
