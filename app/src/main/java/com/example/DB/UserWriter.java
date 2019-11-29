package com.example.DB;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class UserWriter extends DBCommunicator {
    private String dbStart = "Users/";
    private String email;

    private boolean failDueToNotUnique = false;

    public boolean passDueToSearch() {
        return passDueToSearch;
    }

    private boolean passDueToSearch = false;

    public UserWriter(Application application) {
        super(application);
        // just so we don't get null pointer exceptions, UserWriter shouldn't be used this way.
        setEmail("null");
    }

    public void setEmail(String email){
        this.email = email;
    }

    private void createUser(String username, String usertype){
        HashMap<String, String> user = new HashMap<>();
        user.put("userType", usertype);
        user.put("userName", username);
        addIfUnique(dbStart, "userName", username, email, user);
    }

    public void createParticipant(String username){
            createUser(username, "Participant");
    }

    public void checkUserExists(String username){
        searchFor(dbStart, "userName", username);
    }

    @Override
    protected boolean onSuccessfulSearchForAdd(QuerySnapshot qs){
        if(qs.size() != 0){
            Log.d(TAG, "Username already exists");
            failDueToNotUnique = true;
            success.setValue(new Boolean(false));
            return false;
        }

        Log.d(TAG, "Username does not yet exist");
        return true;
    }

    @Override
    protected void onFailedSearchForAdd(@NonNull Exception e){
        Log.d(TAG, "Error getting documents: " + e.toString());
        failDueToNotUnique = false;
        success.setValue(new Boolean(false));
    }

    @Override
    protected void onSuccessfulAddition(){
        Log.d(TAG, "User Successfully created");
        passDueToSearch = false;
        success.setValue(new Boolean(true));
    }

    @Override
    protected void onFailedAddition(@NonNull Exception e){
        Log.d(TAG, "User creation failed " + e.toString());
        failDueToNotUnique = false;
        success.setValue(new Boolean(false));
    }

    @Override
    protected void onSuccessfulSearch(QuerySnapshot qs){
        if(qs.size() == 0){
            Log.d(TAG, "Username is unique");
            passDueToSearch = true;
            success.setValue(new Boolean(true));
        }else{
            Log.d(TAG, "Username is not unique");
            failDueToNotUnique = true;
            success.setValue(new Boolean(false));
        }
    }

    @Override
    protected void onFailedSearch(@NonNull Exception e){
        Log.d(TAG, "Error getting documents: " + e.toString());
        failDueToNotUnique = false;
        success.setValue(new Boolean(false));
    }

    public boolean failDueToNotUnique() {
        return failDueToNotUnique;
    }

}
