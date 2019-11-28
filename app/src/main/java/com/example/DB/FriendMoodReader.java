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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class FriendMoodReader extends DBCommunicator {

    private String dbStart = "Users/";
    private String userpath;
    private String moodpath;
    private boolean initialized = false;

    final private MutableLiveData<ArrayList<MoodEvent>> moodEvents = new MutableLiveData<>(new ArrayList<MoodEvent>());

    public FriendMoodReader(Application application) {
        super(application);
    }

    public void init(String friendUsername){
        if(!initialized){
            //System.out.println(friendUsername);
            searchFor(dbStart, "userName", friendUsername);
            initialized = true;
        }
    }

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
        //System.out.println("Succesfully created a mood");
        return new MoodEvent(name, id, situation, date, emotion, reason);
    }

    public void getMoodEvent(long id){
        //System.out.println("Starting getMoodEvent");
        getData(moodpath, id+"");
    }

    public MutableLiveData<ArrayList<MoodEvent>> getMoodEvents() {
        return moodEvents;
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
            userpath = dbStart + document.getId() + "/";
            moodpath = userpath + "Moods/";
            linkHistory();
            // assuming unique username property of database hasn't been violated, so we end after first doc
            return;
        }
    }

    @Override
    protected void onSuccessfulDataRetrieval(Map<String, Object> map){
        Log.d(TAG, "MoodEvent successfully retrieved" + map.toString());
        HashMap<String,String> hashMap = (HashMap) map;
        //System.out.print(hashMap);
        returnVal.setValue(hashMap);
        success.setValue(new Boolean(true));
    }

    @Override
    protected void onFailedDataRetrieval(@NonNull Exception e){
        Log.d(TAG, "MoodEvent retrieval failed: " + e.toString());
        returnVal.setValue(null);
        success.setValue(new Boolean(false));
    }
}
