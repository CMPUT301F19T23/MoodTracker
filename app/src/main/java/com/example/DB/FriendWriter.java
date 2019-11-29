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

import static com.example.DB.DBConsts.dbStart;
import static com.example.DB.DBConsts.usernameField;
import static com.example.DB.DBConsts.friendTable;
import static com.example.DB.DBConsts.friendRequestTable;

import java.util.ArrayList;
import java.util.HashMap;
import static android.content.ContentValues.TAG;

/**
 * Handles user's sending and accepting/deletion of friends requests and updates their list of friends accordingly
 */
public class FriendWriter extends DBCommunicator {
    private String email;
    private String username;
    private String friendPath;
    private String requestPath;
    private String lastFriendUsername;
    private boolean queryIsRequest = false;
    private boolean initialized = false;
    private boolean writingFriend = false;

    private MutableLiveData<ArrayList<String>> friendList;

    private MutableLiveData<ArrayList<String>> friendRequestList;

    public FriendWriter(Application application) {
        super(application);
        // just to avoid null pointer exceptions FriendWriter shouldn't be used without initializing.
        setUsernameAndEmail("null", "null");
        friendList = new MutableLiveData<>(new ArrayList<String>());
        friendRequestList = new MutableLiveData<>(new ArrayList<String>());
    }

    /**
     * Set up the the relevant paths that the object has to reference for queries. Also sets up LiveData
     * pointing to friends and friend request lists.
     * @param email
     *      email of this user
     * @param username
     *      username of this user
     */
    public void init(String email, String username){
        if(!initialized){
            setUsernameAndEmail(email, username);
            linkFriendsList();
            linkFriendRequestsList();
        }
        initialized = true;
    }

    /**
     * Set user's username and email, modifies stored paths to reflect the database entry with those parameters.
     * Does not call linkFriendsList or linkFriendRequestsList.
     * @param email
     *      email of this user
     * @param username
     *      username of this user
     */
    private void setUsernameAndEmail(String email, String username){
        this.email = email;
        this.username = username;
        friendPath = dbStart + this.email + "/" + friendTable;
        requestPath = dbStart + this.email + "/" + friendRequestTable;
    }

    /**
     * Connects this object's LiveData object to a SnapshotListener of the user's friend list so we can see the updates to it in real time.
     */
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

    /**
     * Connects this object's LiveData object to a SnapshotListener of the user's friend request document so we can see the updates to it in real time.
     */
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

    /**
     * Runs a modified search looking for the User with friendUsername as their username.
     * If that username exists, then call an insert of this user's username on the friend's friend request collection
     * @param friendUsername
     */
    public void addFriendRequest(String friendUsername){
        queryIsRequest = true;
        writingFriend = false;
        searchFor(dbStart, usernameField, friendUsername);
    }

    /**
     * Runs a modified search looking for the User with friendUsername as their username.
     * If that username exists, then call an insert of this user's username on the friend's friend collection.
     * Also deletes the friend request associated with this friend.
     * @param friendUsername
     *      username of the friend
     */
    public void addFriend(String friendUsername){
        queryIsRequest = false;
        lastFriendUsername = friendUsername;
        HashMap map = new HashMap();
        map.put("null", "null");
        writingFriend = true;
        searchFor(dbStart, usernameField, friendUsername);
    }

    /**
     * Removes the friend's username from this user's friend list
     * @param friendUsername
     *      username of the friend
     */
    public void deleteFriend(String friendUsername){
        deleteData(friendPath, friendUsername);
    }

    /**
     * Removes the friend's username from this user's friend list
     * @param friendUsername
     *      username of the friend
     */
    public void deleteFriendRequest(String friendUsername){
        deleteData(requestPath, friendUsername);
    }


    /**
     * Get the LiveData holding the ArrayList of usernames of this user's friends
     * @return
     *      LiveData representing a list of this user's friends
     */
    public LiveData<ArrayList<String>> getFriendList() {
        return friendList;
    }

    /**
     * Get the LiveData holding the ArrayList of usernames of this user's friend requests
     * @return
     */
    public LiveData<ArrayList<String>> getFriendRequestList() {
        return friendRequestList;
    }


    /**
     *  Called when a search query terminates without encountering errors. If the QuerySnapshot is empty,
     *  the query is considered a failure. If a document was found, setData is called inserting the actual friend request.
     * @param qs
     *      QuerySnapshot holding information about the query
     */
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
            if(writingFriend){
                setData(dbStart + document.getId() + "/" + friendTable + username, map);
            }else{
                setData(dbStart + document.getId() + "/" + friendRequestTable + username, map);
            }
            // assuming unique username property of database hasn't been violated, so we end after first doc
            return;
        }
    }

    /**
     * Called when this object writes data without encountering error. If this wrote a friend, then it starts a delete operation
     * on the corresponding friend request. If this wrote a friend request, this sets success to true.
     * */
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

    /**
     * Called when a set query encounters an error. Sets success to false.
     */
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

    /**
     * Called when a deletion operation terminates without errors. Sets success to true.
     */
    @Override
    protected void onSuccessfulDelete(){
        Log.d(TAG, "Friend request deletion successful");
        success.setValue(new Boolean(true));
    }

    /**
     * Called when a deletion operation encounters errors. Sets success to false.
     */
    @Override
    protected void onFailedDelete(@NonNull Exception e){
        Log.d(TAG, "Friend request deletion failed " + e.toString());
        success.setValue(new Boolean(false));
    }



}
