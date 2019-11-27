package com.example.moodtracker.bean;

import com.example.moodtracker.MoodEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataUtil {
    private static List<MoodEvent> list = new ArrayList<MoodEvent>();
    private static List<User> listUser = new ArrayList<User>();

    private static Map<String, List<String>> mapFriend = new HashMap<String, List<String>>();
    private static Map<String, List<String>> mapAsk = new HashMap<String, List<String>>();

    public static boolean login(String username, String password) {
        for (int i = 0; i < listUser.size(); i++) {
            if (listUser.get(i).getUsername().equals(username) && listUser.get(i).getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public static boolean register(String username, String password) {
        for (int i = 0; i < listUser.size(); i++) {
            if (listUser.get(i).getUsername().equals(username)) {
                return false;
            }
        }

        listUser.add(new User(username, password));
        return true;
    }

    public static void getAll(String username, List<MoodEvent> list2) {
        list2.clear();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUsername().equals(username)) {
                list2.add(list.get(i));
            }
        }
    }

    public static String getUsernameByUsername(String username) {
        for (int i = 0; i < listUser.size(); i++) {
            if (listUser.get(i).getUsername().equals(username)) {
                return username;
            }
        }

        return null;
    }

    public static void getOtherUser(List<User> list2) {
        list2.clear();
        list2.addAll(listUser);
    }

    public static void addData(MoodEvent e) {
        list.add(e);
    }

    public static MoodEvent getMoodEvent(String username, String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id) && list.get(i).getUsername().equals(username)) {
                return list.get(i);
            }
        }

        return null;
    }

    public static void removeMoodEvent(String username, String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id) && list.get(i).getUsername().equals(username)) {
                list.remove(i);
                break;
            }
        }
    }

    public static void updateMoodEvent(String username, String id, boolean attach, String name, int i0, int i1, int i2, int i3, int i4, String s1, String s2, String reason, String image) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id) && list.get(i).getUsername().equals(username)) {
                list.get(i).setAttach(attach);
                list.get(i).setEventName(name);
                list.get(i).setDate(i0, i1, i2, i3, i4);
                list.get(i).setEmotion(s1);
                list.get(i).setSituation(s2);
                list.get(i).setReasonString(reason);
                list.get(i).setImage(image);
                break;
            }
        }
    }

    public static void insertFriendAsk(String username, String username2) {
        List<String> list = mapAsk.get(username2);
        if (list == null) {
            list = new ArrayList<String>();
            list.add(username);
            mapAsk.put(username2, list);
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (username.equals(list.get(i))) {
                    return;
                }
            }
            list.add(username);
        }
    }

    public static void deleteFriendAsk(String username, String username2) {
        List<String> list = mapAsk.get(username);
        if (list == null) {
            ;
        } else {
            list.remove(username2);
        }
    }

    public static void getFriends(String username, List<String> recycleList1) {
        recycleList1.clear();

        List<String> list = mapFriend.get(username);
        if (list != null) {
            recycleList1.addAll(list);
        }
    }

    public static int getAskByUsername(String username) {
        List<String> list = mapAsk.get(username);
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }

    public static List<String> getAskListByUsername(String username) {
        List<String> list = mapAsk.get(username);
        if (list == null) {
            return null;
        } else {
            return list;
        }
    }

    public static void insertFriendAccept(String username, String username2) {
        List<String> list = mapFriend.get(username);
        if (list == null) {
            list = new ArrayList<String>();
            list.add(username2);
            mapFriend.put(username, list);
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (username2.equals(list.get(i))) {
                    return;
                }
            }
            list.add(username2);
        }
    }

    public static void deleteFriend(String username, String username2) {
        List<String> list = mapFriend.get(username);
        if (list == null) {
        } else {
            list.remove(username2);
        }

        List<String> list2 = mapFriend.get(username2);
        if (list2 == null) {
        } else {
            list2.remove(username);
        }
    }

}
