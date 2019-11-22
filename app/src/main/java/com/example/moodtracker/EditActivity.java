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

import com.example.moodtracker.bean.ResUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class EditActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_PHOTO = 1001;
    private CheckBox cb;

    int s1 = -1, s2 = -1;
    private Spinner moodSpinner, situationSpinner;
    private List<String> moodList = new ArrayList<String>();
    private List<String> situationList = new ArrayList<String>();
    private MyAdapter<String> moodAdapter, situationAdapter;

    private EditText nameField, reasonField;
    private TextView dateField, timeField;

    private TextView tvSense2;

    private Calendar cal = null;

    private String reason = "";
    private String image = "";

    private MoodEvent selectedMoodEvent = null;

    private String userpath;
    private String username;
    private String moodpath;

    private RelativeLayout relativeLayout;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        userpath = intent.getStringExtra(LoginActivity.EXTRA_USERPATH);
        username = intent.getStringExtra(LoginActivity.EXTRA_USER);
        moodpath = userpath + username + "/" + "Moods/";

        db = FirebaseFirestore.getInstance();

        //selectedMoodEvent = (MoodEvent) this.getIntent().getSerializableExtra("selectedMoodEvent");
        long id = Long.parseLong(intent.getStringExtra(MoodHistoryActivity.EXTRA_MOOD));
        for (int i = 0; i < ResUtil.list.size(); i++) {
            if (ResUtil.list.get(i).getId() == id) {
                selectedMoodEvent = ResUtil.list.get(i);
                break;
            }
        }

        cal = Calendar.getInstance();

        cb = findViewById(R.id.idAttach);

        reasonField = findViewById(R.id.reason_field);

        nameField = findViewById(R.id.name_field);
        dateField = findViewById(R.id.date_field);
        timeField = findViewById(R.id.time_field);

        tvSense2 = findViewById(R.id.idSense2);

        dateField.setText(MoodEvent.dayFormat.format(cal.getTime()));
        timeField.setText(MoodEvent.timeFormat.format(cal.getTime()));

        initSpinnerData();

        moodSpinner = findViewById(R.id.mood_spinner);
        moodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    //String keshi = moodList.get(position);
                    s1 = position;
                    sens22();
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

        // 声明一个ArrayAdapter用于存放简单数据
        moodAdapter = new MyAdapter<>(
                EditActivity.this, android.R.layout.simple_spinner_item,
                moodList);
        // 把定义好的Adapter设定到spinner中
        moodSpinner.setAdapter(moodAdapter);
        moodSpinner.setSelection(0);

        // 声明一个ArrayAdapter用于存放简单数据
        situationAdapter = new MyAdapter<>(
                EditActivity.this, android.R.layout.simple_spinner_item,
                situationList);
        // 把定义好的Adapter设定到spinner中
        situationSpinner.setAdapter(situationAdapter);
        situationSpinner.setSelection(0);

        initData();

        sens2();
    }

    private void sens22() {
        String keshi = moodList.get(s1);
        relativeLayout = findViewById(R.id.relativelayout);
        if (keshi.equals(EmotionData.ANGRY_DATA.getEmotion())) {
            tvSense2.setText(new String(Character.toChars(EmotionData.ANGRY_DATA.getEmoji())));
            relativeLayout.setBackgroundColor(EmotionData.ANGRY_DATA.getColor());
            nameField.setBackgroundColor(0xFFFFFFFF);
            reasonField.setBackgroundColor(0xFFFFFFFF);
            dateField.setBackgroundColor(0xFFFFFFFF);
            timeField.setBackgroundColor(0xFFFFFFFF);
            moodSpinner.setBackgroundColor(0xFFFFFFFF);
            situationSpinner.setBackgroundColor(0xFFFFFFFF);

        } else if (keshi.equals(EmotionData.HAPPY_DATA.getEmotion())) {
            tvSense2.setText(new String(Character.toChars(EmotionData.HAPPY_DATA.getEmoji())));
            relativeLayout.setBackgroundColor(EmotionData.HAPPY_DATA.getColor());
            nameField.setBackgroundColor(0xFFFFFFFF);
            reasonField.setBackgroundColor(0xFFFFFFFF);
            dateField.setBackgroundColor(0xFFFFFFFF);
            timeField.setBackgroundColor(0xFFFFFFFF);
            moodSpinner.setBackgroundColor(0xFFFFFFFF);
            situationSpinner.setBackgroundColor(0xFFFFFFFF);

        } else if (keshi.equals(EmotionData.SAD_DATA.getEmotion())) {
            tvSense2.setText(new String(Character.toChars(EmotionData.SAD_DATA.getEmoji())));
            relativeLayout.setBackgroundColor(EmotionData.SAD_DATA.getColor());
            nameField.setBackgroundColor(0xFFFFFFFF);
            reasonField.setBackgroundColor(0xFFFFFFFF);
            dateField.setBackgroundColor(0xFFFFFFFF);
            timeField.setBackgroundColor(0xFFFFFFFF);
            moodSpinner.setBackgroundColor(0xFFFFFFFF);
            situationSpinner.setBackgroundColor(0xFFFFFFFF);

        } else if (keshi.equals(EmotionData.NEUTRAL_DATA.getEmotion())) {
            tvSense2.setText(new String(Character.toChars(EmotionData.NEUTRAL_DATA.getEmoji())));
            relativeLayout.setBackgroundColor(EmotionData.NEUTRAL_DATA.getColor());
            nameField.setBackgroundColor(0xFFFFFFFF);
            reasonField.setBackgroundColor(0xFFFFFFFF);
            dateField.setBackgroundColor(0xFFFFFFFF);
            timeField.setBackgroundColor(0xFFFFFFFF);
            moodSpinner.setBackgroundColor(0xFFFFFFFF);
            situationSpinner.setBackgroundColor(0xFFFFFFFF);

        }
    }

    private void sens2() {

        tvSense2.setText(new String(Character.toChars(selectedMoodEvent.getEmoji())));
        tvSense2.setBackgroundColor(selectedMoodEvent.getColor());

        /*
        if (selectedMoodEvent.getEmotion().toLowerCase().equals(EmotionData.ANGRY_DATA)) {
            tvSense2.setText(selectedMoodEvent.getEmoji());
            tvSense2.setBackgroundColor(selectedMoodEvent.getColor());

        } else if (selectedMoodEvent.getEmotion().toLowerCase().equals(EmotionData.HAPPY_DATA)) {
            tvSense2.setText(selectedMoodEvent.getEmoji());
            tvSense2.setBackgroundColor(selectedMoodEvent.getColor());

        } else if (selectedMoodEvent.getEmotion().toLowerCase().equals(EmotionData.SAD_DATA)) {
            tvSense2.setText(selectedMoodEvent.getEmoji());
            tvSense2.setBackgroundColor(selectedMoodEvent.getColor());

        } else if (selectedMoodEvent.getEmotion().toLowerCase().equals(EmotionData.NEUTRAL_DATA)) {
            tvSense2.setText(selectedMoodEvent.getEmoji());
            tvSense2.setBackgroundColor(selectedMoodEvent.getColor());

        }
        */
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

    private void initData() {
        if (selectedMoodEvent.isAttach()) {
            cb.setChecked(true);
        } else {
            cb.setChecked(false);
        }

        image = selectedMoodEvent.getImage();

        nameField.setText(selectedMoodEvent.getName());
        reasonField.setText(selectedMoodEvent.getReasonString());

        for (int i = 0; i < moodList.size(); i++) {
            if (moodList.get(i).equals(selectedMoodEvent.getEmotion())) {
                moodSpinner.setSelection(i);
                s1 = i;
                break;
            }
        }
        for (int i = 0; i < situationList.size(); i++) {
            if (situationList.get(i).equals(selectedMoodEvent.getSituation())) {
                situationSpinner.setSelection(i);
                s2 = i;
                break;
            }
        }

        findViewById(R.id.idViewImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMoodEvent.getImage().isEmpty()) {
                    Toast.makeText(EditActivity.this, "picture is not exist.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(EditActivity.this, ViewPictureActivity.class);
                intent.putExtra("image", selectedMoodEvent.getImage());
                startActivity(intent);
            }
        });

        findViewById(R.id.idChangeImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                MultiImageSelector.create()
                        .showCamera(false)
                        //.count(IMAGE_SIZE - originImages.size() + 1)
                        .count(1)
                        .multi()
                        .start(EditActivity.this, REQUEST_IMAGE_PHOTO);
                        */

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PHOTO);
            }
        });

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean attach = false;
                if (cb.isChecked()) {
                    attach = true;
                }

                String name = nameField.getEditableText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(EditActivity.this, "name is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                reason = reasonField.getEditableText().toString();
                if (!reason.isEmpty()) {
                    String[] names = reason.split(" ");
                    if (names.length > 3) {
                        Toast.makeText(EditActivity.this, "word count is more than 3", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (name.length() > 20) {
                        Toast.makeText(EditActivity.this, "there are more than 20 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (s1 == -1) {
                    Toast.makeText(EditActivity.this, "please choose a mood", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (s2 == -1) {
                    Toast.makeText(EditActivity.this, "please choose a social situation", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (int i = 0; i < ResUtil.list.size(); i++) {
                    if (ResUtil.list.get(i).getId() == selectedMoodEvent.getId()) {
                        MoodEvent mood = ResUtil.list.get(i);
                        mood.setAttach(attach);
                        mood.setName(name);
                        mood.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                        mood.setEmotion(moodList.get(s1));
                        mood.setSituation(MoodEvent.situationToInt(situationList.get(s2)));
                        mood.setReasonString(reason);
                        mood.setImage(image);
                        updateMood(mood);
                        break;
                    }
                }

                finish();
            }
        });

        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < ResUtil.list.size(); i++) {
                    if (ResUtil.list.get(i).getId() == selectedMoodEvent.getId()) {
                        MoodEvent mood = ResUtil.list.get(i);
                        deleteMood(mood);
                        //ResUtil.list.remove(mood);
                        break;
                    }
                }

                finish();
            }
        });

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
            }
        }
        situationList.add("select a social situation");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == REQUEST_IMAGE_PHOTO && resultCode == RESULT_OK) {//从相册选择完图片
            //压缩图片
            /*
            ArrayList<String> images = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);

            image = images.get(0);
            */

            showPic(resultCode, data);
        }
    }

    // 调用android自带图库，显示选中的图片
    private void showPic(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    //选择的就只是一张图片，所以cursor只有一条记录
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            String path = cursor.getString(cursor.getColumnIndex("_data"));//获取相册路径字段
                            image = path;
                        }
                    }
                }
            }
        } else {
            Log.d("OptionActivity", "放弃从相册选择");
        }
    }

    private void updateMood(MoodEvent mood){
        HashMap<String, String> moodData = new HashMap<>();
        moodData.put("mood_name", mood.getName());
        moodData.put("mood_date", MoodEvent.longFormat.format(mood.getDate().getTime()));
        moodData.put("mood_situation", mood.getSituation()+"");
        moodData.put("mood_reason_str", mood.getReasonString());
        moodData.put("mood_emotion", mood.getEmotion());

        db.document(moodpath + mood.getId())
                .set(moodData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data modification successful");
                        Toast.makeText(EditActivity.this, "Data addition successful.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data modification failed " + e.toString());
                        Toast.makeText(EditActivity.this, "Data addition failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteMood(MoodEvent mood){
        db.document(moodpath + mood.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data deletion successful");
                        Toast.makeText(EditActivity.this, "Data addition successful.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data deletion failed " + e.toString());
                        Toast.makeText(EditActivity.this, "Data addition failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
