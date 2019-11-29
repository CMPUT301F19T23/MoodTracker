package com.example.DB;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.QuerySnapshot;

import static com.example.DB.DBConsts.dbStart;
import static com.example.DB.DBConsts.participant;
import static com.example.DB.DBConsts.usertypeField;
import static com.example.DB.DBConsts.usernameField;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class UserWriter extends DBCommunicator {
    private String email;
    private boolean searchingForId = false;
    private MutableLiveData<String> id;

    private boolean failDueToNotUnique = false;

    public boolean passDueToSearch() {
        return passDueToSearch;
    }

    private boolean passDueToSearch = false;

    public UserWriter(Application application) {
        super(application);
        id = new MutableLiveData<>("");
        // just so we don't get null pointer exceptions, UserWriter shouldn't be used this way.
        setEmail("null");
    }

    public void setEmail(String email){
        this.email = email;
    }

    private void createUser(String username, String usertype){
        HashMap<String, String> user = new HashMap<>();
        user.put(usertypeField, usertype);
        user.put(usernameField, username);
        searchingForId = false;
        addIfUnique(dbStart, usernameField, username, email, user);
    }

    public void createParticipant(String username){
            createUser(username, participant);
    }

    public void checkUserExists(String username){
        searchingForId = false;
        searchFor(dbStart, usernameField, username);
    }

    public void getIdFromUsername(String username){
        searchingForId = true;
        searchFor(dbStart, usernameField, username);
    }

    public void getUsernameFromId(String id){
        searchingForId = false;
        getData(dbStart, id);
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
        if(!searchingForId){
            if(qs.size() == 0){
                Log.d(TAG, "Username is unique");
                passDueToSearch = true;
                success.setValue(new Boolean(true));
            }else{
                Log.d(TAG, "Username is not unique");
                failDueToNotUnique = true;
                success.setValue(new Boolean(false));
            }
        }else{
            if(qs.size() == 0){
                Log.d(TAG, "Username not found");
                success.setValue(new Boolean(false));
            }else{
                Log.d(TAG, "Username found");
                id.setValue(qs.getDocuments().get(0).getId());
                passDueToSearch = true;
                success.setValue(new Boolean(true));
            }
        }
    }

    @Override
    protected void onFailedSearch(@NonNull Exception e){
        Log.d(TAG, "Error getting documents: " + e.toString());
        failDueToNotUnique = false;
        success.setValue(new Boolean(false));
    }

    protected void onSuccessfulDataRetrieval(Map<String, Object> map){
        Log.d(TAG, "Username successfully retrieved");
        HashMap<String,String> hashMap = (HashMap) map;
        returnVal.setValue(hashMap);
        success.setValue(new Boolean(true));
    }

    protected void onFailedDataRetrieval(@NonNull Exception e){
        Log.d(TAG, "Couldn't find User with that ID: " + e.toString());
        returnVal.setValue(null);
        success.setValue(new Boolean(false));
    }

    public boolean failDueToNotUnique() {
        return failDueToNotUnique;
    }

}
