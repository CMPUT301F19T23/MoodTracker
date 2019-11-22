package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;


import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MoodActivity extends AppCompatActivity {
    TextView viewProfileButton;
    TextView logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);
        viewProfileButton = findViewById(R.id.view_profile_button);
        logoutButton = findViewById(R.id.log_out_button);

        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent oldIntent = getIntent();
                Intent intent = new Intent(MoodActivity.this, MoodHistoryActivity.class);
                // Have to propagate information other classes need but we don't use
                intent.putExtra(LoginActivity.EXTRA_USERPATH, oldIntent.getStringExtra(LoginActivity.EXTRA_USERPATH));
                intent.putExtra(LoginActivity.EXTRA_USER, oldIntent.getStringExtra(LoginActivity.EXTRA_USER));
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
