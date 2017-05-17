package org.whiteboard.server.service;

import org.whiteboard.server.model.User;

/**
 * Created by 李浩然 on 2017/5/15.
 */
public interface UserService {
    public void addUser(User user);
    public User getUserById(long userId);
    public User getUserByName(String username);
    public User getUserByPhone(String phoneNumber);
    public User updateUserInformation(User user);
    public boolean loginIfPhoneAndPasswordIsCorrect(String phoneNumber, String password);
    public void logout(User user);
}
