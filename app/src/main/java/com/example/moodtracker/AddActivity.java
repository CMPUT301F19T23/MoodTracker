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

import com.example.moodtracker.bean.ResUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This is the activity that allows the user to add a new event, if
 * the user clicks on "add new event" button, which allows the user to enter the name of
 * the event, and select the moods and the social situation of the events from
 * the dropdown list.
 *
 * @author xuhf0429
 */

public class AddActivity extends AppCompatActivity {

    private CheckBox cb;

    int s1 = -1, s2 = -1;//suppose there is no object in the spinner object
    private Spinner mSpinner1, mSpinner2;
    private List<String> mList1 = new ArrayList<String>();
    private List<String> mList2 = new ArrayList<String>();
    private Myadapter<String> adapter1, adapter2;

    private EditText etName;
    private TextView tvDate, tvTime;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//set the current date and time
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");//set the correct formate of time
    private Calendar cal = null;

    private String reason = "";
    private String image = "";

    private String loginUsername = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        cal = Calendar.getInstance();

        cb = (CheckBox) findViewById(R.id.idAttach);

        etName = (EditText) findViewById(R.id.idName);
        tvDate = (TextView) findViewById(R.id.idDate);
        tvTime = (TextView) findViewById(R.id.idTime);

        tvDate.setText(dateFormat.format(cal.getTime()));
        tvTime.setText(timeFormat.format(cal.getTime()));

        initSpinnerData();

        mSpinner1 = (Spinner) findViewById(R.id.idSense);//the first spinner object to be the moods
        mSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    //String keshi = mList1.get(position);
                    s1 = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinner2 = (Spinner) findViewById(R.id.idSituation);//the second spinner object to be the social situations
        mSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        //clicks on the confirm adding button
        ((TextView) findViewById(R.id.idConfirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ;
            }
        });

        // an ArrayAdapter storing simple datas
        adapter1 = new Myadapter<String>(
                AddActivity.this, android.R.layout.simple_spinner_item,
                mList1);
        // set well-defined ArrayAdapter into spinner object.
        mSpinner1.setAdapter(adapter1);
        mSpinner1.setSelection(mList1.size() - 1, true);

        // an ArrayAdapter storing simple datas
        adapter2 = new Myadapter<String>(
                AddActivity.this, android.R.layout.simple_spinner_item,
                mList2);
        // set well-defined ArrayAdapter into spinner object
        mSpinner2.setAdapter(adapter2);
        mSpinner2.setSelection(mList2.size() - 1, true);

        ((TextView) findViewById(R.id.idOption)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, OptionActivity.class);
                startActivityForResult(intent, 1001);
            }
        });

        ((TextView) findViewById(R.id.idConfirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirm();
            }
        });

    }

    class Myadapter<T> extends ArrayAdapter {
        public Myadapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
            super(context, resource, objects);
        }

        @Override
        public int getCount() {
            int i = super.getCount();
            return i > 0 ? i - 1 : i;
        }
    }

    private void initSpinnerData() {
        //angry，happy，sad，neutral
        mList1.add(EmotionData.ANGRY_DATA.getEmotion());
        mList1.add(EmotionData.HAPPY_DATA.getEmotion());
        mList1.add(EmotionData.SAD_DATA.getEmotion());
        mList1.add(EmotionData.NEUTRAL_DATA.getEmotion());
        mList1.add("select a mood");

        //alone，with one other person，with two to several people，with a crowd
        mList2.add("alone");
        mList2.add("with one other person");
        mList2.add("with two to several people");
        mList2.add("with a crowd");
        mList2.add("select a social situation");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

        String name = etName.getEditableText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, "name is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (s1 == -1) {
            Toast.makeText(this, "please choose a mood", Toast.LENGTH_SHORT).show();
            return;
        }
        if (s2 == -1) {
            Toast.makeText(this, "please choose a social situation", Toast.LENGTH_SHORT).show();
            return;
        }

        MoodEvent me = new MoodEvent(cal, mList1.get(s1), reason);
        me.setId(cal.getTimeInMillis() + "");
        me.setAttach(attach);
        me.setEventName(name);
        me.setSituation(mList2.get(s2));
        me.setImage(image);

        ResUtil.list.add(me);

        finish();
    }

}
