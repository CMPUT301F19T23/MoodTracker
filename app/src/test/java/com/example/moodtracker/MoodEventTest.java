package com.example.moodtracker;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static com.example.moodtracker.MoodEvent.dayFormat;
import static com.example.moodtracker.MoodEvent.intToSituation;
import static com.example.moodtracker.MoodEvent.situationToInt;
import static com.example.moodtracker.MoodEvent.timeFormat;
import static org.junit.jupiter.api.Assertions.*;

public class MoodEventTest {


    private MoodEvent mockMoodEvent(){
        MoodEvent moodEvent = new MoodEvent("testName", 123, 0, Calendar.getInstance(), "angry", "No reason");
        return moodEvent;
    }


    @Test
    void testSetDate(){

        MoodEvent moodEvent = mockMoodEvent();


        moodEvent.setDate(2015, 01, 19, 06, 32);
        Calendar cal = Calendar.getInstance();
        cal.set(2015, 01, 19, 06,32);
        assertEquals(0, moodEvent.getDate().compareTo(cal));
    }


    @Test
    void testGetDate() {
        MoodEvent moodEvent = mockMoodEvent();



        Calendar cal = Calendar.getInstance();
        assertEquals(0, moodEvent.getDate().compareTo(cal));

    }

    @Test
    void testGetTime() {
        MoodEvent moodEvent = mockMoodEvent();

        Calendar cal = Calendar.getInstance();
        assertEquals(0, (moodEvent.getTime()).compareTo(timeFormat.format(cal.getTime())));
    }


    @Test
    void testGetDay() {
        MoodEvent moodEvent = mockMoodEvent();

        Calendar cal = Calendar.getInstance();
        assertEquals(0,(moodEvent.getDay()).compareTo(dayFormat.format(cal.getTime())));
    }


    @Test
    void testSetEmotion() {
        MoodEvent moodEvent = mockMoodEvent();

        moodEvent.setEmotion("angry");
        assertEquals(0, moodEvent.getEmotion().compareTo("angry"));
    }


    @Test
    void testGetEmotion() {
        MoodEvent moodEvent = mockMoodEvent();

        moodEvent.setEmotion("angry");
        assertEquals(0, moodEvent.getEmotion().compareTo("angry"));

        moodEvent.setEmotion("happy");
        assertEquals(0, moodEvent.getEmotion().compareTo("happy"));
    }


    @Test
    void testGetEmoji() {
        MoodEvent moodEvent = mockMoodEvent();

        moodEvent.setEmotion("angry");
        assertTrue( moodEvent.getEmoji()==0x1F620);
    }


    @Test
    void testGetColor() {
        MoodEvent moodEvent = mockMoodEvent();

        moodEvent.setEmotion("angry");
        assertTrue( moodEvent.getColor()==0xFFFF0000);
    }


    @Test
    void testGetReasonString() {
        MoodEvent moodEvent = mockMoodEvent();

        assertEquals(0, moodEvent.getReasonString().compareTo("No reason"));
    }

    @Test
    void testGetName() {
        MoodEvent moodEvent = mockMoodEvent();

        assertEquals(0, moodEvent.getName().compareTo("testName"));
    }

    @Test
    void testSetName() {
        MoodEvent moodEvent = mockMoodEvent();
        moodEvent.setName("newName");

        assertEquals(0, moodEvent.getName().compareTo("newName"));
    }


    @Test
    void testIsAttach() {
        MoodEvent moodEvent = mockMoodEvent();

        moodEvent.setAttach(true);
        assertTrue(moodEvent.isAttach());
    }

    @Test
    void testSetAttach() {
        MoodEvent moodEvent = mockMoodEvent();

        moodEvent.setAttach(true);
        assertTrue(moodEvent.isAttach());

        moodEvent.setAttach(false);
        assertFalse(moodEvent.isAttach());
    }


    @Test
    void testGetSituation() {
        MoodEvent moodEvent = mockMoodEvent();


        assertTrue( moodEvent.getSituation()==0);
    }


    @Test
    void testSetSituation() {
        MoodEvent moodEvent = mockMoodEvent();



        assertTrue( moodEvent.getSituation()==0);

        moodEvent.setSituation(1);
        assertTrue( moodEvent.getSituation()==1);
    }



    @Test
    void testGetImage() {
        MoodEvent moodEvent = mockMoodEvent();

        moodEvent.setImage("example1");
        assertTrue(moodEvent.getImage()=="example1");
    }



    @Test
    void testSetImage() {
        MoodEvent moodEvent = mockMoodEvent();

        moodEvent.setImage("example1");
        assertTrue(moodEvent.getImage()=="example1");

        moodEvent.setImage("example2");
        assertTrue(moodEvent.getImage()=="example2");
    }


    @Test
    void testSituationToInt() {
        MoodEvent moodEvent = mockMoodEvent();


        assertTrue(situationToInt("With one person")==1);
        assertTrue(situationToInt("Alone")==0);
    }


    @Test
    void testIntToSituation() {
        MoodEvent moodEvent = mockMoodEvent();


        assertTrue(intToSituation(0)=="Alone");
        assertTrue(intToSituation(1)=="With one person");
    }

    @Test
    void testGetID() {
        MoodEvent moodEvent = mockMoodEvent();


        assertTrue(moodEvent.getId()==123);
    }


    @Test
    void testSetID() {
        MoodEvent moodEvent = mockMoodEvent();



        assertTrue(moodEvent.getId()==123);

        moodEvent.setId(456);
        assertTrue(moodEvent.getId()==456);
    }
}















