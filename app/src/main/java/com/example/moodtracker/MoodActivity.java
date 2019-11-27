package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * This activity prompts the user to either select to
 * view his/her own events or to track his/her friends
 *
 * @author xuhf0429
 */

public class MoodActivity extends AppCompatActivity {

    private String username = null;//there is no username signed up yet

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        username = this.getIntent().getStringExtra("username"); //save the username signed up, the data is stored

        //click on View My Mood button
        ((TextView) findViewById(R.id.idMyMoodHistory)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoodActivity.this, MoodHistoryActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        //click on View My Friend's Mood button
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
