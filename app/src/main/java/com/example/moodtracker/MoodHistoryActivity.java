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

/**
 * This function allows the user to view his/her event history
 */
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

        //click on add new event button
        findViewById(R.id.create_mood_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MoodHistoryActivity.this, AddActivity.class);
                intent.putExtra(LoginActivity.EXTRA_USER, email);
                startActivity(intent);
            }
        });

        initRecycleView1();

        searchField = findViewById(R.id.search_field); //type in a certain mood to search for its events
        searchField.addTextChangedListener(new TextChangedListener<EditText>(searchField) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                filterMoods(s.toString()); //call the function which filters the list
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

    /**
     * Resets the changed data
     * @param <T>
     */
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
    //resets every content
    protected void onResume() {
        super.onResume();
    }

    /**
     * Sets the view of the screen
     */
    public void initRecycleView1() {
        //get controller
        mRecyclerView1 = findViewById(R.id.friends_list);

        //set layout format
        mRecyclerView1.setLayoutManager(new LinearLayoutManager(this));  //linear layout
        mRecyclerView1.setHasFixedSize(true);

        //set adapter
        mRecyclerView1.setAdapter(recycleAdapter1 = new MyRecycleAdapter<MoodEvent>(this,
                -1, null,
                -1, null,
                R.layout.item_mood_history, displayList) {

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
            public void convertItem(ItemViewHolder helper, MoodEvent item) { //set the view of each item in the mood event list
                helper.setText(R.id.name_field, item.getName() + "\n" + MoodEvent.longFormat.format(item.getDate().getTime()));

                helper.setText(R.id.idImage, new String(Character.toChars(item.getEmoji()))); //set the look of the emoji according to the emotiondata
                helper.getView(R.id.name_field).setBackgroundColor(item.getColor()); //get the background color for each event item
            }
        });

        //click on and event item
        recycleAdapter1.setOnClickListener(new MyRecycleAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MoodHistoryActivity.this, EditActivity.class);
                intent.putExtra(LoginActivity.EXTRA_USER, email);
                intent.putExtra(EXTRA_MOOD, displayList.get(position).getId() + "");
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

    /**
     * filter the mood event list by typing a certain mood
     * @param text
     */
    private void filterMoods(String text){
        if(text == null || text.isEmpty()){ displayList.clear(); displayList.addAll(moodEventList); recycleAdapter1.notifyDataSetChanged(); return;}

        displayList.clear();

        //filter all moods except the one that is selected
        for(MoodEvent mood : moodEventList){
            if(mood.getEmotion().equalsIgnoreCase(text)){
                displayList.add(mood);
            }
        }

        recycleAdapter1.notifyDataSetChanged();
    }

}
