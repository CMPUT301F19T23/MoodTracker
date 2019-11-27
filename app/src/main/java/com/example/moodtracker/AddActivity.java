package com.example.moodtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

public class AddActivity extends AppCompatActivity {

    private CheckBox cb;

    int s1 = -1, s2 = -1;
    private Spinner mSpinner1, mSpinner2;
    private List<String> mList1 = new ArrayList<String>();
    private List<String> mList2 = new ArrayList<String>();
    private Myadapter<String> adapter1, adapter2;

    private EditText etName;
    private TextView tvDate, tvTime;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private Calendar cal = null;

    private String reason = "";
    private String image = "";

    private String username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        username = this.getIntent().getStringExtra("username");

        cal = Calendar.getInstance();

        cb = (CheckBox) findViewById(R.id.idAttach);

        etName = (EditText) findViewById(R.id.idName);
        tvDate = (TextView) findViewById(R.id.idDate);
        tvTime = (TextView) findViewById(R.id.idTime);

        tvDate.setText(dateFormat.format(cal.getTime()));
        tvTime.setText(timeFormat.format(cal.getTime()));

        initSpinnerData();

        mSpinner1 = (Spinner) findViewById(R.id.idSense);
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

        mSpinner2 = (Spinner) findViewById(R.id.idSituation);
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

        ((TextView) findViewById(R.id.idConfirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ;
            }
        });

        // 声明一个ArrayAdapter用于存放简单数据
        adapter1 = new Myadapter<String>(
                AddActivity.this, android.R.layout.simple_spinner_item,
                mList1);
        // 把定义好的Adapter设定到spinner中
        mSpinner1.setAdapter(adapter1);
        mSpinner1.setSelection(mList1.size() - 1, true);

        // 声明一个ArrayAdapter用于存放简单数据
        adapter2 = new Myadapter<String>(
                AddActivity.this, android.R.layout.simple_spinner_item,
                mList2);
        // 把定义好的Adapter设定到spinner中
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
        me.setUsername(username);

        DataUtil.addData(me);

        finish();
    }

}
