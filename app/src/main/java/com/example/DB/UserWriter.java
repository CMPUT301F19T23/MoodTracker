package com.example.DB;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.QuerySnapshot;

import static com.example.DB.DBConsts.dbStart;
import static com.example.DB.DBConsts.participant;
import static com.example.DB.DBConsts.usertypeField;
import static com.example.DB.DBConsts.usernameField;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Handles searches for users and creating their database entries
 */
public class UserWriter extends DBCommunicator {
    private String email;
    private boolean searchingForId = false;

    private MutableLiveData<String> id;

    private boolean failDueToNotUnique = false;

    private boolean passDueToSearch = false;

    public UserWriter(Application application) {
        super(application);
        id = new MutableLiveData<>("");
        // just to avoid null pointer exceptions UserWriter shouldn't be used without initializing.
        setEmail("null");
    }

    /**
     * Set the email of this user
     * @param email
     *      the email address of the user
     */
    public void setEmail(String email){
        this.email = email;
    }

    /**
     * Runs an addIfUnique query on username and a hashmap containing usertype and username.
     * If there aren't any entries already with that
     * @param username
     *      the username of the user to be created
     * @param usertype
     *      the user type of the user to be created
     */
    private void createUser(String username, String usertype){
        HashMap<String, String> user = new HashMap<>();
        user.put(usertypeField, usertype);
        user.put(usernameField, username);
        searchingForId = false;
        addIfUnique(dbStart, usernameField, username, email, user);
    }

    /**
     * Call create user with usertype equal to participant
     * @param username
     *      the username of the user to be created
     */
    public void createParticipant(String username){
            createUser(username, participant);
    }

    /**
     * Run a search for query on a given username
     * @param username
     *      Username of user to be searched
     */
    public void checkUserExists(String username){
        searchingForId = false;
        searchFor(dbStart, usernameField, username);
    }

    /**
     * Runs a modified search that puts the id of a document into the id LiveData object
     * @param username
     *      Username of user to be searched
     */
    public void getIdFromUsername(String username){
        searchingForId = true;
        searchFor(dbStart, usernameField, username);
    }

    /**
     * Runs a get Data query on id
     * @param id
     *      the id(email address) to search through
     */
    public void getUsernameFromId(String id){
        searchingForId = false;
        getData(dbStart, id);
    }


    /**
     * Called when a n addIfUnique query meets a breakpoint and did not encounter any errors
     * @param qs
     *      the QuerySnapshot holding information about the query.
     * @return
     *      Whether or not qs is empty
     */
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

    /** Called if an addIfUnique query encounters an error.
     */
    @Override
    protected void onFailedSearchForAdd(@NonNull Exception e){
        Log.d(TAG, "Error getting documents: " + e.toString());
        failDueToNotUnique = false;
        success.setValue(new Boolean(false));
    }

    /**
     * Called when a set operation occurs without encountering errors
     */
    @Override
    protected void onSuccessfulAddition(){
        Log.d(TAG, "User Successfully created");
        passDueToSearch = false;
        success.setValue(new Boolean(true));
    }

    /**
     * Called when a set data query encounters an error.
     */
    @Override
    protected void onFailedAddition(@NonNull Exception e){
        Log.d(TAG, "User creation failed " + e.toString());
        failDueToNotUnique = false;
        success.setValue(new Boolean(false));
    }

    /**
     *Called when a search query runs and doesn't encounter an exception
     */
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

    /**
     * Called when a search query encounters an exception
     */
    @Override
    protected void onFailedSearch(@NonNull Exception e){
        Log.d(TAG, "Error getting documents: " + e.toString());
        failDueToNotUnique = false;
        success.setValue(new Boolean(false));
    }

    /** Called when a get data query runs and doesn't encounter an error
     * @param map
     *      HashMap containing the data that was retrieved.
     */
    protected void onSuccessfulDataRetrieval(Map<String, Object> map){
        Log.d(TAG, "Username successfully retrieved");
        HashMap<String,String> hashMap = (HashMap) map;
        returnVal.setValue(hashMap);
        success.setValue(new Boolean(true));
    }

    /**
     * Called when a get data query runs and encounters an error
     */
    protected void onFailedDataRetrieval(@NonNull Exception e){
        Log.d(TAG, "Couldn't find User with that ID: " + e.toString());
        returnVal.setValue(null);
        success.setValue(new Boolean(false));
    }


    /**
     * Allows UI to have better distinguish between various situations
     * @return
     *      returns whether or not the last relevant operation failed checking to see if something was unique
     */
    public boolean failDueToNotUnique() {
        return failDueToNotUnique;
    }

    /**
     * Allows UI to have better distinguish between various situations
     * @return
     *      returns whether or not the last relevant operation passed during a search query
     */
    public boolean passDueToSearch() {
        return passDueToSearch;
    }

    /**
     * Returns access to a changing object thaat holds the last id read from getIdFromUsername queries.
     * @return
     */
    public LiveData<String> getId() {
        return id;
    }

}
