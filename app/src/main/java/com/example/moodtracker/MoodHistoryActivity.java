package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;


import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodtracker.bean.ResUtil;
import com.example.moodtracker.recycle.FooterViewHolder;
import com.example.moodtracker.recycle.HeaderViewHolder;
import com.example.moodtracker.recycle.ItemViewHolder;
import com.example.moodtracker.recycle.MyRecycleAdapter;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MoodHistoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView1 = null;

    private MyRecycleAdapter<MoodEvent> recycleAdapter1 = null;
    private List<MoodEvent> moodEventList = new ArrayList<>();
    private List<MoodEvent> displayList = new ArrayList<>();

    private EditText searchField;

    private String userpath;
    private String username;
    private String moodpath;

    public final static String EXTRA_MOOD = "com.example.moodtracker.EXTRA_MOOD";

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_history);


        findViewById(R.id.create_mood_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oldIntent = getIntent();
                Intent intent = new Intent(MoodHistoryActivity.this, AddActivity.class);
                intent.putExtra(LoginActivity.EXTRA_USERPATH, oldIntent.getStringExtra(LoginActivity.EXTRA_USERPATH));
                intent.putExtra(LoginActivity.EXTRA_USER, oldIntent.getStringExtra(LoginActivity.EXTRA_USER));
                startActivity(intent);
            }
        });

        initRecycleView1();

        searchField = findViewById(R.id.search_field);
        searchField.addTextChangedListener(new TextChangedListener<EditText>(searchField) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                System.out.println(s.toString());
                filterMoods(s.toString());
            }
        });

        Intent intent = getIntent();
        userpath = intent.getStringExtra(LoginActivity.EXTRA_USERPATH);
        username = intent.getStringExtra(LoginActivity.EXTRA_USER);
        moodpath = userpath + username + "/" + "Moods/";

        db = FirebaseFirestore.getInstance();

        db.collection(moodpath).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                moodEventList.clear();
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    Map map = doc.getData();
                    long id = Long.parseLong(doc.getId());
                    Calendar date = Calendar.getInstance();
                    try {
                        date.setTime(MoodEvent.longFormat.parse((String) map.get("mood_date")));
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                        Log.d(TAG, "Error in reading date");
                        continue;
                    }

                    String name = (String) map.get("mood_name");
                    String reason = (String) map.get("mood_reason_str");
                    int situation = Integer.parseInt((String) map.get("mood_situation"));
                    String emotion = (String) map.get("mood_emotion");

                    moodEventList.add(new MoodEvent(name, id, situation, date, emotion, reason));
                }
                filterMoods(searchField.getText().toString());
                ResUtil.list = displayList;
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
        mRecyclerView1 = findViewById(R.id.recycler_view1);

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
                helper.setText(R.id.name_field, item.getName());

                helper.setText(R.id.idImage, item.getEmoji());
                helper.getView(R.id.name_field).setBackgroundColor(item.getColor());
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
                Intent oldIntent = getIntent();
                Intent intent = new Intent(MoodHistoryActivity.this, EditActivity.class);
                intent.putExtra(LoginActivity.EXTRA_USERPATH, oldIntent.getStringExtra(LoginActivity.EXTRA_USERPATH));
                intent.putExtra(LoginActivity.EXTRA_USER, oldIntent.getStringExtra(LoginActivity.EXTRA_USER));
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
                System.out.println("adding match");
                displayList.add(mood);
            }
        }

        System.out.println(displayList);
        System.out.println();

        recycleAdapter1.notifyDataSetChanged();
    }

}
