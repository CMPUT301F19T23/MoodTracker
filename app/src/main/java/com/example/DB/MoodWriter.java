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

/**
 * Handles reading and writing MoodEvents and lists of moodEvents to database. Also provides multiple convenient ways to create MoodEvents.
 */
public class MoodWriter extends DBCommunicator {
    private String userpath;
    private String moodpath;
    private boolean initalized = false;

    final private MutableLiveData<ArrayList<MoodEvent>> moodEvents = new MutableLiveData<>(new ArrayList<MoodEvent>());

     public MoodWriter(Application application){
        super(application);
        // just to avoid null pointer exceptions MoodWriter shouldn't be used without initializing.
        setEmail("null");
    }

    /**
     * Set up the the relevant paths that the object has to reference for queries. Also sets up LiveData
     * pointing to this user's moods.
     * @param email
     *      email of this user
     */
    public void init(String email){
         if(!initalized){
             setEmail(email);
             linkHistory();
             initalized = true;
         }
    }

    /**
     * Set user's and email, modifies stored paths to reflect the database entry with those parameters.
     * Does not call linkHistory.
     * @param email
     *      email of this user
     */
    private void setEmail(String email){
         if(email != null){
            userpath = dbStart + email + "/";
            moodpath = userpath + moodTable;
         }
    }

    /**
     * Connects this object's LiveData object to a SnapshotListener of the user's MoodEvent list so we can see the updates to it in real time.
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
     }

    public MutableLiveData<ArrayList<MoodEvent>> getMoodEvents() {
        return moodEvents;
    }

    /**
     * Create a new MoodEvent with given parameters. Main difference from createMoodEvent(id, hashmap) is that this
     * is meant to represent new MoodEvents, as in those whose id is this instant as they haven't existed before.
     * @param name
     *      title of the event as given by user
     * @param cal
     *      newly instantiated calendar object
     * @param situation
     *      String equivalent of the various situation ints
     * @param emotion
     *      String representing emotion this MoodEvent represents
     * @param reason
     *      String explaining why this MoodEvent occurred
     * @param image
     *      String representing URL of this MoodEvent's attached image
     * @return
     *      the created MoodEvent
     */
    public MoodEvent createMoodEvent(String name, Calendar cal, String situation, String emotion, String reason, String image){
        return new MoodEvent(name, Calendar.getInstance().getTimeInMillis(), MoodEvent.situationToInt(situation), cal, emotion, reason, image);
    }

    /**
     * Create a MoodEvent with a predetermined id, and with all of its data in a hashmap
     * Meant to be used to construct MoodEvents that already exist from the database. If
     * hashmap is invalid(eg, missing expected fields), will return null.
     * @param id
     *      id representing the MoodEvent (time in milliseconds)
     * @param map
     *      hashmap containing all the MoodEvent's data
     * @return
     *      the created MoodEvent, or null if there was a problem
     */
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

         if(map.get("mood_name") == null){return null;}
         String name = (String) map.get("mood_name");

         if(map.get("mood_reason_str") == null){return null;}
         String reason = (String) map.get("mood_reason_str");

         if(map.get("mood_situation") == null){return null;}
         int situation = Integer.parseInt((String) map.get("mood_situation"));

         if(map.get("mood_emotion") == null){return null;}
         String emotion = (String) map.get("mood_emotion");

         if(map.get("mood_image") == null){return null;}
         String image = (String) map.get("mood_image");
            
         return new MoodEvent(name, id, situation, date, emotion, reason, image);
    }





    /**
     * Removes a specific MoodEvent from this user's MoodEvent list
     * @param id
     *      id of the MoodEvent to be deleted
     */
    public void deleteMoodEvent(long id){
         deleteData(moodpath, id+"");
    }

    /**
     * Calls createMoodEvent(...) to create a MoodEvent, then runs a setData query to add it to the database
     * @param name
     *      title of the event as given by user
     * @param cal
     *      newly instantiated calendar object
     * @param situation
     *      String equivalent of the various situation ints
     * @param emotion
     *      String representing emotion this MoodEvent represents
     * @param reason
     *      String explaining why this MoodEvent occurred
     * @param image
     *      String representing URL of this MoodEvent's attached image
     */
    public void createAndWriteMood(String name, Calendar cal, String situation, String emotion, String reason, String image){
         MoodEvent moodEvent = createMoodEvent(name, cal, situation, emotion, reason, image);
         HashMap<String, String> moodData = new HashMap<>();
         moodData.put("mood_name", moodEvent.getName());
         moodData.put("mood_date", MoodEvent.longFormat.format(moodEvent.getDate().getTime()));
         moodData.put("mood_situation", moodEvent.getSituation()+"");
         moodData.put("mood_reason_str", moodEvent.getReasonString());
         moodData.put("mood_emotion", moodEvent.getEmotion());
         moodData.put("mood_image", moodEvent.getImage());

        setData(moodpath + moodEvent.getId(), moodData);
    }

    /**
     * Calls createMoodEvent(...) to create a MoodEvent, then runs a setData query to update it in the database.
     * Intended to get information from an already existing MoodEvent.
     * @param name
     *      title of the event as given by user
     * @param id
     *      id of the MoodEvent
     * @param cal
     *      newly instantiated calendar object
     * @param situation
     *      String equivalent of the various situation ints
     * @param emotion
     *      String representing emotion this MoodEvent represents
     * @param reason
     *      String explaining why this MoodEvent occurred
     * @param image
     *      String representing URL of this MoodEvent's attached image
     * @return
     *      the created MoodEvent
     */
    public void updateMood(String name, long id, String situation, Calendar cal, String emotion, String reason, String image){
        MoodEvent moodEvent = new MoodEvent(name, id, MoodEvent.situationToInt(situation), cal, emotion, reason, image);
        HashMap<String, String> moodData = new HashMap<>();
        moodData.put("mood_name", moodEvent.getName());
        moodData.put("mood_date", MoodEvent.longFormat.format(moodEvent.getDate().getTime()));
        moodData.put("mood_situation", moodEvent.getSituation()+"");
        moodData.put("mood_reason_str", moodEvent.getReasonString());
        moodData.put("mood_emotion", moodEvent.getEmotion());
        moodData.put("mood_image", moodEvent.getImage());

        setData(moodpath + moodEvent.getId(), moodData);
    }

    /**
     * Runs a getData query on this user's Mood document + the id of the mood
     * @param id
     *      id of the desired MoodEvent
     */
    public void getMoodEvent(long id){
         getData(moodpath, id+"");
    }

    /**
     * Called when data is set without encountering errors. Sets success to true.
     */
    @Override
    protected void onSuccessfulAddition(){
        Log.d(TAG, "MoodEvent successfully created and written");
        success.setValue(new Boolean(true));
    }

    /**
     * Called when data is set and encounters errors. Sets success to false.
     */
    @Override
    protected void onFailedAddition(@NonNull Exception e){
        Log.d(TAG, "MoodEvent failed to create " + e.toString());
        success.setValue(new Boolean(false));
    }

    /**
     * Called when data is deleted without encountering errors. Sets success to true.
     */
    @Override
    protected void onSuccessfulDelete(){
        Log.d(TAG, "MoodEvent successfully deleted");
        success.setValue(new Boolean(true));
    }

    /**
     * Called when data is set and encounters errors. Sets success to false.
     */
    @Override
    protected void onFailedDelete(@NonNull Exception e){
        Log.d(TAG, "MoodEvent failed to delete " + e.toString());
        success.setValue(new Boolean(false));
    }

    /**
     * Called when data is retrieved without encountering errors. Puts hashmap of data into returnValue. Sets success to true.
     * @param map
     *      hashmap containing all the data of the MoodEvent
     */
    @Override
    protected void onSuccessfulDataRetrieval(Map<String, Object> map){
        Log.d(TAG, "MoodEvent successfully retrieved" + map.toString());
        HashMap<String,String> hashMap = (HashMap) map;
        returnVal.setValue(hashMap);
        success.setValue(new Boolean(true));
    }

    /**
     * Called when data is retrieved but encounters errors. Sets success to false.
     */
    @Override
    protected void onFailedDataRetrieval(@NonNull Exception e){
        Log.d(TAG, "MoodEvent retrieval failed: " + e.toString());
        returnVal.setValue(null);
        success.setValue(new Boolean(false));
    }
}
