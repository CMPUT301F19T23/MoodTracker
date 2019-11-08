package com.example.moodtracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;



import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodtracker.bean.ResUtil;







import java.util.ArrayList;
import java.util.List;

/**
 * This activity is to allow the user view his/her history events.
 *
 * @author xuhf0429
 */

public class MoodHistoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView1 = null;

    private MyRecycleAdapter<MoodEvent> recycleAdapter1 = null;
    private List<MoodEvent> recycleList1 = new ArrayList<MoodEvent>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_history);

        ((TextView) findViewById(R.id.idAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoodHistoryActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        initRecycleView1();
    }

    @Override
    protected void onResume() {
        super.onResume();

        recycleList1.clear();

        recycleList1.addAll(ResUtil.list);
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
                /*
                if (item.getEmotion().toLowerCase().equals("angry")) {
                    helper.setText(R.id.idImage, item.getEmoji());
                    helper.getView(R.id.idName).setBackgroundColor(item.getColor());

                } else if (item.getEmotion().toLowerCase().equals("happy")) {
                    helper.setText(R.id.idImage, item.getEmoji());
                    helper.getView(R.id.idName).setBackgroundColor(item.getColor());

                } else if (item.getEmotion().toLowerCase().equals("sad")) {
                    helper.setText(R.id.idImage, item.getEmoji());
                    helper.getView(R.id.idName).setBackgroundColor(item.getColor());

                } else if (item.getEmotion().toLowerCase().equals("neutral")) {
                    helper.setText(R.id.idImage, item.getEmoji());
                    helper.getView(R.id.idName).setBackgroundColor(item.getColor());

                }
                */
            }
        });

        recycleAdapter1.setOnClickListener(new MyRecycleAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MoodHistoryActivity.this, EditActivity.class);
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
