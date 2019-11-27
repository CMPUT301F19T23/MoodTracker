package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moodtracker.bean.DataUtil;
import com.example.moodtracker.bean.User;
import com.example.moodtracker.recycle.FooterViewHolder;
import com.example.moodtracker.recycle.HeaderViewHolder;
import com.example.moodtracker.recycle.ItemViewHolder;
import com.example.moodtracker.recycle.MyRecycleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * This function shows the list of user's friends
 * @author xuhf0429
 */

public class FriendListActivity extends AppCompatActivity {

    private String username = null;

    private RecyclerView mRecyclerView1 = null;

    private MyRecycleAdapter<String> recycleAdapter1 = null; //there is no friends added, thus no events available
    private List<String> recycleList1 = new ArrayList<String>(); //set a list of friends

    private EditText etSearch;

    private TextView tvAcceptDisplay; //show user to accept the participant

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        username = this.getIntent().getStringExtra("username");

        etSearch = ((EditText) findViewById(R.id.idSearchContent));

        //click on search button to search participant
        ((TextView) findViewById(R.id.idSearchBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strSearchContent = etSearch.getEditableText().toString();
                //check if search username is oneself or no user searched, or username does not exist in the data
                if (strSearchContent.isEmpty()) {
                    Toast.makeText(FriendListActivity.this, "the search content is empty", Toast.LENGTH_SHORT).show();
                } else if (strSearchContent.equals(username)) {
                    Toast.makeText(FriendListActivity.this, "the search username is yourself", Toast.LENGTH_SHORT).show();
                } else {
                    String username2 = DataUtil.getUsernameByUsername(strSearchContent);
                    if (null == username2) {
                        Toast.makeText(FriendListActivity.this, strSearchContent + " is not exsit", Toast.LENGTH_SHORT).show();
                    } else {
                        etSearch.setText("");

                        Intent intent = new Intent(FriendListActivity.this, FriendAskActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("username2", username2);
                        startActivity(intent);
                    }
                }
            }
        });

        tvAcceptDisplay = ((TextView) findViewById(R.id.idAcceptDisplay));

        //click on accept button
        ((TextView) findViewById(R.id.idAccept)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if accept exists, then switch to the accept activity
                if (!tvAcceptDisplay.getText().toString().equals("0")) {
                    Intent intent = new Intent(FriendListActivity.this, FriendAcceptActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });

        //click on view on map button to view event on current location
        ((TextView) findViewById(R.id.idViewOnMap)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ;
            }
        });

        initRecycleView1();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //resets the list
        recycleList1.clear();

        //get the accepted users
        tvAcceptDisplay.setText(String.valueOf(DataUtil.getAskByUsername(username)));

        //reset and get all data
        DataUtil.getFriends(username, recycleList1);
        recycleAdapter1.notifyDataSetChanged();
    }

    public void initRecycleView1() {
        //get controller
        mRecyclerView1 = (RecyclerView) findViewById(R.id.recycler_view1);

        //set layout format
        mRecyclerView1.setLayoutManager(new LinearLayoutManager(this));  //linear layout
        mRecyclerView1.setHasFixedSize(true);  //set fixed size for the layout

        //set adapter
        mRecyclerView1.setAdapter(recycleAdapter1 = new MyRecycleAdapter<String>(this,
                -1, null,
                -1, null,
                R.layout.item_mood_friends, recycleList1) {

            @Override
            public void convertHeader(HeaderViewHolder helper, Object obj) {
            }

            @Override
            public void convertFooter(FooterViewHolder helper, Object obj) {
            }

            @Override
            public void convertItem(ItemViewHolder helper, String item) {
                helper.setText(R.id.idName, item); //set the layout for each friend
            }
        });

        //click on the friend, switch to the friend's event list
        recycleAdapter1.setOnClickListener(new MyRecycleAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(FriendListActivity.this, MoodFriendHistoryActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("username2", recycleList1.get(position));
                startActivity(intent);
            }
        });

        //press button long
        recycleAdapter1.setOnLongClickListener(new MyRecycleAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(View view, int position) {
            }
        });

    }

}
