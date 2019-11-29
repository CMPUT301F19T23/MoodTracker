package com.example.DB;

import android.app.Application;

public class LoginManager extends AuthCommunicator {
    public LoginManager(Application application) {
        super(application);
    }

    public void logUserIn(String email, String password){
        super.logUserIn(email, password);
    }
}
