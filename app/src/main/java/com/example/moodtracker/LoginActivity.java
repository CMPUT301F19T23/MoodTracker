package com.example.moodtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.DB.LoginManager;

public class LoginActivity extends AppCompatActivity {

    private boolean bPermission = false;
    private EditText emailField;
    private EditText passwordField;
    private int failCount = 0;

    private LoginManager loginManager;

    public static final String EXTRA_USERPATH = "com.example.moodtracker.USERPATH"; // Filepath to get to the Users database
    private String userPathStr = "Users/";
    public static final String EXTRA_USER = "com.example.moodtracker.USER";
    public static final String EXTRA_EMAIL = "com.example.moodtracker.EMAIL";
    public static final String EXTRA_PASSWORD = "com.example.moodtracker.PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        request_permission();

        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);

        Intent intent = getIntent();	
        String siEmail  = intent.getStringExtra(RegisterActivity.si_EMAIL);	
        String siPassword  = intent.getStringExtra(RegisterActivity.si_PASSWORD);

        emailField.setText(siEmail);	
        passwordField.setText(siPassword);

        loginManager = ViewModelProviders.of(this).get(LoginManager.class);
        loginManager.getSuccess().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                Boolean b = (Boolean)o;
                if(b.booleanValue()){
                    Intent intent = new Intent(LoginActivity.this, MoodActivity.class);
                    intent.putExtra(EXTRA_USERPATH, userPathStr);
                    intent.putExtra(EXTRA_USER, emailField.getText().toString());
                    startActivity(intent);
                }
                else{
                    if(failCount >= 1){
                        // a bit janky, but have to do because false is returned on create
                        Toast.makeText(LoginActivity.this, "Failed to sign in. Check email and password.", Toast.LENGTH_SHORT).show();
                    }
                    ++failCount;
                }
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailField.getEditableText().toString().trim();
                final String password = passwordField.getEditableText().toString();
                Log.d("email", email);
                Log.d("password", password);
                if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "username or password is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginManager.logUserIn(email,password);
            }
        });

         findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailField.getEditableText().toString().trim();
                final String password = passwordField.getEditableText().toString();
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra(EXTRA_EMAIL, email);
                intent.putExtra(EXTRA_PASSWORD, password);
                intent.putExtra(EXTRA_USERPATH, userPathStr);
                startActivity(intent);
            }
        });
    }

    private void request_permission() {
        // 拍照及文件权限申请
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 权限还没有授予，进行申请
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 3000);
        } else {
            bPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
        switch (requestCode) {
            case 3000:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        bPermission = false;
                        return;
                    }
                    bPermission = true;
                }
                break;

            default:
                break;
        }
    }

}
