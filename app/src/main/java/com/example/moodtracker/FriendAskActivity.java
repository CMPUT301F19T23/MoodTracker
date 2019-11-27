package com.example.moodtracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.moodtracker.bean.DataUtil;

/**
 * This function asks a participant permission to follow
 *
 * @author xuhf0429
 */

public class FriendAskActivity extends AppCompatActivity {

    //both participants are unknown since no following request
    private String username = null;
    private String username2 = null;

    private TextView tvFriend, tvOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_ask);

        //set two usernames for oneself and another participant
        username = this.getIntent().getStringExtra("username");
        username2 = this.getIntent().getStringExtra("username2");

        tvFriend = (TextView) findViewById(R.id.idFriend); //user can enter a friend's name
        tvFriend.setText(username2); //set the friend's name as another participant

        //click on search button to ask permission
        ((TextView) findViewById(R.id.idOption)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtil.insertFriendAsk(username, username2); //send request to the participant in the data
                finish();
            }
        });
    }
}
