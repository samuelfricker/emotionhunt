package ch.fhnw.ip5.emotionhunt.helpers;

import java.util.ArrayList;

import ch.fhnw.ip5.emotionhunt.models.User;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.helpers
 *
 * @author Benjamin Bur
 */
public class UserList {
    public User me;
    public ArrayList<User> users;
    public ArrayList<User> recipients;
    private static UserList ourInstance = new UserList();

    public static UserList getInstance() {
        return ourInstance;
    }

    public String getMyName() {
        if (me == null) return "";
        return me.name;
    }

    private UserList() {
        users = new ArrayList<>();
    }
}
