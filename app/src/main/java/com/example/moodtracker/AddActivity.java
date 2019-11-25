package com.example.moodtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;



import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.DB.MoodWriter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    private CheckBox cb;

    int s1 = -1, s2 = -1;
    private Spinner moodSpinner, situationSpinner;
    private List<String> moodList = new ArrayList<String>();
    private List<String> situationList = new ArrayList<String>();
    private MyAdapter<String> moodAdapter, situationAdapter;

    private EditText nameField;
    private TextView dateField, timeField;

    private Calendar cal = null;

    private String reason = "";
    private String image = "";

    //private String userpath;
    private String email;
    //private String moodpath;
    private MoodWriter moodWriter;
    //private FirebaseFirestore db;

    private int failCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //long start = System.currentTimeMillis();

        Intent intent = getIntent();
        //userpath = intent.getStringExtra(LoginActivity.EXTRA_USERPATH);
        email = intent.getStringExtra(LoginActivity.EXTRA_USER);
        //moodpath = userpath + email + "/" + "Moods/";
        moodWriter =  ViewModelProviders.of(this).get(MoodWriter.class);
        moodWriter.init(email);

        //db = FirebaseFirestore.getInstance();
        //System.out.println("ADD ACTIVITY ON CREATE MARKER 0 " + (System.currentTimeMillis()-start)/1000.0);



        //System.out.println("ADD ACTIVITY ON CREATE MARKER 1 " + (System.currentTimeMillis()-start)/1000.0);

        cal = Calendar.getInstance();

        cb = findViewById(R.id.idAttach);

        nameField = findViewById(R.id.name_field);
        dateField = findViewById(R.id.date_field);
        timeField = findViewById(R.id.time_field);

        dateField.setText(MoodEvent.dayFormat.format(cal.getTime()));
        timeField.setText(MoodEvent.timeFormat.format(cal.getTime()));

        //System.out.println("ADD ACTIVITY ON CREATE MARKER 2 " + (System.currentTimeMillis()-start)/1000.0);

        initSpinnerData();

        //System.out.println("ADD ACTIVITY ON CREATE MARKER 3 " + (System.currentTimeMillis()-start)/1000.0);

        moodSpinner = findViewById(R.id.mood_spinner);
        moodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    //String keshi = moodList.get(position);
                    s1 = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        situationSpinner = findViewById(R.id.situation_spinner);
        situationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    s2 = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //System.out.println("ADD ACTIVITY ON CREATE MARKER 4 " + (System.currentTimeMillis()-start)/1000.0);


        // 声明一个ArrayAdapter用于存放简单数据
        moodAdapter = new MyAdapter<>(
                AddActivity.this, android.R.layout.simple_spinner_item,
                moodList);
        // 把定义好的Adapter设定到spinner中
        moodSpinner.setAdapter(moodAdapter);
        moodSpinner.setSelection(moodList.size() - 1, true);

        // 声明一个ArrayAdapter用于存放简单数据
        situationAdapter = new MyAdapter<>(
                AddActivity.this, android.R.layout.simple_spinner_item,
                situationList);
        // 把定义好的Adapter设定到spinner中
        situationSpinner.setAdapter(situationAdapter);
        situationSpinner.setSelection(situationList.size() - 1, true);

        //System.out.println("ADD ACTIVITY ON CREATE MARKER 5 " + (System.currentTimeMillis()-start)/1000.0);

        findViewById(R.id.option_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, OptionActivity.class);
                startActivityForResult(intent, 1001);
            }
        });

        //System.out.println("ADD ACTIVITY ON CREATE MARKER 6 " + (System.currentTimeMillis()-start)/1000.0);

        findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirm();
            }
        });

        //System.out.println("ADD ACTIVITY ON CREATE MARKER 7 " + (System.currentTimeMillis()-start)/1000.0);
        moodWriter.getSuccess().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                Boolean b = (Boolean)o;
                if(b.booleanValue()){
                    finish();
                }else{
                    if(failCount >= 1){
                        // a bit janky, but have to do because false is returned on create
                        Toast.makeText(AddActivity.this, "Couldn't save mood. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                    ++failCount;
                }
            }
        });
        //System.out.println("ADD ACTIVITY ON CREATE MARKER 9 " + (System.currentTimeMillis()-start)/1000.0);
        //System.out.println("ADD ACTIVITY ON CREATE FINAL MARKER " + (System.currentTimeMillis()-start)/1000.0);
    }

    class MyAdapter<T> extends ArrayAdapter {
        public MyAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
            super(context, resource, objects);
        }

        @Override
        public int getCount() {
            int i = super.getCount();
            return i > 0 ? i - 1 : i;
        }
    }

    private void initSpinnerData() {
        // this way, list dynamically grows as we add emotions
        for(int i = 0; i < MoodEvent.MOOD_DATA.length; ++i){
            moodList.add(MoodEvent.MOOD_DATA[i].getEmotion());
        }
        moodList.add("select a mood");

        // this way, list dynamically grows as we add situations
        for(int i = 0; i >= 0; ++i){
            String s = MoodEvent.intToSituation(i);
            if(!s.equals("Error")){
                situationList.add(s);
            }else{break;}
        }
        situationList.add("select a social situation");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            reason = data.getStringExtra("reason");
            image = data.getStringExtra("image");
        }
    }

    private void onConfirm() {
        boolean attach = false;
        if (cb.isChecked()) {
            attach = true;
        }

        String name = nameField.getEditableText().toString();
        if (name.isEmpty()) {
            Toast.makeText(AddActivity.this, "name is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (s1 == -1) {
            Toast.makeText(AddActivity.this, "please choose a mood", Toast.LENGTH_SHORT).show();
            return;
        }
        if (s2 == -1) {
            Toast.makeText(AddActivity.this, "please choose a social situation", Toast.LENGTH_SHORT).show();
            return;
        }

        moodWriter.createAndWriteMood(name, cal, situationList.get(s2), moodList.get(s1), reason);
        //TODO add next lines to createANdWriteMood
        //me.setAttach(attach);
        //me.setImage(image);


        //writeMoodToDB(me);

        //ResUtil.list.add(me);

        //finish();
    }

//    private void writeMoodToDB(MoodEvent mood){
//        HashMap<String, String> moodData = new HashMap<>();
//        moodData.put("mood_name", mood.getName());
//        moodData.put("mood_date", MoodEvent.longFormat.format(mood.getDate().getTime()));
//        moodData.put("mood_situation", mood.getSituation()+"");
//        moodData.put("mood_reason_str", mood.getReasonString());
//        moodData.put("mood_emotion", mood.getEmotion());
//
//        db.document(moodpath + mood.getId())
//                .set(moodData)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "Data addition successful");
//                        Toast.makeText(AddActivity.this, "Data addition successful.", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "Data addition failed " + e.toString());
//                        Toast.makeText(AddActivity.this, "Data addition failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

}
