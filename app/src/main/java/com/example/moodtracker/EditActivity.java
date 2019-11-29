package com.example.moodtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import java.util.HashMap;
import java.util.List;

/**
 * This function allows the user to edit own event details,
 * when clicking on save change button, every changed contents are
 * updated into database
 */

public class EditActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_PHOTO = 1001;

    int moodPos = -1, sitPos = -1; //set the indexes to be none since situation and mood are not selected
    private Spinner moodSpinner, situationSpinner; //lists spinner for mood and situation
    private List<String> moodList = new ArrayList<String>();
    private List<String> situationList = new ArrayList<String>();
    private MyAdapter<String> moodAdapter, situationAdapter; //adapters that stores spinners of moods and situations

    private EditText nameField, reasonField;
    private TextView dateField, timeField;

    private TextView tvSense2; //set the editable emoji

    private Calendar cal = null;

    //set reason and image to be empty since none of them selected
    private String reason = "";
    private String image = "";

    private MoodEvent selectedMoodEvent = null;

    private String email; //get user's email
    private long id; //index

    private RelativeLayout relativeLayout; //set the layout for events

    private MoodWriter moodWriter; //object of MoodWrite class

    private int failCount = 0; //initialize to 0 since no failures yet
    private boolean retrieveFlag = false;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit); //get email of the user from login activity

        Intent intent = getIntent();
        email = intent.getStringExtra(LoginActivity.EXTRA_USER); //store the email data from the login intent
        
        moodWriter = ViewModelProviders.of(this).get(MoodWriter.class);
        moodWriter.init(email);

        moodWriter.getReturnVal().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                retrieveFlag = true;
                selectedMoodEvent = moodWriter.createMoodEvent(id, (HashMap)o);
            }
        });

        moodWriter.getSuccess().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                Boolean b = (Boolean)o;
                if(retrieveFlag){
                    retrieveFlag = false;
                    if(b.booleanValue()){
                        cal = selectedMoodEvent.getDate();
                        dateField.setText(MoodEvent.dayFormat.format(cal.getTime()));
                        timeField.setText(MoodEvent.timeFormat.format(cal.getTime()));
                        initData();
                        sens22();
                    }else{
                        if(failCount >= 1){
                            // a bit janky, but have to do because null is returned on create
                            Toast.makeText(EditActivity.this, "Couldn't load mood. Please try again.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        ++failCount;
                    }
                }
                else{
                    if(b.booleanValue()){
                        finish();
                    }else{
                        if(failCount >= 1){
                            // a bit janky, but have to do because false is returned on create
                            Toast.makeText(EditActivity.this, "Couldn't modify mood. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                        ++failCount;
                    }
                }
            }
        });

        id = Long.parseLong(intent.getStringExtra(MoodHistoryActivity.EXTRA_MOOD));


        moodWriter.getMoodEvent(id); //write the mood event into database


        reasonField = findViewById(R.id.reason_field);

        nameField = findViewById(R.id.name_field);
        dateField = findViewById(R.id.date_field);
        timeField = findViewById(R.id.time_field);

        tvSense2 = findViewById(R.id.idSense2);

        initSpinnerData(); //show spinners
    }
    /**
     * Sets the layout outlook of the event
     */
    private void sens22() {
        String keshi = moodList.get(moodPos); //get the item in the mood list by indexes
        relativeLayout = findViewById(R.id.relativelayout);
        //check event item their layout looks
        if (keshi.equals(EmotionData.ANGRY_DATA.getEmotion())) {
            tvSense2.setText(new String(Character.toChars(EmotionData.ANGRY_DATA.getEmoji())));
            tvSense2.setBackgroundColor(EmotionData.ANGRY_DATA.getColor());
            relativeLayout.setBackgroundColor(EmotionData.ANGRY_DATA.getColor());
            nameField.setBackgroundColor(0xFFFFFFFF);
            reasonField.setBackgroundColor(0xFFFFFFFF);
            dateField.setBackgroundColor(0xFFFFFFFF);
            timeField.setBackgroundColor(0xFFFFFFFF);
            moodSpinner.setBackgroundColor(0xFFFFFFFF);
            situationSpinner.setBackgroundColor(0xFFFFFFFF);

        } else if (keshi.equals(EmotionData.HAPPY_DATA.getEmotion())) {
            tvSense2.setText(new String(Character.toChars(EmotionData.HAPPY_DATA.getEmoji())));
            tvSense2.setBackgroundColor(EmotionData.HAPPY_DATA.getColor());
            relativeLayout.setBackgroundColor(EmotionData.HAPPY_DATA.getColor());
            nameField.setBackgroundColor(0xFFFFFFFF);
            reasonField.setBackgroundColor(0xFFFFFFFF);
            dateField.setBackgroundColor(0xFFFFFFFF);
            timeField.setBackgroundColor(0xFFFFFFFF);
            moodSpinner.setBackgroundColor(0xFFFFFFFF);
            situationSpinner.setBackgroundColor(0xFFFFFFFF);

        } else if (keshi.equals(EmotionData.SAD_DATA.getEmotion())) {
            tvSense2.setText(new String(Character.toChars(EmotionData.SAD_DATA.getEmoji())));
            tvSense2.setBackgroundColor(EmotionData.SAD_DATA.getColor());
            relativeLayout.setBackgroundColor(EmotionData.SAD_DATA.getColor());
            nameField.setBackgroundColor(0xFFFFFFFF);
            reasonField.setBackgroundColor(0xFFFFFFFF);
            dateField.setBackgroundColor(0xFFFFFFFF);
            timeField.setBackgroundColor(0xFFFFFFFF);
            moodSpinner.setBackgroundColor(0xFFFFFFFF);
            situationSpinner.setBackgroundColor(0xFFFFFFFF);

        } else if (keshi.equals(EmotionData.NEUTRAL_DATA.getEmotion())) {
            tvSense2.setText(new String(Character.toChars(EmotionData.NEUTRAL_DATA.getEmoji())));
            tvSense2.setBackgroundColor(EmotionData.NEUTRAL_DATA.getColor());
            relativeLayout.setBackgroundColor(EmotionData.NEUTRAL_DATA.getColor());
            nameField.setBackgroundColor(0xFFFFFFFF);
            reasonField.setBackgroundColor(0xFFFFFFFF);
            dateField.setBackgroundColor(0xFFFFFFFF);
            timeField.setBackgroundColor(0xFFFFFFFF);
            moodSpinner.setBackgroundColor(0xFFFFFFFF);
            situationSpinner.setBackgroundColor(0xFFFFFFFF);

        }
    }

    /**
     * Sets the general background layout for all events
     * associate with different moods
     */
    private void sens2() {

        tvSense2.setText(new String(Character.toChars(selectedMoodEvent.getEmoji())));
        tvSense2.setBackgroundColor(selectedMoodEvent.getColor());
    }

    /**
     * Stores all datas
     * @param <T>
     */
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

    /**
     * get items of mood and situation spinners by indexes
     */
    private void initData() {
        moodSpinner = findViewById(R.id.mood_spinner);
        moodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    moodPos = position; //get the index of a mood
                    sens22(); //get its layout

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
                    sitPos = position; //get the index of a situation
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //state array adapter to store simple data
        moodAdapter = new MyAdapter<>(
                EditActivity.this, android.R.layout.simple_spinner_item,
                moodList);
        //  set the defined adapter into mood spinner
        moodSpinner.setAdapter(moodAdapter);
        moodSpinner.setSelection(0);

        // state array adapter to store simple data
        situationAdapter = new MyAdapter<>(
                EditActivity.this, android.R.layout.simple_spinner_item,
                situationList);
        // set the defined adapter into situation spinner
        situationSpinner.setAdapter(situationAdapter);
        situationSpinner.setSelection(0);

        image = selectedMoodEvent.getImage(); //get image of event

        nameField.setText(selectedMoodEvent.getName());
        reasonField.setText(selectedMoodEvent.getReasonString());

        //select the item in the mood spinner
        for (int i = 0; i < moodList.size(); i++) {
            if (moodList.get(i).equals(selectedMoodEvent.getEmotion())) {
                moodSpinner.setSelection(i);
                moodPos = i;
                break;
            }
        }

        //select the item in the situation spinner
        for (int i = 0; i < situationList.size(); i++) {
            if (situationList.get(i).equals(MoodEvent.intToSituation(selectedMoodEvent.getSituation()))) {
                situationSpinner.setSelection(i);
                sitPos = i;
                break;
            }
        }

        //view the attached image for reason
        findViewById(R.id.idViewImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMoodEvent.getImage().isEmpty()) {
                    Toast.makeText(EditActivity.this, "picture does not exist.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(EditActivity.this, ViewPictureActivity.class);
                intent.putExtra("image", selectedMoodEvent.getImage());
                startActivity(intent);
            }
        });

        //click on change image button
        findViewById(R.id.idChangeImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PHOTO);
            }
        });

        //click on save change button
        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean attach = false;

                //check if the name of the event is empty
                String name = nameField.getEditableText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(EditActivity.this, "name is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check the reason conditions, if it is more than 3 words or 20 characters.
                reason = reasonField.getEditableText().toString();
                if (!reason.isEmpty()) {
                    String[] names = reason.split(" ");
                    if (names.length > 3) {
                        Toast.makeText(EditActivity.this, "word count is more than 3", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (reason.length() > 20) {
                        Toast.makeText(EditActivity.this, "there are more than 20 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (moodPos == -1) {// if no mood selected
                    Toast.makeText(EditActivity.this, "please choose a mood", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (sitPos == -1) {// if no situation selected
                    Toast.makeText(EditActivity.this, "please choose a social situation", Toast.LENGTH_SHORT).show();
                    return;
                }

                moodWriter.updateMood(name, id, situationList.get(sitPos), cal, moodList.get(moodPos), reason, image, latitude, longitude);

            }
        });

        //click on the delete button
        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodWriter.deleteMoodEvent(id);
            }
        });

    }

    /**initialize the spinners since no mood or situation is selected
     */
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
    /**
     * comparison between requestCodes and resultCode
     */
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //compare requestCode and resultCode
        if (requestCode == REQUEST_IMAGE_PHOTO && resultCode == RESULT_OK) {//select the image from the shop
            //zip image
            showPic(resultCode, data);
        }
    }

    /**
     * call android photo store, show the selected image
     * @param resultCode
     * @param data
     */
    private void showPic(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//if the code of the image is correct
            if (data != null) {
                Uri uri = data.getData();//get stored data from the database
                if (uri != null) {
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    //selected an image, cursor only has one record
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            String path = cursor.getString(cursor.getColumnIndex("_data"));//get the path field
                            image = path;
                        }
                    }
                }
            }
        } else {//if url is null, then cursor gets no data
            Log.d("OptionActivity", "give up selection");
        }
    }

}
