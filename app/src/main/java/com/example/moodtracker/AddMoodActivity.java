package com.example.moodtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddMoodActivity extends AppCompatActivity {

    EditText eventNameEditText, eventDateEditText, eventTimeEditText;
    Button optionButton, confirmButton;
    Spinner selectMood, selectSocilSituation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO:Add new event and update the database

                Intent intent = new Intent(AddMoodActivity.this, MoodHistory.class);
                startActivity(intent);
            }
        });

        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AddMoodActivity.this, OptionActivity.class);
                startActivity(intent);
            }
        });
    }
}
