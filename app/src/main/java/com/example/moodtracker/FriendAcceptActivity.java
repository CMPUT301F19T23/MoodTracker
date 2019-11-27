package com.example.moodtracker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moodtracker.bean.DataUtil;

import java.util.List;

public class FriendAcceptActivity extends AppCompatActivity {

    private String username = null;

    private TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_accept);

        username = this.getIntent().getStringExtra("username");

        tvUsername = (TextView) findViewById(R.id.idFriend);

        final List<String> list = DataUtil.getAskListByUsername(username);
        tvUsername.setText(list.get(0) + " asks to follow you");

        ((TextView) findViewById(R.id.idAccept)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtil.insertFriendAccept(username, list.get(0));
                DataUtil.insertFriendAccept(list.get(0), username);
                DataUtil.deleteFriendAsk(username, list.get(0));
                finish();
            }
        });

        ((TextView) findViewById(R.id.idDecline)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtil.deleteFriendAsk(username, list.get(0));
                finish();
            }
        });

    }

}
