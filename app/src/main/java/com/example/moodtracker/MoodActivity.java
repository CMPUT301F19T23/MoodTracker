package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MoodActivity extends AppCompatActivity {

    private String username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        username = this.getIntent().getStringExtra("username");

        ((TextView) findViewById(R.id.idMyMoodHistory)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoodActivity.this, MoodHistoryActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        ((TextView) findViewById(R.id.idFriend)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoodActivity.this, FriendListActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

    }
}
