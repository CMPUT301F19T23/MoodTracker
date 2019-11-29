package com.example.DB;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static android.content.ContentValues.TAG;

public class RegisterManager extends AuthCommunicator {
    private UserWriter userWriter;
    private boolean initialized = false;
    private String username;

    public RegisterManager(Application application) {
        super(application);
        setUsername("");
    }

    public void init(String username, UserWriter userWriter){
        if(!initialized){
            setUsername(username);
            this.userWriter = userWriter;
            initialized = true;
        }
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void registerParticipant(String email, String password){
        createAccount(email, password);
    }

    @Override
    protected void onSuccessfulAccountCreation(String email){
        Log.d(TAG, "Successfully created user");
        FirebaseUser user = auth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(email)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            onInitializeUser();
                        }else{
                            onFailInitializeUser();
                        }
                    }
                });
    }

    public void onInitializeUser(){
        Log.d(TAG, "User profile initialized.");
        userWriter.createParticipant(username);
    }

    public void onFailInitializeUser(){
        Log.d(TAG, "User profile could not be initialized.");
        success.setValue(new Boolean(false));
    }

}
