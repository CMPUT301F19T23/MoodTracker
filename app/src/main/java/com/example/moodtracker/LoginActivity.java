package com.example.moodtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moodtracker.bean.DataUtil;

public class LoginActivity extends AppCompatActivity {

    private boolean bPermission = false;
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        for (int i = 1; i < 4; i++) {
            DataUtil.register(i + "", i + "");
        }

        request_permission();

        etUsername = (EditText) findViewById(R.id.idUsername);
        etPassword = (EditText) findViewById(R.id.idPassword);

        ((TextView) findViewById(R.id.idLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getEditableText().toString();
                String password = etPassword.getEditableText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "username or password is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (DataUtil.login(username, password)) {
                    etUsername.setText("");
                    etPassword.setText("");

                    Intent intent = new Intent(LoginActivity.this, MoodActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "username or password is error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((TextView) findViewById(R.id.idCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
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
