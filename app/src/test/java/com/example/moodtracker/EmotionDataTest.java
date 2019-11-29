package com.example.moodtracker;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmotionDataTest {
    private EmotionData mockEmotionData() {
        EmotionData emotionData = new EmotionData("angry", 0x1F620, 0xFFFF0000);
        return emotionData;
    }


    @Test
    void testGetEmotion() {
        EmotionData emotionData = mockEmotionData();

        assertTrue(emotionData.getEmotion()=="angry");
    }


    @Test
    void testGetEmoji() {
        EmotionData emotionData = mockEmotionData();

        assertTrue(emotionData.getEmoji()==0x1F620);
    }

    @Test
    void testGetColor() {
        EmotionData emotionData = mockEmotionData();

        assertTrue(emotionData.getColor()==0xFFFF0000);
    }

}
