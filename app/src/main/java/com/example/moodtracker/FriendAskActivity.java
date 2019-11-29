package com.example.moodtracker;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.DB.FriendWriter;

/**
 * This activity makes the user to ask permission to
 * follow the other participants
 */

public class FriendAskActivity extends AppCompatActivity {

    private String username = null;
    private String friendUsername = null; //no username
    private String email = null;
    private FriendWriter friendWriter; //object of FriendWriter class
    private int failCount = 0;

    private TextView friendField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_ask);

        username = this.getIntent().getStringExtra("username");
        friendUsername = this.getIntent().getStringExtra("friendUsername");
        email = this.getIntent().getStringExtra("email");

        friendField = findViewById(R.id.friend_field);
        friendField.setText(friendUsername); //use username of the participant when searching friends

        friendWriter = ViewModelProviders.of(this).get(FriendWriter.class);
        friendWriter.init(email, username); //write initial data of email and username into friend database

        //click on permission button
        findViewById(R.id.permission_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendWriter.addFriendRequest(friendUsername);
            }
        });

        friendWriter.getSuccess().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                Boolean b = (Boolean)o;
                if(b.booleanValue()){
                    finish();
                }else{
                    if(failCount >= 1){
                        // a bit janky, but have to do because false is returned on create
                        Toast.makeText(FriendAskActivity.this, "Error. Check your connection.", Toast.LENGTH_SHORT).show();
                    }
                    ++failCount;
                }
            }
        });
    }
}
