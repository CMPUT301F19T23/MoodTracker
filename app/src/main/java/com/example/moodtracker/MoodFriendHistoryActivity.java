package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DB.FriendMoodReader;
import com.example.DB.FriendWriter;
import com.example.moodtracker.recycle.FooterViewHolder;
import com.example.moodtracker.recycle.HeaderViewHolder;
import com.example.moodtracker.recycle.ItemViewHolder;
import com.example.moodtracker.recycle.MyRecycleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity allows users to view their
 * friend's events
 */

public class MoodFriendHistoryActivity extends AppCompatActivity {
    private String username = null, friendUsername = null, email = null;

    private RecyclerView friendsList = null; //set friend list to be empty at initial

    private FriendWriter friendWriter;
    private FriendMoodReader friendMoodReader;

    private int writerFailCount = 0;
    private int readerFailCount = 0;

    private MyRecycleAdapter<MoodEvent> recycleAdapter1 = null; //no item in the history
    private List<MoodEvent> recycleList1 = new ArrayList<>(); //list of friend item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_friend_history);

        username = this.getIntent().getStringExtra("username"); //store username in the intent
        friendUsername = this.getIntent().getStringExtra("friendUsername"); //store friends' username in the intent
        email = this.getIntent().getStringExtra("email"); //store email in the intent

        ((TextView)findViewById(R.id.idAcceptBelow)).setText(friendUsername.trim() + "'s History");

        friendWriter = ViewModelProviders.of(this).get(FriendWriter.class);
        friendWriter.init(email, username);

        friendWriter.getSuccess().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                Boolean b = (Boolean)o;
                if(b.booleanValue()){
                    finish();
                }else{
                    if(writerFailCount >= 1){
                        // a bit janky, but have to do because false is returned on create
                        Toast.makeText(MoodFriendHistoryActivity.this, "Error. Check your connection.", Toast.LENGTH_SHORT).show();
                    }
                    ++writerFailCount;
                }
            }
        });

        friendMoodReader = ViewModelProviders.of(this).get(FriendMoodReader.class);
        friendMoodReader.init(friendUsername);

        friendMoodReader.getSuccess().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                Boolean b = (Boolean)o;
                if(b.booleanValue()){

                }else{
                    if(readerFailCount >= 1){
                        // a bit janky, but have to do because false is returned on create
                        Toast.makeText(MoodFriendHistoryActivity.this, "Couldn't load that user's data. Check your connection.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    ++readerFailCount;
                }
            }
        });

        friendMoodReader.getMoodEvents().observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                recycleList1.clear();
                recycleList1.addAll((ArrayList<MoodEvent>)o);
                recycleAdapter1.notifyDataSetChanged();
            }
        });

        //click on stop following button
        findViewById(R.id.stop_following_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendWriter.deleteFriend(friendUsername);
            }
        });

        initRecycleView1(); // set the view of the friends activity
    }

    public void initRecycleView1() {
        //get controller
        friendsList = findViewById(R.id.friends_list);

        //set layout format
        friendsList.setLayoutManager(new LinearLayoutManager(this));  //linear layout
        friendsList.setHasFixedSize(true);

        //set adapter
        friendsList.setAdapter(recycleAdapter1 = new MyRecycleAdapter<MoodEvent>(this,
                -1, null,
                -1, null,
                R.layout.item_mood_friend_history, recycleList1) {

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
            public void convertItem(ItemViewHolder helper, MoodEvent item) {
                helper.setText(R.id.idName, item.getName() + "\n" + MoodEvent.longFormat.format(item.getDate().getTime()));
                helper.setText(R.id.idImage, new String(Character.toChars(item.getEmoji())));

                helper.getView(R.id.idName).setBackgroundColor(item.getColor());
                helper.getView(R.id.idImage).setBackgroundColor(item.getColor());
                helper.getView(R.id.mood_item).setBackgroundColor(item.getColor());
            }
        });

        //click on and friend item
        recycleAdapter1.setOnClickListener(new MyRecycleAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MoodFriendHistoryActivity.this, FriendEditActivity.class);
                intent.putExtra("friendUsername", friendUsername);
                intent.putExtra("id", recycleList1.get(position).getId()+"");
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
