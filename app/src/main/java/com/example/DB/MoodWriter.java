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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MoodWriter extends DBCommunicator {
    private String userpath;
    private String moodpath;
    private boolean initalized = false;

    final private MutableLiveData<ArrayList<MoodEvent>> moodEvents = new MutableLiveData<>(new ArrayList<MoodEvent>());

     public MoodWriter(Application application){
        super(application);
        setEmail("null");
    }

    public void init(String email){
         if(!initalized){
             setEmail(email);
             linkHistory();
             initalized = true;
         }
    }

    private void setEmail(String email){
         if(email != null){
            userpath = dbStart + email + "/";
            moodpath = userpath + moodTable;
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
     }

    public MutableLiveData<ArrayList<MoodEvent>> getMoodEvents() {
        return moodEvents;
    }

    public MoodEvent createMoodEvent(String name, Calendar cal, String situation, String emotion, String reason){
        return new MoodEvent(name, Calendar.getInstance().getTimeInMillis(), MoodEvent.situationToInt(situation), cal, emotion, reason);
    }

    public MoodEvent createMoodEvent(long id, HashMap map){ 
         if(map == null){return null;}
         if(map.get("mood_date") == null){return null;}
         Calendar date = Calendar.getInstance();
         try {
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

    public void deleteMoodEvent(long id){
         deleteData(moodpath, id+"");
    }

    public void createAndWriteMood(String name, Calendar cal, String situation, String emotion, String reason){
         MoodEvent moodEvent = createMoodEvent(name, cal, situation, emotion, reason);
         HashMap<String, String> moodData = new HashMap<>();
         moodData.put("mood_name", moodEvent.getName());
         moodData.put("mood_date", MoodEvent.longFormat.format(moodEvent.getDate().getTime()));
         moodData.put("mood_situation", moodEvent.getSituation()+"");
         moodData.put("mood_reason_str", moodEvent.getReasonString());
         moodData.put("mood_emotion", moodEvent.getEmotion());

        setData(moodpath + moodEvent.getId(), moodData);
    }

    public void updateMood(String name, long id, String situation, Calendar cal, String emotion, String reason){
        MoodEvent moodEvent = new MoodEvent(name, id, MoodEvent.situationToInt(situation), cal, emotion, reason);
        HashMap<String, String> moodData = new HashMap<>();
        moodData.put("mood_name", moodEvent.getName());
        moodData.put("mood_date", MoodEvent.longFormat.format(moodEvent.getDate().getTime()));
        moodData.put("mood_situation", moodEvent.getSituation()+"");
        moodData.put("mood_reason_str", moodEvent.getReasonString());
        moodData.put("mood_emotion", moodEvent.getEmotion());

        setData(moodpath + moodEvent.getId(), moodData);
    }

    public void getMoodEvent(long id){
         getData(moodpath, id+"");
    }

    @Override
    protected void onSuccessfulAddition(){
        Log.d(TAG, "MoodEvent successfully created and written");
        success.setValue(new Boolean(true));
    }

    @Override
    protected void onFailedAddition(@NonNull Exception e){
        Log.d(TAG, "MoodEvent failed to create " + e.toString());
        success.setValue(new Boolean(false));
    }

    @Override
    protected void onSuccessfulDelete(){
        Log.d(TAG, "MoodEvent successfully deleted");
        success.setValue(new Boolean(true));
    }

    @Override
    protected void onFailedDelete(@NonNull Exception e){
        Log.d(TAG, "MoodEvent failed to delete " + e.toString());
        success.setValue(new Boolean(false));
    }

    @Override
    protected void onSuccessfulDataRetrieval(Map<String, Object> map){
        Log.d(TAG, "MoodEvent successfully retrieved" + map.toString());
        HashMap<String,String> hashMap = (HashMap) map;
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
