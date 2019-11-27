package com.example.moodtracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moodtracker.bean.DataUtil;
import com.example.moodtracker.bean.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = (EditText) findViewById(R.id.idUsername);
        etPassword = (EditText) findViewById(R.id.idPassword);

        ((TextView) findViewById(R.id.idCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getEditableText().toString();
                String password = etPassword.getEditableText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "username or password is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (DataUtil.register(username,password) == false) {
                    Toast.makeText(RegisterActivity.this, "username exist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "register success", Toast.LENGTH_SHORT).show();

                    RegisterActivity.this.finish();
                }
            }
        });
    }
}
