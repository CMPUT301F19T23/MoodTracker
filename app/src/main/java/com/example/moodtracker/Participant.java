package com.example.moodtracker;

import java.util.ArrayList;

public class Participant extends User {
    private ArrayList<Participant> friends;

    public Participant(String username, String email) {
        super(username, email);
    }

    public void addFriend(){}

    public void removeFriend(){}


}
