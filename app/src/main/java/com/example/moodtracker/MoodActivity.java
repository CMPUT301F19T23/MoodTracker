package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;


import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This allows the user to view his/her moods under account
 * when clicked on "view my history" button
 * @author xuhf0429
 */

public class MoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        ((TextView) findViewById(R.id.idMyMoodHistory)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoodActivity.this, MoodHistoryActivity.class);
                startActivity(intent);
            }
        });



        ((TextView) findViewById(R.id.idLogOut)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoodActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
