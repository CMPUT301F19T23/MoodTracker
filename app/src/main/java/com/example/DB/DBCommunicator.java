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


    protected void setData(String docName, HashMap data){
        //System.out.println("calling set data");
        new SetDataAsync(db, docName, this).execute(data);
    }

    protected void addIfUnique(String docName, String field, String searchTerm, String id, HashMap data){
        HashMap fieldMap = new HashMap();
        fieldMap.put("field", field);

        HashMap searchTermMap = new HashMap();
        searchTermMap.put("searchTerm", searchTerm);

        HashMap idMap = new HashMap();
        idMap.put("id", id);

        new UniqueAddDataAsync(db, docName, this).execute(fieldMap, searchTermMap, idMap, data);
    }

    protected void getData(String docName, String element){
        new GetDataAsync(db, docName, this).execute(element);
    }

    protected void deleteData(String docName, String element){
        new DeleteDataAsync(db, docName, this).execute(element);
    }

    protected void searchFor(String docName, String field, String searchTerm){
        new SearchDataAsync(db, docName, this).execute(field, searchTerm);
    }


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

    protected void onFailedSearch(@NonNull Exception e){
        Log.d(TAG, "Error getting documents: " + e.toString());
        multipleReturnVals.setValue(new ArrayList<HashMap<String, String>>());
        success.setValue(new Boolean(false));
    }

    protected boolean onSuccessfulSearchForAdd(QuerySnapshot qs){
        if(qs.size() != 0){
            Log.d(TAG, "Search result already exists");
            success.setValue(new Boolean(false));
            return false;
        }

        Log.d(TAG, "Search result does not yet exist");
        return true;
    }

    protected void onFailedSearchForAdd(@NonNull Exception e){
        Log.d(TAG, "Error getting documents: " + e.toString());
        success.setValue(new Boolean(false));
    }

    protected void onSuccessfulDataRetrieval(Map<String, Object> map){
        Log.d(TAG, "Data successfully retrieved");
        HashMap<String,String> hashMap = (HashMap) map;
        returnVal.setValue(hashMap);
        success.setValue(new Boolean(true));
    }

    protected void onEmptyDataRetrieval(){
        Log.d(TAG, "No such document");
        returnVal.setValue(null);
        success.setValue(new Boolean(false));
    }

    protected void onFailedDataRetrieval(@NonNull Exception e){
        Log.d(TAG, "Retrieval failed: " + e.toString());
        returnVal.setValue(null);
        success.setValue(new Boolean(false));
    }

    protected void onSuccessfulAddition(){
        Log.d(TAG, "Data addition successful");
        success.setValue(new Boolean(true));
    }

    protected void onFailedAddition(@NonNull Exception e){
        Log.d(TAG, "Data addition failed " + e.toString());
        success.setValue(new Boolean(false));
    }

    protected void onSuccessfulDelete(){
        Log.d(TAG, "Data deletion successful");
        success.setValue(new Boolean(true));
    }
    protected void onFailedDelete(@NonNull Exception e){
        Log.d(TAG, "Data deletion failed " + e.toString());
        success.setValue(new Boolean(false));
    }

    public LiveData<Boolean> getSuccess(){
        return success;
    }

    public LiveData<HashMap<String, String>> getReturnVal() {
        return returnVal;
    }

    public LiveData<ArrayList<HashMap<String, String>>> getMultipleReturnVals() {
        return multipleReturnVals;
    }

    private static class SetDataAsync extends AsyncTask<HashMap, Void, Void>{
        private FirebaseFirestore db;
        String path;
        DBCommunicator dbc;

        private SetDataAsync(FirebaseFirestore db, String path, DBCommunicator dbc){
            this.db = db;
            this.path = path;
            this.dbc = dbc;
            //System.out.println("Successful Async construction.");
        }

        @Override
        protected Void doInBackground(HashMap... hashMaps) {
            //System.out.println("About to send to database");
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

    private static class GetDataAsync extends AsyncTask<String, Void, Void>{
        private FirebaseFirestore db;
        String path;
        DBCommunicator dbc;

        private GetDataAsync(FirebaseFirestore db, String path, DBCommunicator dbc){
            this.db = db;
            this.path = path;
            this.dbc = dbc;
            //System.out.println("Successful Async construction.");
        }

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

    private static class DeleteDataAsync extends AsyncTask<String, Void, Void>{
        private FirebaseFirestore db;
        String path;
        DBCommunicator dbc;

        private DeleteDataAsync(FirebaseFirestore db, String path, DBCommunicator dbc){
            this.db = db;
            this.path = path;
            this.dbc = dbc;
            //System.out.println("Successful Async construction.");
        }

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

    private static class SearchDataAsync extends AsyncTask<String, Void, Void>{
        private FirebaseFirestore db;
        String path;
        DBCommunicator dbc;

        private SearchDataAsync(FirebaseFirestore db, String path, DBCommunicator dbc){
            this.db = db;
            this.path = path;
            this.dbc = dbc;
            //System.out.println("Successful Async construction.");
        }

        @Override
        protected Void doInBackground(final String... strings) {
            db.collection(path)
                    .whereEqualTo(strings[0], strings[1])
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
//                                System.out.println(path);
//                                System.out.println(strings[0] + ", " + strings[1] + "\n");
                                dbc.onSuccessfulSearch(task.getResult());
                            } else {
                                dbc.onFailedSearch(task.getException());
                            }
                        }
                    });
            return null;
        }
    }

    private static class UniqueAddDataAsync extends AsyncTask<HashMap, Void, Void>{
        private FirebaseFirestore db;
        String path;
        DBCommunicator dbc;

        private UniqueAddDataAsync(FirebaseFirestore db, String path, DBCommunicator dbc){
            this.db = db;
            this.path = path;
            this.dbc = dbc;
            //System.out.println("Successful Async construction.");
        }

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
