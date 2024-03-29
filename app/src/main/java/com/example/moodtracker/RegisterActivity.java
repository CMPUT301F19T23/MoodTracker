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

/**
 * This activity lets the user to register a
 * new account, and the account is stored into database
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField, cpassword;
    private EditText emailField;
    private String email;
    private String username;
    private String password;
    private int writerFailCount = 0;
    private int registerFailCount = 0;

    private RegisterManager registerManager; //object for RegisterManager class
    private UserWriter userWriter; //object for userWriter class

    private String userPathStr;
    public static final String si_PASSWORD = "com.example.moodtracker.siPASSWORD";	
    public static final String si_EMAIL = "com.example.moodtracker.siEMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameField = findViewById(R.id.username_field);
        passwordField = findViewById(R.id.password_field);
        emailField = findViewById(R.id.email_field);
        cpassword = findViewById(R.id.confirm_password_field);

        Intent intent = getIntent();
        String newEmail  = intent.getStringExtra(LoginActivity.EXTRA_EMAIL); //get the email for login
        String newPassword  = intent.getStringExtra(LoginActivity.EXTRA_PASSWORD); //get the password for login
        emailField.setText(newEmail);
        passwordField.setText(newPassword);

        userPathStr = intent.getStringExtra(LoginActivity.EXTRA_USERPATH); //get the user path for registration

        registerManager = ViewModelProviders.of(this).get(RegisterManager.class);
        userWriter = ViewModelProviders.of(this).get(UserWriter.class);
        registerManager.init(" ", userWriter);

        //asks userWriter if data registered successfully stored
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
                    if(writerFailCount >= 1){
                        // a bit janky, but have to do because false is returned on create

                        if(userWriter.failDueToNotUnique()){
                            Toast.makeText(RegisterActivity.this, "That Username has already been taken.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(RegisterActivity.this, "Couldn't register you. Check your connection.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    ++writerFailCount;
                }
            }
        });

        //check if register success
        registerManager.getSuccess().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                Boolean b = (Boolean)o;
                if(b.booleanValue()){

                }else{
                    if(registerFailCount >= 1){
                        Toast.makeText(RegisterActivity.this, "Failed to create account. An account with that email may already exist.", Toast.LENGTH_SHORT).show();
                    }
                    ++registerFailCount;
                }
            }
        });

        //click on sign up button
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
                }


            }
        });
    }

    /**
     * check if the account is valid
     * @return true
     * @return false
     */
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
