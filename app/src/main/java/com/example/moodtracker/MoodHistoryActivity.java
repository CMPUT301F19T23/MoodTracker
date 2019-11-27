package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.moodtracker.bean.DataUtil;
import com.example.moodtracker.recycle.FooterViewHolder;
import com.example.moodtracker.recycle.HeaderViewHolder;
import com.example.moodtracker.recycle.ItemViewHolder;
import com.example.moodtracker.recycle.MyRecycleAdapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This function sets the user's events list
 * layout.
 *
 * @author xuhf0429
 */
public class MoodHistoryActivity extends AppCompatActivity {

    private String username = null; //no username since no user adds event

    private RecyclerView mRecyclerView1 = null; //set the layout of event to be empty

    private MyRecycleAdapter<MoodEvent> recycleAdapter1 = null;
    private List<MoodEvent> recycleList1 = new ArrayList<MoodEvent>(); //set a list of mood events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_history);

        username = this.getIntent().getStringExtra("username");

        //click on add new event button to switch into add event activity
        ((TextView) findViewById(R.id.idAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoodHistoryActivity.this, AddActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        initRecycleView1();// initialize the layout format
    }

    @Override
    protected void onResume() {
        super.onResume();

        //resets the list
        recycleList1.clear();

        //reset and get all data
        DataUtil.getAll(username, recycleList1);
        recycleAdapter1.notifyDataSetChanged();
    }

    public void initRecycleView1() {
        //get controller
        mRecyclerView1 = (RecyclerView) findViewById(R.id.recycler_view1);

        //set layout format
        mRecyclerView1.setLayoutManager(new LinearLayoutManager(this));  //linearlayout

        mRecyclerView1.setHasFixedSize(true); //set fixed size for the layout

        //set adapter
        mRecyclerView1.setAdapter(recycleAdapter1 = new MyRecycleAdapter<MoodEvent>(this,
                -1, null,
                -1, null,
                R.layout.item_mood_history, recycleList1) {

            @Override
            public void convertHeader(HeaderViewHolder helper, Object obj) {
            }

            @Override
            public void convertFooter(FooterViewHolder helper, Object obj) {
            }

            @Override
            //set event details layout with their background colors associate with moods and names
            public void convertItem(ItemViewHolder helper, MoodEvent item) {
                helper.setText(R.id.idName, item.getEventName());

                helper.setText(R.id.idImage, item.getEmoji());
                helper.getView(R.id.idName).setBackgroundColor(item.getColor());
            }
        });

        //switch to edit event activity
        recycleAdapter1.setOnClickListener(new MyRecycleAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MoodHistoryActivity.this, EditActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("bean", recycleList1.get(position).getId());
                startActivity(intent);
            }
        });

        //press on the button for long time
        recycleAdapter1.setOnLongClickListener(new MyRecycleAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(View view, int position) {
            }
        });

    }

}
