package com.example.moodtracker;

import java.util.ArrayList;

/**this shows a list of participants of an user
 * with unique usernames and emails
 */
public class Participant extends User {
    private ArrayList<Participant> friends; //list of followers

    public Participant(String username, String email) {
        super(username, email);
    }

    public void addFriend(){} //add followers

    public void removeFriend(){} //stop following


}
