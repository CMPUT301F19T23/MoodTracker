package com.example.DB;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.ContentValues.TAG;

/**
 * The AuthCommunicator class holds several methods designed to deal with the logging in and Users of the database.
 *
 * Usage: the class extending AuthCommunicator needs to wrap one or more of the query methods and call it (probably publicly)
 * These methods create an Asynchronous object and execute it. In these objects, the actual Firebase queries are run.
 * At certain points inside the async objects, usually on success or failure of the query, mid-query methods are called.
 * These can and often should be overriden to more accurately log relevant information, and modify data as required by your
 * program. Asynchronous objects run in the background so as not to slow down the UI thread. Because of this, the mid-query
 * methods update a LiveData object representing the success of the operation.
 */
public abstract class AuthCommunicator extends AndroidViewModel {
    protected FirebaseAuth auth;

    protected MutableLiveData<Boolean> success;

    public AuthCommunicator(Application application) {
        super(application);
        auth = FirebaseAuth.getInstance();
        success = new MutableLiveData<>(false);
    }



    // Query Methods:
    /**
     * Create a User's account
     * @param email
     *      the email address of the new user
     * @param password
     *      the password of the new user
     */
    protected void createAccount(String email, String password){
        new CreateAccountAsync(auth,this).execute(email, password);
    }

    /**
     * Log a user in
     * @param email
     *      the email address of the user
     * @param password
     *      the password of the user
     */
    protected void logUserIn(String email, String password){
        new LoginAsync(auth, this).execute(email, password);
    }





    /**
     * @return
     *      changing object representing the successes and failures of this object's queries. Meant for UI classes to be able to know
     *      that operations have finished.
     */
    public MutableLiveData<Boolean> getSuccess() {
        return success;
    }




    // Mid-Query methods:
    /**
     * called when an operation that creates a user account terminates without encountering an error
     * @param email
     *      the email address
     */
    protected void onSuccessfulAccountCreation(String email){
        Log.d(TAG, "Successfully created user " + email);
        success.setValue(new Boolean(true));
    }

    /**
     * called when an operation that creates a user account encounters an error
     */
    protected void onFailedAccountCreation(@NonNull Exception e){
        Log.d(TAG, "Failed to create user " + e.toString());
        success.setValue(new Boolean(false));
    }

    /**
     * called when an operation logs a user in without encountering an error
     */
    protected  void onSuccessfulLogin(){
        Log.d(TAG, "Successfully Logged user in");
        success.setValue(new Boolean(true));
    }

    /**
     * called when an operation that logs a user in encounters an error
     */
    protected  void onFailedLogin(@NonNull Exception e){
        Log.d(TAG, "Failed to log user in " + e.toString());
        success.setValue(new Boolean(false));
    }




    // Asynchronous Objects:
    /**
     * The object created by the createAccount method
     */
    private static class CreateAccountAsync extends AsyncTask<String, Void, Void> {
        private FirebaseAuth auth;
        AuthCommunicator ac;

        private CreateAccountAsync(FirebaseAuth auth, AuthCommunicator ac){
            this.auth = auth;
            this.ac = ac;
        }

        /**
         * Method that is called in this class's .execute(). Runs the Firebase query to create an account.
         */
        @Override
        protected Void doInBackground(final String... strings) {
            // strings[0] = email
            // strings[1] = password
            auth.createUserWithEmailAndPassword(strings[0], strings[1]).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        ac.onSuccessfulAccountCreation(strings[0]);
                    }
                    else{
                        ac.onFailedAccountCreation(task.getException());
                    }
                }

            });
            return null;
        }
    }

    /**
     * The object created by the logUserIn method
     */
    private static class LoginAsync extends AsyncTask<String, Void, Void> {
        private FirebaseAuth auth;
        AuthCommunicator ac;

        private LoginAsync(FirebaseAuth auth, AuthCommunicator ac){
            this.auth = auth;
            this.ac = ac;
        }

        /**
         * Method that is called in this class's .execute(). Runs the Firebase query to log in an account.
         */
        @Override
        protected Void doInBackground(final String... strings) {
            // strings[0] = email
            // strings[1] = password
            auth.signInWithEmailAndPassword(strings[0], strings[1])
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                ac.onSuccessfulLogin();
                            } else {
                                ac.onFailedLogin(task.getException());
                            }
                        }
                    });
            return null;
        }
    }



}
