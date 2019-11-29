package com.example.DB;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.moodtracker.MoodEvent;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.DB.DBConsts.dbStart;
import static com.example.DB.DBConsts.moodTable;
import static com.example.DB.DBConsts.usernameField;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * A way for UI to read data from the mood objects of users they are friends with. Does not publicly store or send data
 * representing the friend's email, for security reasons.
 */
public class FriendMoodReader extends DBCommunicator {
    private String userpath;
    private String moodpath;
    private boolean initialized = false;

    final private MutableLiveData<ArrayList<MoodEvent>> moodEvents = new MutableLiveData<>(new ArrayList<MoodEvent>());

    public FriendMoodReader(Application application) {
        super(application);
    }

    /**
     * Begins a modified search query for the friendUsername parameter. If this returns a failure, the UI thread should not proceed
     * and this object should not be used.
     * @param friendUsername
     *      the username of the user whom we are searching for
     */
    public void init(String friendUsername){
        if(!initialized){
            searchFor(dbStart, usernameField, friendUsername);
            initialized = true;
        }
    }

    /**
     * Connects this object's LiveData object to a SnapshotListener of the friend's moods so we can see the updates to it in real time.
     */
    private void linkHistory(){
        db.collection(moodpath).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                ArrayList<MoodEvent> moodEventList = new ArrayList<>();
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    Map map = doc.getData();
                    long id = Long.parseLong(doc.getId());
                    MoodEvent me = createMoodEvent(id, (HashMap) map);
                    if(me == null){
                        continue;
                    }
                    moodEventList.add(0, me);
                }
                moodEvents.setValue(moodEventList);
            }
        });
        Log.d(TAG, "Reader successfully set up.");
        success.setValue(new Boolean(true));
    }

    /**
     * Create a MoodEvent object from the id and a hashmap representing the data in a MoodEvent
     * @param id
     *      the id of the MoodEvent object
     * @param map
     *      the hashmap storing the data representing the MoodEvent
     * @return
     *      the completed MoodEvent, or null if invalid objects were passed
     */
    public MoodEvent createMoodEvent(long id, HashMap map){
        if(map == null){return null;}
        System.out.println("CreateMoodEvent: " + map);
        Calendar date = Calendar.getInstance();
        try {
            if(map.get("mood_date") == null){return null;}
            date.setTime(MoodEvent.longFormat.parse((String) map.get("mood_date")));
        } catch (ParseException ex) {
            ex.printStackTrace();
            Log.d(TAG, "Error in reading date");
            return null;
        }

        String name = (String) map.get("mood_name");
        String reason = (String) map.get("mood_reason_str");
        int situation = Integer.parseInt((String) map.get("mood_situation"));
        String emotion = (String) map.get("mood_emotion");
        return new MoodEvent(name, id, situation, date, emotion, reason);
    }

    /**
     * Run a query to return a moodEvent with a known id
     * @param id
     *      the id to look for
     */
    public void getMoodEvent(long id){
        getData(moodpath, id+"");
    }

    /**
     * Get the updating list of MoodEvents
     * @return
     *      LiveData object wrapping an ArrayList of MoodEvents
     */
    public MutableLiveData<ArrayList<MoodEvent>> getMoodEvents() {
        return moodEvents;
    }

    /**
     * Modified from base search method to increase specificity of Logging, and to get friend user's information so that we
     * can access their MoodEvents
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
            userpath = dbStart + document.getId() + "/";
            moodpath = userpath + moodTable;
            linkHistory();
            // assuming unique username property of database hasn't been violated, so we end after first doc
            return;
        }
    }

    /**
     * Modified from base method to increase specificity of Logging,
     * @param map
     *      map object representing all of a moodEvent's data
     */
    @Override
    protected void onSuccessfulDataRetrieval(Map<String, Object> map){
        Log.d(TAG, "MoodEvent successfully retrieved" + map.toString());
        HashMap<String,String> hashMap = (HashMap) map;
        returnVal.setValue(hashMap);
        success.setValue(new Boolean(true));
    }

    /**
     * Modified from base method to increase specificity of Logging
     */
    @Override
    protected void onFailedDataRetrieval(@NonNull Exception e){
        Log.d(TAG, "MoodEvent retrieval failed: " + e.toString());
        returnVal.setValue(null);
        success.setValue(new Boolean(false));
    }
}
