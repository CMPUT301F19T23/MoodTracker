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

/**
 * This function brings the user into add event activity
 * which allows the user to create a mood event
 */
public class AddActivity extends AppCompatActivity {

    private CheckBox cb; //the user can fill in the checkbox by check mark

    int s1 = -1, s2 = -1; //since there is no situation or mood selected
    private Spinner moodSpinner, situationSpinner;
    private List<String> moodList = new ArrayList<String>();
    private List<String> situationList = new ArrayList<String>();
    private MyAdapter<String> moodAdapter, situationAdapter; //set the adapters of the spinners

    private EditText nameField;
    private TextView dateField, timeField;

    private Calendar cal = null; //there is no event created, so no time and date set

    private String reason = ""; //no reason stated
    private String image = ""; //no image selected

    private String email;
    private MoodWriter moodWriter;

    private int failCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Intent intent = getIntent();
        email = intent.getStringExtra(LoginActivity.EXTRA_USER);
        moodWriter =  ViewModelProviders.of(this).get(MoodWriter.class);
        moodWriter.init(email); //initialize the user email into the database and write it into database

        cal = Calendar.getInstance();

        cb = findViewById(R.id.idAttach); //check to attach event to location

        nameField = findViewById(R.id.name_field);
        dateField = findViewById(R.id.date_field);
        timeField = findViewById(R.id.time_field);

        dateField.setText(MoodEvent.dayFormat.format(cal.getTime()));
        timeField.setText(MoodEvent.timeFormat.format(cal.getTime()));

        initSpinnerData(); //show spinners

        //show mood items in mood spinner
        moodSpinner = findViewById(R.id.mood_spinner);
        moodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    s1 = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            } //if nothing then no item index
        });

        //show situation items in situation spinner
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
            } //if nothing then no item index
        });


        // set an adapter to store simple mood data
        moodAdapter = new MyAdapter<>(
                AddActivity.this, android.R.layout.simple_spinner_item,
                moodList);

        // set the defined adapter into mood spinner
        moodSpinner.setAdapter(moodAdapter);
        moodSpinner.setSelection(moodList.size() - 1, true);

        // set an adapter to store simple situation data
        situationAdapter = new MyAdapter<>(
                AddActivity.this, android.R.layout.simple_spinner_item,
                situationList);
        // set the defined adapter into situation spinner
        situationSpinner.setAdapter(situationAdapter);
        situationSpinner.setSelection(situationList.size() - 1, true);

        //click on option button
        findViewById(R.id.option_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, OptionActivity.class);
                intent.putExtra("image", image);
                intent.putExtra("reason", reason);
                startActivityForResult(intent, 1001);
            }
        });

        //click on confirm adding button
        findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirm();
            }
        });

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

    }

    //stores all data
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

    //store data into spinners
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
        //Compare requestCode and resultCode
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            reason = data.getStringExtra("reason");
            image = data.getStringExtra("image");
        }
    }

    //confirm for correctness of details
    private void onConfirm() {
        boolean attach = false;
        if (cb.isChecked()) {
            attach = true;
        } //event is attached to current location

        //check if the reason is empty
        String name = nameField.getEditableText().toString();
        if (name.isEmpty()) {
            Toast.makeText(AddActivity.this, "name is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        //check if mood or situation is selected
        if (s1 == -1) {
            Toast.makeText(AddActivity.this, "please choose a mood", Toast.LENGTH_SHORT).show();
            return;
        }
        if (s2 == -1) {
            Toast.makeText(AddActivity.this, "please choose a social situation", Toast.LENGTH_SHORT).show();
            return;
        }

        //write data of an event into database
        moodWriter.createAndWriteMood(name, cal, situationList.get(s2), moodList.get(s1), reason, image);
    }

}
