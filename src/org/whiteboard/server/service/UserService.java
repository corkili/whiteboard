package org.whiteboard.server.service;

import org.whiteboard.server.model.User;

import javax.servlet.http.HttpSession;

/**
 * Created by 李浩然 on 2017/5/15.
 */
public interface UserService {
    public static final long DEFAULT_USER_ID = -1L;
    public static final int DEFAULT_AUTHORITY = 0x1;
    public static final String DEFAULT_HEAD_IMAGE = "image/head/default.png";
    public boolean addUserIfNotExist(User user);
    public User getUserById(long userId);
    public User getUserByName(String username);
    public User getUserByPhone(String phoneNumber);
    public User updateUserInformation(User user);
    public boolean loginIfPhoneAndPasswordIsCorrect(String phoneNumber, String password);
    public void logout(User user);
    public void sessionHandlerByCacheMap(HttpSession session);
}
