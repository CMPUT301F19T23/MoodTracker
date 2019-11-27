package com.example.moodtracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.moodtracker.bean.DataUtil;

public class FriendAskActivity extends AppCompatActivity {

    private String username = null;
    private String username2 = null;

    private TextView tvFriend, tvOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_ask);

        username = this.getIntent().getStringExtra("username");
        username2 = this.getIntent().getStringExtra("username2");

        tvFriend = (TextView) findViewById(R.id.idFriend);
        tvFriend.setText(username2);

        ((TextView) findViewById(R.id.idOption)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtil.insertFriendAsk(username, username2);
                finish();
            }
        });
    }
}
