package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.DB.FriendMoodReader;

import java.util.Calendar;
import java.util.HashMap;

public class FriendEditActivity extends AppCompatActivity {

    private String friendUsername = null;
    private long id;

    private final int REQUEST_IMAGE_PHOTO = 1001;

    private TextView mSpinner1, mSpinner2;

    private TextView etName, etReason;
    private TextView tvDate, tvTime;

    private TextView tvSense2;

    private Calendar cal = null;

    private MoodEvent moodEvent = null;

    private FriendMoodReader friendMoodReader;

    private RelativeLayout relativeLayout;

    private int readerFailCount = 0;
    private boolean retrieveFlag = false;
    private boolean initialized = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_edit);

        friendUsername = this.getIntent().getStringExtra("friendUsername");

        System.out.println("ID equal to: " + this.getIntent().getStringExtra("id"));
        id = Long.parseLong(this.getIntent().getStringExtra("id"));

        friendMoodReader = ViewModelProviders.of(this).get(FriendMoodReader.class);
        friendMoodReader.init(friendUsername);

        friendMoodReader.getReturnVal().observe(this, new Observer(){

            @Override
            public void onChanged(Object o) {
                retrieveFlag = true;
                moodEvent = friendMoodReader.createMoodEvent(id, (HashMap)o);
            }
        });

        friendMoodReader.getSuccess().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                Boolean b = (Boolean)o;
                if(b.booleanValue()){
                    if(!initialized){
                        initialized = true;
                        friendMoodReader.getMoodEvent(id);
                    }else{
                        if(retrieveFlag){
                            retrieveFlag = false;
                            cal = moodEvent.getDate();

                            etReason = findViewById(R.id.idReason);

                            etName = findViewById(R.id.idName);
                            tvDate = findViewById(R.id.idDate);
                            tvTime = findViewById(R.id.idTime);

                            tvSense2 = findViewById(R.id.idSense2);

                            tvDate.setText(MoodEvent.dayFormat.format(cal.getTime()));
                            tvTime.setText(MoodEvent.timeFormat.format(cal.getTime()));

                            mSpinner1 = findViewById(R.id.idSense);

                            mSpinner2 = findViewById(R.id.idSituation);

                            initData();

                            sens2();
                        }
                    }
                }else{
                    if(readerFailCount >= 1){
                        // a bit janky, but have to do because false is returned on create
                        Toast.makeText(FriendEditActivity.this, "Couldn't load that user's data. Check your connection.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    ++readerFailCount;
                }
            }
        });

        //moodEvent = DataUtil.getMoodEvent(friendUsername, id);


    }

    private void sens2() {
        relativeLayout = findViewById(R.id.relativelayout);
        tvSense2.setText(new String(Character.toChars(moodEvent.getEmoji())));
        relativeLayout.setBackgroundColor(moodEvent.getColor());
        etName.setBackgroundColor(0xFFFFFFFF);
        etReason.setBackgroundColor(0xFFFFFFFF);
        tvDate.setBackgroundColor(0xFFFFFFFF);
        tvTime.setBackgroundColor(0xFFFFFFFF);
        mSpinner1.setBackgroundColor(0xFFFFFFFF);
        mSpinner2.setBackgroundColor(0xFFFFFFFF);

    }

    private void initData() {
        etName.setText(moodEvent.getName());
        etReason.setText(moodEvent.getReasonString());

        mSpinner1.setText(moodEvent.getEmotion());
        mSpinner2.setText(MoodEvent.intToSituation(moodEvent.getSituation()));

        String reasonString = moodEvent.getReasonString();
        if (reasonString == null || reasonString.isEmpty()){
            etReason.setVisibility(View.GONE);
        }else{
            etReason.setVisibility(View.VISIBLE);
            etReason.setText(reasonString);
        }

         findViewById(R.id.idViewImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moodEvent.getImage().isEmpty()) {
                    Toast.makeText(FriendEditActivity.this, "picture is not exist.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(FriendEditActivity.this, ViewPictureActivity.class);
                intent.putExtra("image", moodEvent.getImage());
                startActivity(intent);
            }
        });

    }
}
