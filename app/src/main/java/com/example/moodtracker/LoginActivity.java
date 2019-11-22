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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    private boolean bPermission = false;
    private EditText emailField;
    private EditText passwordField;

    FirebaseAuth auth;

    public static final String EXTRA_USERPATH = "com.example.moodtracker.USERPATH"; // Filepath to get to the Users database
    private String userPathStr = "Users/";
    public static final String EXTRA_USER = "com.example.moodtracker.USER";
    public static final String EXTRA_EMAIL = "com.example.moodtracker.EMAIL";
    public static final String EXTRA_PASSWORD = "com.example.moodtracker.PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        request_permission();

        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);



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
                attemptLogin(email,password);
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

    public void attemptLogin(String email, String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();

                            Intent intent = new Intent(LoginActivity.this, MoodActivity.class);

                            intent.putExtra(EXTRA_USERPATH, userPathStr);
                            Log.d("Extra_userpath", userPathStr);

                            intent.putExtra(EXTRA_USER, user.getDisplayName());
                            Log.d("Extra_user", user.getDisplayName());
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // Set the UI so the user knows login failed
                        }
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
