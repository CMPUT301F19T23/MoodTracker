package com.example.moodtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class EditMoodActivity extends AppCompatActivity {

    EditText editEventNameEditText, editEventDateEditText, editEventOptionEditText;
    Button saveButton, deleteButton;
    Spinner selectMood, selectSocilSituation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mood);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO:Save the event and update to the database

                Intent intent = new Intent(EditMoodActivity.this, MoodHistory.class);
                startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO:Delete the event from the database

                Intent intent = new Intent(EditMoodActivity.this, MoodHistory.class);
                startActivity(intent);
            }
        });
    }
}
