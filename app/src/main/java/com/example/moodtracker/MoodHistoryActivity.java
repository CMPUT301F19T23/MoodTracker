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

public class MoodHistoryActivity extends AppCompatActivity {

    private String username = null;

    private RecyclerView mRecyclerView1 = null;

    private MyRecycleAdapter<MoodEvent> recycleAdapter1 = null;
    private List<MoodEvent> recycleList1 = new ArrayList<MoodEvent>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_history);

        username = this.getIntent().getStringExtra("username");

        ((TextView) findViewById(R.id.idAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoodHistoryActivity.this, AddActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        initRecycleView1();
    }

    @Override
    protected void onResume() {
        super.onResume();

        recycleList1.clear();

        DataUtil.getAll(username, recycleList1);
        recycleAdapter1.notifyDataSetChanged();
    }

    public void initRecycleView1() {
        //1.获取控件
        mRecyclerView1 = (RecyclerView) findViewById(R.id.recycler_view1);

        //2.设置布局方式
        mRecyclerView1.setLayoutManager(new LinearLayoutManager(this));  //线性布局
        //mRecyclerView1.setLayoutManager(new GridLayoutManager(this, 3));  //网格布局
        mRecyclerView1.setHasFixedSize(true);

        //3.设置适配器
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
            public void convertItem(ItemViewHolder helper, MoodEvent item) {
                helper.setText(R.id.idName, item.getEventName());

                helper.setText(R.id.idImage, item.getEmoji());
                helper.getView(R.id.idName).setBackgroundColor(item.getColor());
            }
        });

        recycleAdapter1.setOnClickListener(new MyRecycleAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MoodHistoryActivity.this, EditActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("bean", recycleList1.get(position).getId());
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
