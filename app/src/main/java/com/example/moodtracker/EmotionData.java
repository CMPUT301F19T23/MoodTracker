package com.example.moodtracker;

/**
 * Object representing various information about an emotional state
 */
public class EmotionData {
    private String emotion;
    private int emoji; // int instead of string to represent unicode characters
    private int color;

    final public static EmotionData ANGRY_DATA = new EmotionData("angry", 0x1F620, 0xFFFF0000); // color is red
    final public static EmotionData HAPPY_DATA = new EmotionData("happy", 0x1F60A, 0xFFFFFF00); // color is yellow
    final public static EmotionData SAD_DATA = new EmotionData("sad",0x1F622 , 0xFF6DADAC); // color is pale blue
    final public static EmotionData NEUTRAL_DATA = new EmotionData("neutral", 0x1F612, 0xFFCFCFCF); // color is light gray


    /**
     * Get the name component of this object
     * @return
     *          the name as a string
     */
    public String getEmotion() {
        return emotion;
    }

    /**
     * Get the emoji component of this object
     * @return
     *          the emoji as an int
     */
    public int getEmoji() {
        return emoji;
    }

    /**
     * Get the color component of this object
     * @return
     *          the color as an int
     */
    public int getColor() {
        return color;
    }

    EmotionData(String emotion, int emoji, int color){
        this.emotion = emotion;
        this.emoji = emoji;
        this.color = color;

    }
}
