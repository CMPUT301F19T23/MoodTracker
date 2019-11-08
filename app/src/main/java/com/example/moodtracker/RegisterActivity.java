package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moodtracker.bean.ResUtil;
import com.example.moodtracker.bean.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * This is the activity that allows user to sign up for his/her account
 * using a specific username and a password.
 *
 * @author xuhf0429
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String userPathStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final String TAG = "UserReg";
        Intent intent = getIntent();
        etUsername = (EditText) findViewById(R.id.idUsername);
        etPassword = (EditText) findViewById(R.id.idPassword);
        userPathStr = intent.getStringExtra(LoginActivity.EXTRA_USERPATH);

        db = FirebaseFirestore.getInstance();

        ((TextView) findViewById(R.id.idCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getEditableText().toString();
                String password = etPassword.getEditableText().toString();


                if (username.isEmpty() || password.isEmpty()) {


                    Toast.makeText(RegisterActivity.this, "username or password is empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                auth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            //we will store the additional fields in firebase database

                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userobj.getUsername())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });


                        }
                        else{
                            Log.d(TAG, "Failed to create user");
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                });

                for (int i = 0; i < ResUtil.listUser.size(); i++) {
                    if (ResUtil.listUser.get(i).getUsername().equals(username)) {
                        Toast.makeText(RegisterActivity.this, "username exist", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                ResUtil.listUser.add(new User(username, password));
                Toast.makeText(RegisterActivity.this, "register success", Toast.LENGTH_SHORT).show();

                RegisterActivity.this.finish();
            }
        });
    }
}
