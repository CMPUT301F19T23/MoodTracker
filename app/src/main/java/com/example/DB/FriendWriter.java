package com.example.DB;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import static android.content.ContentValues.TAG;

public class FriendWriter extends DBCommunicator {
    private String dbStart = "Users/";
    private String email;
    private String username;
    private String friendPath;
    private String requestPath;
    private String lastFriendUsername;
    private boolean queryIsRequest = false;
    private boolean initialized = false;

    private MutableLiveData<ArrayList<String>> friendList;

    private MutableLiveData<ArrayList<String>> friendRequestList;

    public FriendWriter(Application application) {
        super(application);
        setEmail("null", "null");
        friendList = new MutableLiveData<>(new ArrayList<String>());
        friendRequestList = new MutableLiveData<>(new ArrayList<String>());
    }

    public void init(String email, String username){
        if(!initialized){
            setEmail(email, username);
            linkFriendsList();
            linkFriendRequestsList();
        }
        initialized = true;
    }

    private void setEmail(String email, String username){
        this.email = email;
        this.username = username;
        friendPath = dbStart + this.email + "/" + "Friends/";
        requestPath = dbStart + this.email + "/" + "Friend Requests/";
    }

    private void linkFriendsList(){
        db.collection(friendPath).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                ArrayList<String> newFriendList = new ArrayList<>();
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    newFriendList.add(doc.getId());
                }
                friendList.setValue(newFriendList);
            }
        });
    }

    private void linkFriendRequestsList(){
        db.collection(requestPath).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                ArrayList<String> newRequestList = new ArrayList<>();
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    newRequestList.add(doc.getId());
                }
                friendRequestList.setValue(newRequestList);
            }
        });
    }


    public void addFriendRequest(String friendUsername){
        // first check if username exists
        // if so, then overriden onSuccessfulSearch method calls an insert on
        // the friends collection attached to searched username
        queryIsRequest = true;
        searchFor(dbStart, "userName", friendUsername);
    }

    public void addFriend(String friendUsername){
        queryIsRequest = false;
        lastFriendUsername = friendUsername;
        HashMap map = new HashMap();
        map.put("null", "null");
        setData(friendPath + friendUsername, map);
    }

    public void deleteFriend(String friendUsername){
        deleteData(friendPath, friendUsername);
    }

    public void deleteFriendRequest(String friendUsername){
        deleteData(requestPath, friendUsername);
    }

    public LiveData<ArrayList<String>> getFriendList() {
        return friendList;
    }

    public LiveData<ArrayList<String>> getFriendRequestList() {
        return friendRequestList;
    }

    @Override
    protected void onSuccessfulSearch(QuerySnapshot qs){
        if(qs.size() == 0){
            Log.d(TAG, "No User found with that name");
            success.setValue(new Boolean(false));
            return;
        }
        for (QueryDocumentSnapshot document : qs) {
            Log.d(TAG, "Found Friend with email: " + document.getId());
            HashMap map = new HashMap();
            map.put("null", "null");
            setData(dbStart + document.getId() + "/" + "Friend Requests/" + username, map);
            // assuming unique username property of database hasn't been violated, so we end after first doc
            return;
        }
    }

    @Override
    protected void onSuccessfulAddition(){
        if(queryIsRequest){
        Log.d(TAG, "Friend request added");
            success.setValue(new Boolean(true));
        }else{
            Log.d(TAG, "Friend added, deleting friend request");
            deleteData(requestPath, lastFriendUsername);
        }

    }

    @Override
    protected void onFailedAddition(@NonNull Exception e){
        if(queryIsRequest){
            Log.d(TAG, "Friend request failed to add " + e.toString());
        }else{
            Log.d(TAG, "Friend failed to add " + e.toString());
            lastFriendUsername = null;
        }
        success.setValue(new Boolean(false));
    }

    @Override
    protected void onSuccessfulDelete(){
        Log.d(TAG, "Friend request deletion successful");
        success.setValue(new Boolean(true));
    }

    @Override
    protected void onFailedDelete(@NonNull Exception e){
        Log.d(TAG, "Friend request deletion failed " + e.toString());
        success.setValue(new Boolean(false));
    }



}
