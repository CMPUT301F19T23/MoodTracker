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

/**
 * This is for the user to accept or decline a follower
 * according to the username in the data
 *
 * @author xuhf0429
 */
public class FriendAcceptActivity extends AppCompatActivity {

    private String username = null; //no username since no follower

    private TextView tvUsername; //make the username to be editable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_accept);

        username = this.getIntent().getStringExtra("username"); //get the username with the intent

        tvUsername = (TextView) findViewById(R.id.idFriend);

        final List<String> list = DataUtil.getAskListByUsername(username); //get list of user to follow according to usernames in the data
        tvUsername.setText(list.get(0) + " asks to follow you"); //find the followers who would follow you in the data

        //click on accept button, the follower is accepted
        ((TextView) findViewById(R.id.idAccept)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtil.insertFriendAccept(username, list.get(0)); //get the follower from his/her username
                DataUtil.insertFriendAccept(list.get(0), username); //get the follower with username from the list in the data
                DataUtil.deleteFriendAsk(username, list.get(0)); //delete the follower from the data according to the username
                finish();
            }
        });

        //click on decline button, the follower is declined
        ((TextView) findViewById(R.id.idDecline)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtil.deleteFriendAsk(username, list.get(0)); //delete the follower by his/her username according to the data
                finish();
            }
        });

    }

}
