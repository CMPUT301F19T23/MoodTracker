package com.example.moodtracker;

import android.graphics.Color;

/**
 * Object representing various information about an emotional state
 */
public class EmotionData {
    private String emotion; // eg angry, sad, happy etc
    private String emoji; // I believe that emoji are now part of unicode, but even if I'm wrong
    // then we can use things like :) for placeholders
    private int color; // implementation might change but I think android stores colors as integers

    final public static EmotionData ANGRY_DATA = new EmotionData("angry", ">:(", 0xFFFF0000); // color is red
    final public static EmotionData HAPPY_DATA = new EmotionData("happy", ":)", 0xFFFFFF00); // color is yellow
    final public static EmotionData SAD_DATA = new EmotionData("sad", ":(", 0xFF6DADAC); // color is pale blue
    final public static EmotionData NEUTRAL_DATA = new EmotionData("neutral", ":|", 0xFFCFCFCF); // color is light gray


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
     *          the emoji as a string
     */
    public String getEmoji() {
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

    EmotionData(String emotion, String emoji, int color){
        this.emotion = emotion;
        this.emoji = emoji;
        this.color = color;

    }
}
