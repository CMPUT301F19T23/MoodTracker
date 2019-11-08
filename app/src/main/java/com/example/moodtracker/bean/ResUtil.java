package com.example.moodtracker.bean;

import com.example.moodtracker.MoodEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * The list records the datas of mood events into memory
 * This listUser records the datas of each user into memory
 *
 * @author xuhf0429
 */
public class ResUtil {
    public static List<MoodEvent> list = new ArrayList<MoodEvent>();
    public static List<User> listUser = new ArrayList<User>();
}
