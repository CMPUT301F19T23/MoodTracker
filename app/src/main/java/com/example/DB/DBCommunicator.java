package com.example.DB;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public abstract class DBCommunicator extends AndroidViewModel {
    protected FirebaseFirestore db;

    protected MutableLiveData<Boolean> success;

    protected MutableLiveData<HashMap<String,String>> returnVal;

    public DBCommunicator(Application application){
        super(application);
        db = FirebaseFirestore.getInstance();
        success = new MutableLiveData<>(false);
        returnVal = new MutableLiveData<>(new HashMap<String, String>());
    }


    protected void setData(String docName, HashMap data){
        //System.out.println("calling set data");
        new SetDataAsync(db, docName, this).execute(data);
    }

    protected void getData(String docName, String element){
        new GetDataAsync(db, docName, this).execute(element);
    }

    protected void deleteData(String docName, String element){
        new DeleteDataAsync(db, docName, this).execute(element);
    }

    protected void onSuccessfulDataRetrieval(Map<String, Object> map){
        Log.d(TAG, "Data successfully retrieved");
        HashMap<String,String> hashMap = (HashMap) map;
        returnVal.setValue(hashMap);
        success.setValue(new Boolean(true));
    }

    protected void onEmptyDataRetrieval(){
        Log.d(TAG, "No such document");        ;
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

    private static class SetDataAsync extends AsyncTask<HashMap, Void, Void>{
        private FirebaseFirestore db;
        String path;
        DBCommunicator dbc;

        private SetDataAsync(FirebaseFirestore db, String path, DBCommunicator dbc){
            this.db = db;
            this.path = path;
            this.dbc = dbc;
            System.out.println("Successful Async construction.");
        }

        @Override
        protected Void doInBackground(HashMap... hashMaps) {
            System.out.println("About to send to database");
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

}
