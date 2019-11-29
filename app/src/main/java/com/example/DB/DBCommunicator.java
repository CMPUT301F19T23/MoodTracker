package com.example.DB;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * The DBCommunicator class holds several methods designed to insert data into and retrieve data from the database.
 * It is highly modular and overridable and supports several types of queries.
 *
 * Usage: the class extending DBCommunicator needs to wrap one or more of the query methods and call it (probably publicly)
 * These methods create an Asynchronous object and execute it. In these objects, the actual Firebase queries are run.
 * At certain points inside the async objects, usually on success or failure of the query, mid-query methods are called.
 * These can and often should be overriden to more accurately log relevant information, and modify data as required by your
 * program. Asynchronous objects run in the background so as not to slow down the UI thread. Because of this, the mid-query
 * methods update a LiveData object representing the success of the query, and others representing the return values(HashMaps) of these queries.
 */
public abstract class DBCommunicator extends AndroidViewModel {
    protected FirebaseFirestore db;

    protected MutableLiveData<Boolean> success;

    protected MutableLiveData<HashMap<String,String>> returnVal;

    protected MutableLiveData<ArrayList<HashMap<String,String>>> multipleReturnVals;

    public DBCommunicator(Application application){
        super(application);
        db = FirebaseFirestore.getInstance();
        success = new MutableLiveData<>(false);
        returnVal = new MutableLiveData<>(new HashMap<String, String>());
        multipleReturnVals = new MutableLiveData<>(new ArrayList<HashMap<String, String>>());
    }


    // Query Methods:
    /**
     * Write some data into a document. Will overwrite any data present.
     * @param docName
     *      the document location to write into
     * @param data
     *      key, value pairs to be written into document
     */
    protected void setData(String docName, HashMap data){
        new SetDataAsync(db, docName, this).execute(data);
    }

    /**
     * Check to see if 1 or more of a document with a certain key, value pair exists.
     * If not, then write data into that document or a child of the document.
     * @param docName
     *      the document location to write into
     * @param field
     *      the key to search
     * @param searchTerm
     *      the value to look for
     * @param id
     *      the location to write into from the docName. (Location is docName + id)
     * @param data
     *      the data to write
     */
    protected void addIfUnique(String docName, String field, String searchTerm, String id, HashMap data){
        HashMap fieldMap = new HashMap();
        fieldMap.put("field", field);

        HashMap searchTermMap = new HashMap();
        searchTermMap.put("searchTerm", searchTerm);

        HashMap idMap = new HashMap();
        idMap.put("id", id);

        new UniqueAddDataAsync(db, docName, this).execute(fieldMap, searchTermMap, idMap, data);
    }

    /**
     * Run a query to return all key, value pairs from the specified location
     * @param docName
     *      the document location to write into
     * @param element
     *      the offset from the document (Location is docName + element)
     */
    protected void getData(String docName, String element){
        new GetDataAsync(db, docName, this).execute(element);
    }

    /**
     * Deletes all key, value pairs at the specified location
     * @param docName
     *      the document location to write into
     * @param element
     *      the offset from the document (Location is docName + element)
     */
    protected void deleteData(String docName, String element){
        new DeleteDataAsync(db, docName, this).execute(element);
    }

    /**
     * Check to see if a key value pair exists at a specified location
     * @param docName
     *      the document location to write into
     * @param field
     *      the key to search
     * @param searchTerm
     *      the value to look for
     */
    protected void searchFor(String docName, String field, String searchTerm){
        new SearchDataAsync(db, docName, this).execute(field, searchTerm);
    }




    // Mid-Query execution methods:
    /**
     * called when a search query does not encounter errors
     * @param qs
     *      QuerySnapshot of found search item so we can look at data we searched for
     */
    protected void onSuccessfulSearch(QuerySnapshot qs){
        ArrayList<HashMap<String,String>> mapList = new ArrayList<>();
        for (QueryDocumentSnapshot document : qs) {
            Log.d(TAG, "Found element with id: " + document.getId());
            Map current = document.getData();
            current.put("id", document.getId());
            mapList.add((HashMap)current);
        }
        multipleReturnVals.setValue(mapList);
        success.setValue(new Boolean(true));
    }

    /**
     * called when a search query encounters errors
     */
    protected void onFailedSearch(@NonNull Exception e){
        Log.d(TAG, "Error getting documents: " + e.toString());
        multipleReturnVals.setValue(new ArrayList<HashMap<String, String>>());
        success.setValue(new Boolean(false));
    }

    /**
     * called when a search query determining uniqueness does not encounter errors
     * @param qs
     *      QuerySnapshot of found search item so we can look at data we searched for
     */
    protected boolean onSuccessfulSearchForAdd(QuerySnapshot qs){
        if(qs.size() != 0){
            Log.d(TAG, "Search result already exists");
            success.setValue(new Boolean(false));
            return false;
        }

        Log.d(TAG, "Search result does not yet exist");
        return true;
    }

    /**
     * called when a search query determining uniqueness encounters errors
     */
    protected void onFailedSearchForAdd(@NonNull Exception e){
        Log.d(TAG, "Error getting documents: " + e.toString());
        success.setValue(new Boolean(false));
    }

    /**
     * called when a query fetching data does not encounter errors and finds data
     * @param map
     *      object with all the document's data
     */
    protected void onSuccessfulDataRetrieval(Map<String, Object> map){
        Log.d(TAG, "Data successfully retrieved");
        HashMap<String,String> hashMap = (HashMap) map;
        returnVal.setValue(hashMap);
        success.setValue(new Boolean(true));
    }

    /**
     * called when a query fetching data does not encounter errors but finds no data
     */
    protected void onEmptyDataRetrieval(){
        Log.d(TAG, "No such document");
        returnVal.setValue(null);
        success.setValue(new Boolean(false));
    }

    /**
     * called when a query fetching data encounters errors
     */
    protected void onFailedDataRetrieval(@NonNull Exception e){
        Log.d(TAG, "Retrieval failed: " + e.toString());
        returnVal.setValue(null);
        success.setValue(new Boolean(false));
    }

    /**
     * called when a query setting data does not encounter errors
     */
    protected void onSuccessfulAddition(){
        Log.d(TAG, "Data addition successful");
        success.setValue(new Boolean(true));
    }

    /**
     * called when a query setting data encounters errors
     */
    protected void onFailedAddition(@NonNull Exception e){
        Log.d(TAG, "Data addition failed " + e.toString());
        success.setValue(new Boolean(false));
    }

    /**
     * called when a query deleting data does not encounter errors
     */
    protected void onSuccessfulDelete(){
        Log.d(TAG, "Data deletion successful");
        success.setValue(new Boolean(true));
    }

    /**
     * called when a query setting data encounters errors
     */
    protected void onFailedDelete(@NonNull Exception e){
        Log.d(TAG, "Data deletion failed " + e.toString());
        success.setValue(new Boolean(false));
    }





    /**
     * @return
     *      changing object representing the successes and failures of this object's queries. Meant for UI classes to be able to know
     *      that operations have finished.
     */
    public LiveData<Boolean> getSuccess(){
        return success;
    }

    /**
     * @return
     *      changing object that holds the result of this object's queries that return single objects,
     *      such as the default getData operation. Meant for UI classes to be able to get data when operations complete.
     */
    public LiveData<HashMap<String, String>> getReturnVal() {
        return returnVal;
    }

    /**
     * @return
     *      changing object that holds the result of this object's queries that return multiple objects, such as the default getData operation
     *      Meant for UI classes to be able to get data when operations complete.
     */
    public LiveData<ArrayList<HashMap<String, String>>> getMultipleReturnVals() {
        return multipleReturnVals;
    }




    // Asynchronous objects:
    /**
     * The object created by the setData method
     */
    private static class SetDataAsync extends AsyncTask<HashMap, Void, Void>{
        private FirebaseFirestore db;
        String path;
        DBCommunicator dbc;

        private SetDataAsync(FirebaseFirestore db, String path, DBCommunicator dbc){
            this.db = db;
            this.path = path;
            this.dbc = dbc;
        }

        /**
         * Method that is called in this class's .execute(). Runs the Firebase query for setting some data
         */
        @Override
        protected Void doInBackground(HashMap... hashMaps) {
            db.document(path)
                    .set(hashMaps[0])
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dbc.onSuccessfulAddition();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dbc.onFailedAddition(e);
                        }
                    });
            return null;
        }
    }

    /**
     * The object created by the getData method
     */
    private static class GetDataAsync extends AsyncTask<String, Void, Void>{
        private FirebaseFirestore db;
        String path;
        DBCommunicator dbc;

        private GetDataAsync(FirebaseFirestore db, String path, DBCommunicator dbc){
            this.db = db;
            this.path = path;
            this.dbc = dbc;
        }

        /**
         * Method that is called in this class's .execute(). Runs the Firebase query to get data.
         */
        @Override
        protected Void doInBackground(String... strings) {
            db.document(path + strings[0]).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                dbc.onSuccessfulDataRetrieval(documentSnapshot.getData());
                            }else{
                                dbc.onEmptyDataRetrieval();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dbc.onFailedDataRetrieval(e);
                        }
                    });
            return null;
        }
    }

    /**
     * The object created by the deleteData method
     */
    private static class DeleteDataAsync extends AsyncTask<String, Void, Void>{
        private FirebaseFirestore db;
        String path;
        DBCommunicator dbc;

        private DeleteDataAsync(FirebaseFirestore db, String path, DBCommunicator dbc){
            this.db = db;
            this.path = path;
            this.dbc = dbc;
        }

        /**
         * Method that is called in this class's .execute(). Runs the Firebase query to delete data.
         */
        @Override
        protected Void doInBackground(String... strings) {
            db.document(path + strings[0])
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dbc.onSuccessfulDelete();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dbc.onFailedDelete(e);
                        }
                    });
            return null;
        }
    }

    /**
     * The object created by the searchData method
     */
    private static class SearchDataAsync extends AsyncTask<String, Void, Void>{
        private FirebaseFirestore db;
        String path;
        DBCommunicator dbc;

        private SearchDataAsync(FirebaseFirestore db, String path, DBCommunicator dbc){
            this.db = db;
            this.path = path;
            this.dbc = dbc;
        }

        /**
         * Method that is called in this class's .execute(). Runs the Firebase query to get data,
         * but with a whereEqual clause making sure that a certain key, value pair is found.
         */
        @Override
        protected Void doInBackground(final String... strings) {
            db.collection(path)
                    .whereEqualTo(strings[0], strings[1])
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                dbc.onSuccessfulSearch(task.getResult());
                            } else {
                                dbc.onFailedSearch(task.getException());
                            }
                        }
                    });
            return null;
        }
    }

    /**
     * The object created by the addIfUnique method
     */
    private static class UniqueAddDataAsync extends AsyncTask<HashMap, Void, Void>{
        private FirebaseFirestore db;
        String path;
        DBCommunicator dbc;

        private UniqueAddDataAsync(FirebaseFirestore db, String path, DBCommunicator dbc){
            this.db = db;
            this.path = path;
            this.dbc = dbc;
        }

        /**
         * Method that is called in this class's .execute(). Runs the Firebase query to get data,
         * but with a whereEqual clause making sure that a certain key, value pair is found. If the
         * pair is not found, then the data is written into database.
         */
        @Override
        protected Void doInBackground(final HashMap... hashMaps) {
            db.collection(path)
                    .whereEqualTo((String)hashMaps[0].get("field"), hashMaps[1].get("searchTerm"))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(dbc.onSuccessfulSearchForAdd(task.getResult())){
                                db.document(path + hashMaps[2].get("id"))
                                        .set(hashMaps[3])
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dbc.onSuccessfulAddition();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dbc.onFailedAddition(e);
                                            }
                                        });
                                }
                            } else {
                                dbc.onFailedSearchForAdd(task.getException());
                            }
                        }
                    });
            return null;
        }
    }

}
