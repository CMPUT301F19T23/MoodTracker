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

public class FriendListActivity extends AppCompatActivity {

    private String username = null;
    private String friendUsername = null;
    private String email = null;

    private RecyclerView mRecyclerView1 = null;

    private MyRecycleAdapter<String> recycleAdapter1 = null;
    private List<String> recycleList1 = new ArrayList<String>();

    private EditText searchField;

    private TextView friendRequestNumberDisplay;

    private FriendWriter friendWriter;
    private UserWriter userWriter;

    private int failedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        username = this.getIntent().getStringExtra("username");
        email = this.getIntent().getStringExtra("email");

        friendWriter = ViewModelProviders.of(this).get(FriendWriter.class);
        friendWriter.init(email, username);

        userWriter = ViewModelProviders.of(this).get(UserWriter.class);
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

        friendWriter.getFriendRequestList().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                friendRequestNumberDisplay.setText(((ArrayList<String>)o).size() + "");
            }
        });

        searchField = findViewById(R.id.search_field);

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

        initRecycleView1();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //recycleList1.clear();

        //friendRequestNumberDisplay.setText(String.valueOf(DataUtil.getAskByUsername(username)));

        // TODO updates friend list
        //DataUtil.getFriends(username, recycleList1);
        //recycleAdapter1.notifyDataSetChanged();
    }

    public void initRecycleView1() {
        //1.获取控件
        mRecyclerView1 = findViewById(R.id.friends_list);

        //2.设置布局方式
        mRecyclerView1.setLayoutManager(new LinearLayoutManager(this));  //线性布局
        //mRecyclerView1.setLayoutManager(new GridLayoutManager(this, 3));  //网格布局
        mRecyclerView1.setHasFixedSize(true);

        //3.设置适配器
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
                helper.setText(R.id.idName, item);
            }
        });

        recycleAdapter1.setOnClickListener(new MyRecycleAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(FriendListActivity.this, MoodFriendHistoryActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("username", username);
                intent.putExtra("friendUsername", recycleList1.get(position));
                startActivity(intent);
            }
        });

        recycleAdapter1.setOnLongClickListener(new MyRecycleAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(View view, int position) {
            }
        });

    }

}
