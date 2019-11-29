package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.DB.UserWriter;

import java.util.HashMap;

public class MoodActivity extends AppCompatActivity {
    TextView viewProfileButton;
    TextView logoutButton;
    TextView followingButton;	
    TextView  username_text_view;	
    String userpath, email, username;
    private UserWriter userWriter;
    private HashMap<String, String>  map;
    private int failCount = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        viewProfileButton = findViewById(R.id.view_profile_button);
        logoutButton = findViewById(R.id.log_out_button);
        followingButton = findViewById(R.id.view_friends_mood_button);
        username_text_view = findViewById(R.id.hello_username_text_view);	


        Intent oldIntent = getIntent();	
        userpath = oldIntent.getStringExtra(LoginActivity.EXTRA_USERPATH);	
        email = oldIntent.getStringExtra(LoginActivity.EXTRA_USER);

        userWriter = ViewModelProviders.of(this).get(UserWriter.class);
        userWriter.getUsernameFromId(email);

        userWriter.getReturnVal().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                map = (HashMap)o;
                username = map.get("userName");
                username_text_view.setText(username);
            }
        });

        userWriter.getSuccess().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                Boolean b = (Boolean)o;
                if(b.booleanValue()){

                }else{
                    if(failCount >= 1){
                        Toast.makeText(MoodActivity.this, "Couldn't find that user. Check your connection.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    ++failCount;
                }
            }
        });

        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoodActivity.this, MoodHistoryActivity.class);
                // Have to propagate information other classes need but we don't use
                intent.putExtra(LoginActivity.EXTRA_USERPATH, userpath);
                intent.putExtra(LoginActivity.EXTRA_USER, email);
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

        followingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MoodActivity.this, FriendListActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

    }
}
