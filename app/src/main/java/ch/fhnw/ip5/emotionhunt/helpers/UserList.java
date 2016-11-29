package ch.fhnw.ip5.emotionhunt.helpers;

import java.util.ArrayList;

import ch.fhnw.ip5.emotionhunt.models.User;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.helpers
 *
 * @author Benjamin Bur
 */
public class UserList {
    public ArrayList<User> users;
    private static UserList ourInstance = new UserList();

    public static UserList getInstance() {
        return ourInstance;
    }

    private UserList() {
        users = new ArrayList<>();
    }
}
