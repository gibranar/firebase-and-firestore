package com.example.firebaseandfirestore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private Button logoutButton;
    private Button addTaskButton;
    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    private List<Note> notesList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notesRef = db.collection("notes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get references to UI elements
        userEmail = findViewById(R.id.userEmail);
        logoutButton = findViewById(R.id.logoutButton);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        addTaskButton = findViewById(R.id.addTaskButton);

        // Set up RecyclerView
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesAdapter = new NotesAdapter(notesList);
        notesRecyclerView.setAdapter(notesAdapter);

        // Get current user and set email to TextView
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            userEmail.setText(email);
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }

        // Set up logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(HomeActivity.this, "Logged out", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddNoteActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Fetch notes from Firestore
        fetchNotes();
    }

    private void fetchNotes() {
        notesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@NonNull QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(HomeActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    return;
                }

                notesList.clear();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    Note note = document.toObject(Note.class);
                    notesList.add(note);
                }
                notesAdapter.notifyDataSetChanged();
            }
        });
    }


}
