package com.example.moodtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
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
 * This is the activity that allows the user to edit a given event, if
 * the user clicks on the event, which allows the user to re-enter the name of
 * the event, and select the moods and the social situation of the events from
 * the dropdown list, and updates the details if "save change" button is clicked.
 *
 * @author xuhf0429
 */

public class EditActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_PHOTO = 1001;
    private CheckBox cb;

    int s1 = -1, s2 = -1;
    private Spinner mSpinner1, mSpinner2;
    private List<String> mList1 = new ArrayList<String>();
    private List<String> mList2 = new ArrayList<String>();
    private Myadapter<String> adapter1, adapter2;

    private EditText etName, etReason;
    private TextView tvDate, tvTime;

    private TextView tvSense2;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private Calendar cal = null;

    private String reason = "";
    private String image = "";

    private MoodEvent bean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //bean = (MoodEvent) this.getIntent().getSerializableExtra("bean");
        String id = this.getIntent().getStringExtra("bean");
        for (int i = 0; i < ResUtil.list.size(); i++) {
            if (ResUtil.list.get(i).getId().equals(id)) {
                bean = ResUtil.list.get(i);
                break;
            }
        }

        cal = Calendar.getInstance();

        cb = (CheckBox) findViewById(R.id.idAttach);

        etReason = (EditText) findViewById(R.id.idReason);

        etName = (EditText) findViewById(R.id.idName);
        tvDate = (TextView) findViewById(R.id.idDate);
        tvTime = (TextView) findViewById(R.id.idTime);

        tvSense2 = (TextView) findViewById(R.id.idSense2);

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
                    sens22();
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


        adapter1 = new Myadapter<String>(
                EditActivity.this, android.R.layout.simple_spinner_item,
                mList1);

        mSpinner1.setAdapter(adapter1);
        mSpinner1.setSelection(0);

        adapter2 = new Myadapter<String>(
                EditActivity.this, android.R.layout.simple_spinner_item,
                mList2);

        mSpinner2.setAdapter(adapter2);
        mSpinner2.setSelection(0);

        initData();

        sens2();
    }

    private void sens22() {
        String keshi = mList1.get(s1);
        if (keshi.equals(EmotionData.ANGRY_DATA.getEmotion())) {
            tvSense2.setText(EmotionData.ANGRY_DATA.getEmoji());
            tvSense2.setBackgroundColor(EmotionData.ANGRY_DATA.getColor());

        } else if (keshi.equals(EmotionData.HAPPY_DATA.getEmotion())) {
            tvSense2.setText(EmotionData.HAPPY_DATA.getEmoji());
            tvSense2.setBackgroundColor(EmotionData.HAPPY_DATA.getColor());

        } else if (keshi.equals(EmotionData.SAD_DATA.getEmotion())) {
            tvSense2.setText(EmotionData.SAD_DATA.getEmoji());
            tvSense2.setBackgroundColor(EmotionData.SAD_DATA.getColor());

        } else if (keshi.equals(EmotionData.NEUTRAL_DATA.getEmotion())) {
            tvSense2.setText(EmotionData.NEUTRAL_DATA.getEmoji());
            tvSense2.setBackgroundColor(EmotionData.NEUTRAL_DATA.getColor());

        }
    }

    private void sens2() {

        tvSense2.setText(bean.getEmoji());
        tvSense2.setBackgroundColor(bean.getColor());

        /*
        if (bean.getEmotion().toLowerCase().equals(EmotionData.ANGRY_DATA)) {
            tvSense2.setText(bean.getEmoji());
            tvSense2.setBackgroundColor(bean.getColor());

        } else if (bean.getEmotion().toLowerCase().equals(EmotionData.HAPPY_DATA)) {
            tvSense2.setText(bean.getEmoji());
            tvSense2.setBackgroundColor(bean.getColor());

        } else if (bean.getEmotion().toLowerCase().equals(EmotionData.SAD_DATA)) {
            tvSense2.setText(bean.getEmoji());
            tvSense2.setBackgroundColor(bean.getColor());

        } else if (bean.getEmotion().toLowerCase().equals(EmotionData.NEUTRAL_DATA)) {
            tvSense2.setText(bean.getEmoji());
            tvSense2.setBackgroundColor(bean.getColor());

        }
        */
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

    private void initData() {
        if (bean.isAttach()) {
            cb.setChecked(true);
        } else {
            cb.setChecked(false);
        }

        image = bean.getImage();

        etName.setText(bean.getEventName());
        etReason.setText(bean.getReasonString());

        for (int i = 0; i < mList1.size(); i++) {
            if (mList1.get(i).equals(bean.getEmotion())) {
                mSpinner1.setSelection(i);
                s1 = i;
                break;
            }
        }
        for (int i = 0; i < mList2.size(); i++) {
            if (mList2.get(i).equals(bean.getSituation())) {
                mSpinner2.setSelection(i);
                s2 = i;
                break;
            }
        }

        ((TextView) findViewById(R.id.idViewImage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.getImage().isEmpty()) {
                    Toast.makeText(EditActivity.this, "picture is not exist.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(EditActivity.this, ViewPictureActivity.class);
                intent.putExtra("image", bean.getImage());
                startActivity(intent);
            }
        });

        ((TextView) findViewById(R.id.idChangeImage)).setOnClickListener(new View.OnClickListener() {
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

        ((TextView) findViewById(R.id.idSaveChange)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean attach = false;
                if (cb.isChecked()) {
                    attach = true;
                }

                String name = etName.getEditableText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(EditActivity.this, "name is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                reason = etReason.getEditableText().toString();
                if (!reason.isEmpty()) {
                    String[] names = name.split(" ");
                    if (names.length > 3) {
                        Toast.makeText(EditActivity.this, "word count is more than 20", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (name.length() > 20) {
                        Toast.makeText(EditActivity.this, "char length is more than 20", Toast.LENGTH_SHORT).show();
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
                    if (ResUtil.list.get(i).getId().equals(bean.getId())) {
                        ResUtil.list.get(i).setAttach(attach);
                        ResUtil.list.get(i).setEventName(name);
                        ResUtil.list.get(i).setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                        ResUtil.list.get(i).setEmotion(mList1.get(s1));
                        ResUtil.list.get(i).setSituation(mList2.get(s2));
                        ResUtil.list.get(i).setReasonString(reason);
                        ResUtil.list.get(i).setImage(image);
                        break;
                    }
                }

                finish();
            }
        });

        ((TextView) findViewById(R.id.idDelete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < ResUtil.list.size(); i++) {
                    if (ResUtil.list.get(i).getId().equals(bean.getId())) {
                        ResUtil.list.remove(i);
                        break;
                    }
                }

                finish();
            }
        });

    }

    private void initSpinnerData() {
        //angry，happy，sad，neutral
        //mList1.add("select a mood");
        mList1.add(EmotionData.ANGRY_DATA.getEmotion());
        mList1.add(EmotionData.HAPPY_DATA.getEmotion());
        mList1.add(EmotionData.SAD_DATA.getEmotion());
        mList1.add(EmotionData.NEUTRAL_DATA.getEmotion());

        //alone，with one other person，with two to several people，with a crowd
        //mList2.add("select a social situation");
        mList2.add("alone");
        mList2.add("with one other person");
        mList2.add("with two to several people");
        mList2.add("with a crowd");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PHOTO && resultCode == RESULT_OK) {//从相册选择完图片
            //压缩图片
            /*
            ArrayList<String> images = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);

            image = images.get(0);
            */

            showPic(resultCode, data);
        }
    }

    private void showPic(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            String path = cursor.getString(cursor.getColumnIndex("_data"));
                            image = path;
                        }
                    }
                }
            }
        } else {
            Log.d("OptionActivity", "give up");
        }
    }

}
