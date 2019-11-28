package com.example.moodtracker;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.DB.FriendWriter;

import java.util.ArrayList;

public class FriendAcceptActivity extends AppCompatActivity {

    private String username = null;
    private String friendUsername = null;
    private String email = null;
    private ArrayList<String> friendRequestList;
    private TextView friendUsernameField;

    private FriendWriter friendWriter;
    private boolean adding = false;
    private boolean deleting = false;
    private int failCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_accept);

        username = this.getIntent().getStringExtra("username");
        email = this.getIntent().getStringExtra("email");

        friendUsernameField = findViewById(R.id.friend_field);

        friendRequestList = new ArrayList<>();

        friendWriter = ViewModelProviders.of(this).get(FriendWriter.class);
        friendWriter.init(email, username);

        friendWriter.getFriendRequestList().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                friendRequestList.clear();
                friendRequestList.addAll((ArrayList<String>)o);
                if(friendRequestList.size() == 0){
                    friendUsername = "";
                }
                else {
                    friendUsername = friendRequestList.get(0);
                }
                friendUsernameField.setText(friendUsername + " asks to follow you");
            }
        });

        friendWriter.getSuccess().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                Boolean b = (Boolean)o;
                if(b.booleanValue()){

                }else {
                    if(failCount >= 1){
                       if(adding){
                           Toast.makeText(FriendAcceptActivity.this, "Couldn't add friend. Check your connection.", Toast.LENGTH_SHORT).show();
                       }else if(deleting){
                           Toast.makeText(FriendAcceptActivity.this, "Couldn't delete friend request. Check your connection.", Toast.LENGTH_SHORT).show();
                       }
                    }
                }
            }
        });


        //final List<String> list = DataUtil.getAskListByUsername(username);


        findViewById(R.id.view_following_request_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DataUtil.insertFriendAccept(username, list.get(0));
//                DataUtil.insertFriendAccept(list.get(0), username);
//                DataUtil.deleteFriendAsk(username, list.get(0));
//                finish();
                if(friendUsername.equals("")){
                }else{
                    adding = true;
                    deleting = false;
                    friendWriter.addFriend(friendUsername);
                }
            }
        });

        findViewById(R.id.idDecline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DataUtil.deleteFriendAsk(username, list.get(0));
//                finish();
                if(friendUsername.equals("")){
                }else{
                    adding = false;
                    deleting = true;
                    friendWriter.deleteFriendRequest(friendUsername);
                }
            }
        });

    }

}
