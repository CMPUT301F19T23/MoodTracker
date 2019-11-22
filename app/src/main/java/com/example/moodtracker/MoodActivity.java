package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class MoodActivity extends AppCompatActivity {
    TextView viewProfileButton;
    TextView logoutButton;
    TextView  username_text_view;
    String userpath, username;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);
        viewProfileButton = findViewById(R.id.view_profile_button);
        logoutButton = findViewById(R.id.log_out_button);
        username_text_view = findViewById(R.id.hello_username_text_view);
        db = FirebaseFirestore.getInstance();

        Intent oldIntent = getIntent();
        userpath = oldIntent.getStringExtra(LoginActivity.EXTRA_USERPATH);
        username = oldIntent.getStringExtra(LoginActivity.EXTRA_USER);
        DocumentReference docRef = db.document(userpath + username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Log.i("LOGGER", "First " + document.getString("first"));
                        String username_from_login = document.getString("UserName");
                        username_text_view.setText(username_from_login);
                    }
                }
            }
        });


        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent oldIntent = getIntent();
                Intent intent = new Intent(MoodActivity.this, MoodHistoryActivity.class);
                // Have to propagate information other classes need but we don't use
                intent.putExtra(LoginActivity.EXTRA_USERPATH, userpath);
                intent.putExtra(LoginActivity.EXTRA_USER, username);
                startActivity(intent);
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoodActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
