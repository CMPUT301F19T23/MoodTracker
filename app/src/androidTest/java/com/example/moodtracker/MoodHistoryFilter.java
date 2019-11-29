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
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class MoodHistoryFilter {


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
    public void TryFilter(){

        //test add function
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email_field), "testcase@ua.ca");
        solo.enterText((EditText) solo.getView(R.id.password_field), "123456");
        solo.clickOnText("SIGN IN");
        solo.assertCurrentActivity("Wrong Activity", MoodActivity.class);
        solo.clickOnText("View my Mood History");
        solo.assertCurrentActivity("Wrong Activity", MoodHistoryActivity.class);
        solo.clickOnText("Add New Event");
        solo.assertCurrentActivity("Wrong Activity", AddActivity.class);
        solo.enterText((EditText) solo.getView(R.id.name_field), "Event1");
        solo.clickOnText("select a mood");
        solo.clickOnText("angry");
        solo.clickOnText("Confirm Adding");
        assertTrue(solo.searchText("Event1"));


        //add second moodevent
        solo.clickOnText("Add New Event");
        solo.assertCurrentActivity("Wrong Activity", AddActivity.class);
        solo.enterText((EditText) solo.getView(R.id.name_field), "Event2");
        solo.clickOnText("select a mood");
        solo.clickOnText("sad");
        solo.clickOnText("Confirm Adding");
        assertTrue(solo.searchText("Event1"));
        assertTrue(solo.searchText("Event2"));


        //filter for sad mood
        solo.enterText((EditText) solo.getView(R.id.search_field), "angry");
        assertTrue(solo.searchText("Event1"));
        assertFalse(solo.searchText("Event2"));

        // test delete button
        solo.clearEditText((EditText) solo.getView(R.id.search_field));
        solo.clickOnText("Event1");
        solo.clickOnText("Delete");
        solo.clickOnText("Event2");
        solo.clickOnText("Delete");



    }


    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
