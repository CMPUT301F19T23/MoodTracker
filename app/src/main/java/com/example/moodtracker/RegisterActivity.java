package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private EditText emailField;

    FirebaseAuth auth;
    FirebaseFirestore db;

    private String userPathStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        usernameField = findViewById(R.id.username_field);
        passwordField = findViewById(R.id.password_field);
        emailField = findViewById(R.id.email_field);

        Intent intent = getIntent();
        String newEmail  = intent.getStringExtra(LoginActivity.EXTRA_EMAIL);
        String newPassword  = intent.getStringExtra(LoginActivity.EXTRA_PASSWORD);
        emailField.setText(newEmail);
        passwordField.setText(newPassword);

        userPathStr = intent.getStringExtra(LoginActivity.EXTRA_USERPATH);

        findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailField.getText().toString().trim();
                final String password = passwordField.getText().toString();
                final String username = usernameField.getText().toString().trim();

                if(validate()){
                    registerUser(email, username, password);
                }

                //Toast.makeText(RegisterActivity.this, "register success", Toast.LENGTH_SHORT).show();
                RegisterActivity.this.finish();
            }
        });
    }

    public void registerUser(final String email, final String username, final String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //we will store the additional fields in firebase database

                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = auth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
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
                    HashMap<String, String> users = new HashMap<>();
                    users.put("userType", "Participant");
                    db.document(userPathStr + username)
                            .set(users)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Data addition successful");
                                    Toast.makeText(RegisterActivity.this, "Data addition successful.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Data addition failed " + e.toString());
                                    Toast.makeText(RegisterActivity.this, "Data addition failed", Toast.LENGTH_SHORT).show();
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

    }

    public boolean validate(){
        String name = usernameField.getText().toString();
        String email = emailField.getText().toString();
        String pwd = passwordField.getText().toString();
        //String confirm = cpassword.getText().toString();
        boolean valid = true;

        if (name.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(RegisterActivity.this, "Please enter valid email address.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (pwd.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Please enter password.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        //if (confirm.isEmpty() || !pwd.equals(confirm)){
        //    Toast.makeText(LoginReg.this, "Password does not match.", Toast.LENGTH_SHORT).show();
        //    valid = false;
        //}

        return valid;
    }

}
