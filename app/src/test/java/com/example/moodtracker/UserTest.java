package com.example.moodtracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {


    private User mockUser() {
        User user = new User("testName", "test@UA.ca");
        return user;
    }


    @Test
    void testGetUsername() {
        User user = mockUser();

        assertEquals("testName", user.getUsername());

    }

    @Test
    void testSetUsername() {
        User user = mockUser();
        user.setUsername("anotherName");
        assertEquals("anotherName", user.getUsername());
    }

    @Test
    void testGetEmail() {
        User user = mockUser();

        assertEquals("test@UA.ca", user.getEmail());
    }

    @Test
    void testSetEmail() {
        User user = mockUser();
        user.setEmail("new@UA.ca");
        assertEquals("new@UA.ca", user.getEmail());
    }
}
