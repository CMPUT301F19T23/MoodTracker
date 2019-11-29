package com.example.DB;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static android.content.ContentValues.TAG;

/**
 * Manages the task of creating a new account for a user. Cooperates with a UserWriter object as for
 * our purposes, we have to make a database entry as well as an authorization addition.
 */
public class RegisterManager extends AuthCommunicator {
    private UserWriter userWriter;
    private boolean initialized = false;
    private String username;

    public RegisterManager(Application application) {
        super(application);
        // just to avoid null pointer exceptions RegisterManager shouldn't be used without initializing.
        setUsername("");
    }

    /**
     * Init works a bit differently from other DBCommunicators. Requires cooperation with UI to complete.
     * This object requires a UserWriter because we have to make a database insertion as soon as the user is registered.
     * UserWriter should be declared in same file as this object, and can't be null, but doesn't need to be initialized.
     * @param username
     *      Username of this user
     * @param userWriter
     *
     */
    public void init(String username, UserWriter userWriter){
        if(!initialized){
            setUsername(username);
            this.userWriter = userWriter;
            initialized = true;
        }
    }

    /**
     * Sets username of this object to that of the user, so we know where to write into database upon account creation.
     * @param username
     */
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * Runs a createAccount operation using the provided email and password
     * @param email
     *      the email of the new user
     * @param password
     *      the password of the new user
     */
    public void registerParticipant(String email, String password){
        createAccount(email, password);
    }




    /**
     * Called when the createAccount operation completes successfully. Sets the user's displayName to the stored username
     * and calls on the userWriter to add the username to the database
     * @param email
     *      email of the new user
     */
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

    /**
     * Called when the updateProfile method (Firebase's, not ours) runs without encountering errors. Sets success to true.
     */
    public void onInitializeUser(){
        Log.d(TAG, "User profile initialized.");
        userWriter.createParticipant(username);
    }

    /**
     * Called when the updateProfile method (Firebase's, not ours) encounters errors. Sets success to false.
     */
    public void onFailInitializeUser(){
        Log.d(TAG, "User profile could not be initialized.");
        success.setValue(new Boolean(false));
    }

}
