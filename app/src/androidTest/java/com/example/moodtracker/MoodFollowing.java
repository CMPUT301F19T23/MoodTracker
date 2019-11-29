package com.example.moodtracker;


import android.app.Activity;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MoodFollowing {


    private Solo solo;
    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);


    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }



    @Test
    public void TryFollowing(){

        //test add function
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email_field), "testcase@ua.ca");
        solo.enterText((EditText) solo.getView(R.id.password_field), "123456");
        solo.clickOnText("SIGN IN");
        solo.assertCurrentActivity("Wrong Activity", MoodActivity.class);
        solo.clickOnText("View Friends Mood");

        solo.assertCurrentActivity("Wrong Activity", FriendListActivity.class );

        solo.enterText((EditText) solo.getView(R.id.search_field), "friend1");
        solo.clickOnText("Search");
        solo.clickOnText("Ask permission to follow");


        solo.goBackToActivity("MoodActivity");
        solo.clickOnText("Log");



        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email_field), "friend@ua.ca");
        solo.enterText((EditText) solo.getView(R.id.password_field), "123456");
        solo.clickOnText("SIGN IN");


        solo.assertCurrentActivity("Wrong Activity", MoodActivity.class);
        solo.clickOnText("View Friends Mood");

        solo.assertCurrentActivity("Wrong Activity", FriendListActivity.class );

        solo.clickOnText("Follow Requests");
        solo.clickOnText("Accept");
        solo.goBackToActivity("FriendListActivity");
        assertTrue(solo.searchText("TestName"));

    }



    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
