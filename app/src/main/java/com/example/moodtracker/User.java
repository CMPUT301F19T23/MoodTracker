package com.example.moodtracker;

/**
 * This gets and sets the username and emails
 * of an user
 */

public class User {
    private String username;
    private String email;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    } //return the username and email

    public String getUsername() {
        return username;
    } //get username

    public void setUsername(String username) {
        this.username = username;
    } //set username

    public String getEmail() {
        return email;
    } //get user email

    public void setEmail(String email) {
        this.email = email;
    } //set user email
}
