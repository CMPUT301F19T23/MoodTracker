package com.example.moodtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moodtracker.bean.DataUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FriendEditActivity extends AppCompatActivity {

    private String username2 = null;

    private final int REQUEST_IMAGE_PHOTO = 1001;

    private TextView mSpinner1, mSpinner2;

    private TextView etName, etReason;
    private TextView tvDate, tvTime;

    private TextView tvSense2;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private Calendar cal = null;

    private MoodEvent bean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_edit);

        username2 = this.getIntent().getStringExtra("username2");

        String id = this.getIntent().getStringExtra("bean");
        bean = DataUtil.getMoodEvent(username2, id);

        cal = Calendar.getInstance();

        etReason = (TextView) findViewById(R.id.idReason);

        etName = (TextView) findViewById(R.id.idName);
        tvDate = (TextView) findViewById(R.id.idDate);
        tvTime = (TextView) findViewById(R.id.idTime);

        tvSense2 = (TextView) findViewById(R.id.idSense2);

        tvDate.setText(dateFormat.format(cal.getTime()));
        tvTime.setText(timeFormat.format(cal.getTime()));

        mSpinner1 = (TextView) findViewById(R.id.idSense);

        mSpinner2 = (TextView) findViewById(R.id.idSituation);

        initData();

        sens2();
    }

    private void sens2() {

        tvSense2.setText(bean.getEmoji());
        tvSense2.setBackgroundColor(bean.getColor());

    }

    private void initData() {
        etName.setText(bean.getEventName());
        etReason.setText(bean.getReasonString());

        mSpinner1.setText(bean.getEmotion());
        mSpinner2.setText(bean.getSituation());

        ((TextView) findViewById(R.id.idViewImage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.getImage().isEmpty()) {
                    Toast.makeText(FriendEditActivity.this, "picture is not exist.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(FriendEditActivity.this, ViewPictureActivity.class);
                intent.putExtra("image", bean.getImage());
                startActivity(intent);
            }
        });

    }
}
