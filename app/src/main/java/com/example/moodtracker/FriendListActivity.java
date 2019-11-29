package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DB.FriendWriter;
import com.example.DB.UserWriter;
import com.example.moodtracker.recycle.FooterViewHolder;
import com.example.moodtracker.recycle.HeaderViewHolder;
import com.example.moodtracker.recycle.ItemViewHolder;
import com.example.moodtracker.recycle.MyRecycleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the friend list of the user
 */

public class FriendListActivity extends AppCompatActivity {

    //set all item to be empty at initial
    private String username = null;
    private String friendUsername = null;
    private String email = null;
    private RecyclerView mRecyclerView1 = null;
    private MyRecycleAdapter<String> recycleAdapter1 = null;


    private List<String> recycleList1 = new ArrayList<String>(); //list of data

    private EditText searchField;

    private TextView friendRequestNumberDisplay;

    private FriendWriter friendWriter;
    private UserWriter userWriter;

    private int failedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        //get data of username and email
        username = this.getIntent().getStringExtra("username");
        email = this.getIntent().getStringExtra("email");

        //asks friendWriter object for data from FriendWriter class
        friendWriter = ViewModelProviders.of(this).get(FriendWriter.class);
        friendWriter.init(email, username);

        userWriter = ViewModelProviders.of(this).get(UserWriter.class);  //asks userWriter object for data from UserWriter class
        userWriter.getSuccess().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                Boolean b = (Boolean)o;
                if(b.booleanValue()){
                    Toast.makeText(FriendListActivity.this, "That User doesn't exist.", Toast.LENGTH_SHORT).show();

                }else{
                    if(failedCount >= 1){
                        // a bit janky, but have to do because false is returned on create

                        if(userWriter.failDueToNotUnique()){
                            searchField.setText("");
                            Intent intent = new Intent(FriendListActivity.this, FriendAskActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("username", username);
                            intent.putExtra("friendUsername", friendUsername);
                            startActivity(intent);
                        }else{
                            Toast.makeText(FriendListActivity.this, "Error. Check your connection.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    ++failedCount;
                }
            }
        });

        friendWriter.getFriendList().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                recycleList1.clear();
                recycleList1.addAll((ArrayList<String>)o);
                recycleAdapter1.notifyDataSetChanged();
                //System.out.println("DATA SET CHANGING");
            }
        });

        friendRequestNumberDisplay = findViewById(R.id.friend_request_number_display);

        //get the request number from database
        friendWriter.getFriendRequestList().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                friendRequestNumberDisplay.setText(((ArrayList<String>)o).size() + "");
            }
        });

        searchField = findViewById(R.id.search_field);

        //click on search button
        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendUsername = searchField.getEditableText().toString();
                if (friendUsername.isEmpty()) {
                    Toast.makeText(FriendListActivity.this, "the search content is empty", Toast.LENGTH_SHORT).show();
                } else if (friendUsername.equals(username)) {
                    Toast.makeText(FriendListActivity.this, "the search username is yourself", Toast.LENGTH_SHORT).show();
                } else {
                    userWriter.checkUserExists(friendUsername);
                }
            }
        });

        //click on the request shown
        findViewById(R.id.view_following_request_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!friendRequestNumberDisplay.getText().toString().equals("0")) {
                    Intent intent = new Intent(FriendListActivity.this, FriendAcceptActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
        });

        //click on view on map button
        findViewById(R.id.view_on_map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ;
            }
        });

        initRecycleView1(); //call the function to display the views of each activity
    }

    /**
     * Set the views of each friend activity
     */
    public void initRecycleView1() {
        //get controller
        mRecyclerView1 = findViewById(R.id.friends_list);

        //set layout format
        mRecyclerView1.setLayoutManager(new LinearLayoutManager(this));  //linear layout
        mRecyclerView1.setHasFixedSize(true);

        //set adapter
        mRecyclerView1.setAdapter(recycleAdapter1 = new MyRecycleAdapter<String>(this,
                -1, null,
                -1, null,
                R.layout.item_mood_friends, recycleList1) {

            @Override
            /**
             * set the header view of the screen
             * @param helper
             * @param obj
             */
            public void convertHeader(HeaderViewHolder helper, Object obj) {
            }

            @Override
            /**
             * set the footer view of the screen
             * @param helper
             * @param obj
             */
            public void convertFooter(FooterViewHolder helper, Object obj) {
            }

            @Override
            /**
             * set and get data for each event item
             * @param item
             * @param helper
             */
            public void convertItem(ItemViewHolder helper, String item) {
                helper.setText(R.id.idName, item); //set the view of friends' events
            }
        });

        //click on and friend event item
        recycleAdapter1.setOnClickListener(new MyRecycleAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(FriendListActivity.this, MoodFriendHistoryActivity.class);
                intent.putExtra("email", email); //remember email data from the friend list
                intent.putExtra("username", username); //remember username data from friend list
                intent.putExtra("friendUsername", recycleList1.get(position)); //remember the index item from friend list
                startActivity(intent);
            }
        });

        //press for long period
        recycleAdapter1.setOnLongClickListener(new MyRecycleAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(View view, int position) {
            }
        });

    }

}
