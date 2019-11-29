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

public abstract class AuthCommunicator extends AndroidViewModel {
    protected FirebaseAuth auth;

    protected MutableLiveData<Boolean> success;

    public AuthCommunicator(Application application) {
        super(application);
        auth = FirebaseAuth.getInstance();
        success = new MutableLiveData<>(false);
    }

    protected void createAccount(String email, String password){
        new CreateAccountAsync(auth,this).execute(email, password);
    }

    protected void logUserIn(String email, String password){
        new LoginAsync(auth, this).execute(email, password);
    }

    public MutableLiveData<Boolean> getSuccess() {
        return success;
    }





    protected void onSuccessfulAccountCreation(String email){
        Log.d(TAG, "Successfully created user " + email);
        success.setValue(new Boolean(true));
    }

    protected void onFailedAccountCreation(@NonNull Exception e){
        Log.d(TAG, "Failed to create user " + e.toString());
        success.setValue(new Boolean(false));
    }

    protected  void onSuccessfulLogin(){
        Log.d(TAG, "Successfully Logged user in");
        success.setValue(new Boolean(true));
    }

    protected  void onFailedLogin(@NonNull Exception e){
        Log.d(TAG, "Failed to log user in " + e.toString());
        success.setValue(new Boolean(false));
    }





    private static class CreateAccountAsync extends AsyncTask<String, Void, Void> {
        private FirebaseAuth auth;
        AuthCommunicator ac;

        private CreateAccountAsync(FirebaseAuth auth, AuthCommunicator ac){
            this.auth = auth;
            this.ac = ac;
            //System.out.println("Successful Async construction.");
        }

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

    private static class LoginAsync extends AsyncTask<String, Void, Void> {
        private FirebaseAuth auth;
        AuthCommunicator ac;

        private LoginAsync(FirebaseAuth auth, AuthCommunicator ac){
            this.auth = auth;
            this.ac = ac;
            //System.out.println("Successful Async construction.");
        }

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
                                // If sign in fails, display a message to the user.
                                ac.onFailedLogin(task.getException());

                            }
                        }
                    });
            return null;
        }
    }



}
