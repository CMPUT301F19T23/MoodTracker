package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;

import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.DB.RegisterManager;
import com.example.DB.UserWriter;


public class RegisterActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField, cpassword;
    private EditText emailField;
    private String email;
    private String username;
    private String password;
    private int failCount = 0;

    //FirebaseAuth auth;
    //FirebaseFirestore db;

    private RegisterManager registerManager;
    private UserWriter userWriter;

    private String userPathStr;
    public static final String si_PASSWORD = "com.example.moodtracker.siPASSWORD";	
    public static final String si_EMAIL = "com.example.moodtracker.siEMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        db = FirebaseFirestore.getInstance();
//        auth = FirebaseAuth.getInstance();

        usernameField = findViewById(R.id.username_field);
        passwordField = findViewById(R.id.password_field);
        emailField = findViewById(R.id.email_field);
        cpassword = findViewById(R.id.confirm_password_field);

        Intent intent = getIntent();
        String newEmail  = intent.getStringExtra(LoginActivity.EXTRA_EMAIL);
        String newPassword  = intent.getStringExtra(LoginActivity.EXTRA_PASSWORD);
        emailField.setText(newEmail);
        passwordField.setText(newPassword);

        userPathStr = intent.getStringExtra(LoginActivity.EXTRA_USERPATH);

        registerManager = ViewModelProviders.of(this).get(RegisterManager.class);
        userWriter = ViewModelProviders.of(this).get(UserWriter.class);
        registerManager.init(" ", userWriter);
        userWriter.getSuccess().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                Boolean b = (Boolean)o;
                if(b.booleanValue()){
                    if(!userWriter.passDueToSearch()){
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra(si_EMAIL, email);
                    intent.putExtra(si_PASSWORD, password);
                    startActivity(intent);
                    }else{
                        registerManager.registerParticipant(email, password);
                    }
                }
                else{
                    if(failCount >= 1){
                        // a bit janky, but have to do because false is returned on create

                        if(userWriter.failDueToNotUnique()){
                            Toast.makeText(RegisterActivity.this, "That Username has already been taken.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(RegisterActivity.this, "Couldn't register you. Check your connection.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    ++failCount;
                }
            }
        });

        findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailField.getText().toString().trim();
                password = passwordField.getText().toString();
                username = usernameField.getText().toString().trim();

                if(validate()){
                    registerManager.setUsername(username);
                    userWriter.setEmail(email);
                    userWriter.checkUserExists(username);
//                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                    intent.putExtra(si_EMAIL, email);
//                    intent.putExtra(si_PASSWORD, password);
//                    startActivity(intent);
                }

                //Toast.makeText(RegisterActivity.this, "register success", Toast.LENGTH_SHORT).show();
                //RegisterActivity.this.finish();
                
            }
        });
    }

//    public void registerUser(final String email, final String username, final String password){
//        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    //we will store the additional fields in firebase database
//
//                    Log.d(TAG, "createUserWithEmail:success");
//                    FirebaseUser user = auth.getCurrentUser();
//
//                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                            .setDisplayName(email)
//                            .build();
//
//                    user.updateProfile(profileUpdates)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Log.d(TAG, "User profile updated.");
//                                    }
//                                }
//                            });
//                    HashMap<String, String> users = new HashMap<>();
//                    users.put("userType", "Participant");
//                    users.put("UserName", username);
//                    db.document(userPathStr + email)
//                            .set(users)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d(TAG, "Data addition successful");
//                                    Toast.makeText(RegisterActivity.this, "Data addition successful.", Toast.LENGTH_SHORT).show();
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG, "Data addition failed " + e.toString());
//                                    Toast.makeText(RegisterActivity.this, "Data addition failed", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                }
//                else{
//                    Log.d(TAG, "Failed to create user");
//                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//
//        });
//
//    }

    public boolean validate(){
        String name = usernameField.getText().toString();
        String email = emailField.getText().toString();
        String pwd = passwordField.getText().toString();
        String confirm = cpassword.getText().toString();
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

        if (confirm.isEmpty() || !pwd.equals(confirm)){
            Toast.makeText(RegisterActivity.this, "Password does not match.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

}
