package ch.fhnw.ip5.emotionhunt.helpers;

import java.util.ArrayList;

import ch.fhnw.ip5.emotionhunt.models.User;

/**
 * Singleton class that holds a list with all users received from the server's API
 * and the current logged in user stored in the member variable 'me'.
 */
public class UserList {
    /** own user */
    public User me;
    /** all users */
    public ArrayList<User> users;

    private static UserList userList = new UserList();
    public static UserList getInstance() {
        return userList;
    }

    /**
     * Returns the current name of the logged in user if exists.
     * @return
     */
    public String getMyName() {
        if (me == null) return "";
        return me.name;
    }

    private UserList() {
        users = new ArrayList<>();
    }
}
