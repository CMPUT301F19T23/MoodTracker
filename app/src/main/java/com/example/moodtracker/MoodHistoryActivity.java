package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DB.MoodWriter;
import com.example.moodtracker.recycle.FooterViewHolder;
import com.example.moodtracker.recycle.HeaderViewHolder;
import com.example.moodtracker.recycle.ItemViewHolder;
import com.example.moodtracker.recycle.MyRecycleAdapter;

import java.util.ArrayList;
import java.util.List;

public class MoodHistoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView1 = null;

    private MyRecycleAdapter<MoodEvent> recycleAdapter1 = null;
    private List<MoodEvent> moodEventList = new ArrayList<>();
    private List<MoodEvent> displayList = new ArrayList<>();

    private EditText searchField;

    private String email;

    public final static String EXTRA_MOOD = "com.example.moodtracker.EXTRA_MOOD";

    private MoodWriter moodWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_history);


        findViewById(R.id.create_mood_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MoodHistoryActivity.this, AddActivity.class);
                intent.putExtra(LoginActivity.EXTRA_USER, email);
                startActivity(intent);
            }
        });

        initRecycleView1();

        searchField = findViewById(R.id.search_field);
        searchField.addTextChangedListener(new TextChangedListener<EditText>(searchField) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                filterMoods(s.toString());
            }
        });

        Intent intent = getIntent();
        email = intent.getStringExtra(LoginActivity.EXTRA_USER);

        moodWriter = ViewModelProviders.of(this).get(MoodWriter.class);
        moodWriter.init(email);

        moodWriter.getMoodEvents().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                moodEventList.clear();
                moodEventList.addAll((ArrayList<MoodEvent>)o);
                filterMoods(searchField.getText().toString());
            }
        });

    }

    public abstract class TextChangedListener<T> implements TextWatcher {
        // code from: https://stackoverflow.com/questions/11134144/android-edittext-onchange-listener
        private T target;

        public TextChangedListener(T target) {
            this.target = target;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            this.onTextChanged(target, s);
        }

        public abstract void onTextChanged(T target, Editable s);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //moodEventList.clear();
        //moodEventList.addAll(ResUtil.list);
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
        mRecyclerView1.setAdapter(recycleAdapter1 = new MyRecycleAdapter<MoodEvent>(this,
                -1, null,
                -1, null,
                R.layout.item_mood_history, displayList) {

            @Override
            public void convertHeader(HeaderViewHolder helper, Object obj) {
            }

            @Override
            public void convertFooter(FooterViewHolder helper, Object obj) {
            }

            @Override
            public void convertItem(ItemViewHolder helper, MoodEvent item) {
                helper.setText(R.id.name_field, item.getName() + "\n" + MoodEvent.longFormat.format(item.getDate().getTime()));

                helper.setText(R.id.idImage, new String(Character.toChars(item.getEmoji())));
                helper.getView(R.id.name_field).setBackgroundColor(item.getColor());
            }
        });

        recycleAdapter1.setOnClickListener(new MyRecycleAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MoodHistoryActivity.this, EditActivity.class);
                intent.putExtra(LoginActivity.EXTRA_USER, email);
                intent.putExtra(EXTRA_MOOD, displayList.get(position).getId() + "");
                startActivity(intent);
            }
        });

        recycleAdapter1.setOnLongClickListener(new MyRecycleAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(View view, int position) {
            }
        });

    }

    private void filterMoods(String text){
        if(text == null || text.isEmpty()){ displayList.clear(); displayList.addAll(moodEventList); recycleAdapter1.notifyDataSetChanged(); return;}

        displayList.clear();

        for(MoodEvent mood : moodEventList){
            if(mood.getEmotion().equalsIgnoreCase(text)){
                displayList.add(mood);
            }
        }

        recycleAdapter1.notifyDataSetChanged();
    }

}
