package com.example.moodtracker;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.moodtracker.EmotionData.ANGRY_DATA;
import static com.example.moodtracker.EmotionData.HAPPY_DATA;
import static com.example.moodtracker.EmotionData.NEUTRAL_DATA;
import static com.example.moodtracker.EmotionData.SAD_DATA;

/**
 * Object representing a participant's feelings at a certain time and place,
 * as well as some information about why they felt the way they did
 */
public class MoodEvent implements Serializable{
    final public static SimpleDateFormat longFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    final public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    final public static SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

    final public static int SITUATION_ALONE = 0;
    final public static int SITUATION_ONE_PERSON = 1;
    final public static int SITUATION_SEVERAL_PEOPLE = 2;
    final public static int SITUATION_CROWD = 3;


    private Calendar date; // date of event, formatted to include specific day and time of day fields
    /*I suspect you'll need to read up on Calendar objects especially for the UI
    https://docs.oracle.com/javase/8/docs/api/java/util/Calendar.html
    https://www.mkyong.com/java/java-date-and-calendar-examples/
    Calendars are abstract, instantiate with GregorianCalendar (see examples in MoodEventTest)
    */

    private String reasonString;
    //private type reasonImg; // cannot yet implement. More research needed
    private EmotionData emotionData;
    //private Location location; // cannot yet implement. More research needed
    //private Participant owner; // waiting on Participant implementation.

    private String name;
    private boolean attach;
    private int situation;
    private String image;
    private long id;

    final public static EmotionData[] MOOD_DATA = {ANGRY_DATA, HAPPY_DATA, SAD_DATA, NEUTRAL_DATA};

    MoodEvent(String name, long id, int situation, Calendar d, String emotion){
        this.name = name;
        this.id = id;
        this.situation = situation;
        this.date = d;
        this.setEmotion(emotion);
        this.reasonString = "";
    }

    MoodEvent(String name, long id, int situation, Calendar d, String emotion, String rstr){
        this.name = name;
        this.id = id;
        this.situation = situation;
        this.date = d;
        this.setEmotion(emotion);
        this.reasonString = rstr;
    }

    /**
     * Edit the details of the Calendar object representing the date this MoodEvent took place
     * @param year
     *              the year this took place
     * @param month
     *              the month this took place (0-11)
     * @param date
     *              the date this took place (1-31)
     * @param hourOfDay
     *              the hour this took place (0-23)
     * @param minute
     *              the minute this took place
     */
    public void setDate(int year, int month, int date, int hourOfDay, int minute){
        this.date.set( year,  month,  date,  hourOfDay,  minute);
    }

    /**
     * Get the calendar object representing the date this MoodEvent took place
     * @return
     *          the date Calendar object
     */
    public Calendar getDate(){
        return this.date;
    }

    public String getTime(){
        return timeFormat.format(date.getTime());
    }

    public String getDay(){
        return dayFormat.format(date.getTime());
    }

    /**
     * Set the emotionData object to one of the predefined ones based on the string inserted.
     * If it doesn't match any string, nothing happens
     * @param emotion
     *                  the string of the desired emotion
     */
    public void setEmotion(String emotion){
        switch(emotion.toLowerCase()){
            case "angry":
                this.emotionData = ANGRY_DATA;
                break;
            case "happy":
                this.emotionData = HAPPY_DATA;
                break;
            case "sad":
                this.emotionData = SAD_DATA;
                break;
            case "neutral":
                this.emotionData = NEUTRAL_DATA;
                break;
            default: break;
        }
    }

    /**
     * Get the string representation of this object's mood
     * @return
     *          the mood string
     */
    public String getEmotion() {
        return this.emotionData.getEmotion();
    }

    /**
     * Get the emoji representation of this object's mood
     * @return
     *          the mood emoji
     */
    public int getEmoji() {
        return this.emotionData.getEmoji();
    }

    /**
     * Get the color representation of this object's mood
     * @return
     *          the mood color
     */
    public int getColor() {
        return this.emotionData.getColor();
    }

    /**
     * Get the commented reason for this MoodEvent
     * @return
     *          the comment as a string
     */
    public String getReasonString() {
        return reasonString;
    }

    /**
     * Set the text comment for this MoodEvent
     * @param reasonString
     *                  the comment
     */
    public void setReasonString(String reasonString) {
        this.reasonString = reasonString;
    }

    /*public Type getReasonImg() {
        return reasonImg;
    }

    public void setReasonString(Type reasonImg) {
        this.reasonImg = reasonImg;
    }*/

   /*public Type getLocation() {
        return location;
    }

    public void setLocation(Type location) {
        this.location = location;
    }*/

   /*public Participant getOwner() {
        return owner;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAttach() {
        return attach;
    }

    public void setAttach(boolean attach) {
        this.attach = attach;
    }


    public int getSituation() {
        return situation;
    }

    public void setSituation(int situation) {
        this.situation = situation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static int situationToInt(String situation){
        switch(situation){
            case "Alone": return SITUATION_ALONE;
            case "With one person": return SITUATION_ONE_PERSON;
            case "With several people": return SITUATION_SEVERAL_PEOPLE;
            case "With a crowd": return SITUATION_CROWD;
            default: return -1;
        }
    }

    public static String intToSituation(int i){
        switch(i){
            case SITUATION_ALONE: return "Alone";
            case SITUATION_ONE_PERSON: return "With one person";
            case SITUATION_SEVERAL_PEOPLE: return "With several people";
            case SITUATION_CROWD: return "With a crowd";
            default: return "Error";
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}