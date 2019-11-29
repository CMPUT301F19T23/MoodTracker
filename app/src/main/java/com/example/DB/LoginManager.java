  
package com.example.DB;

import android.app.Application;

/**
 * Handles logging a user in. Pretty Bare-bones for now, but might be more useful later on if authentication
 * needs updating.
 */
public class LoginManager extends AuthCommunicator {
    public LoginManager(Application application) {
        super(application);
    }

    /**
     * Starts a login query with no modifications
     * @param email
     *      the email address of the user
     * @param password
     *      the password of the user
     */
    public void logUserIn(String email, String password){
        super.logUserIn(email, password);
    }
}
