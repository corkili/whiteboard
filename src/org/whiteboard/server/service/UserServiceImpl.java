package org.whiteboard.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.whiteboard.server.dao.UserDao;
import org.whiteboard.server.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    private Map<Long, User> loginedUsers = new HashMap<>();

    @Override
    public void addUser(User user) {
        userDao.addUserToDB(user);
    }

    @Override
    public User getUserById(long userId) {
        User user;
        synchronized (loginedUsers) {
            user = loginedUsers.get(userId);
            if(user != null) {
                return user;
            }
        }
        user = userDao.findUserByIdFromDB(userId);
        return user;
    }

    @Override
    public User getUserByName(String username) {
        if(username == null){
            return null;
        }
        synchronized (loginedUsers) {
            Collection<User> users = loginedUsers.values();
            for(User user : users) {
                if(user.getUsername().equals(username)){
                    return user;
                }
            }
        }
        return userDao.findUserByNameFromDB(username);
    }

    @Override
    public User getUserByPhone(String phoneNumber) {
        if(phoneNumber == null){
            return null;
        }
        synchronized (loginedUsers) {
            Collection<User> users = loginedUsers.values();
            for(User user : users) {
                if(user.getPhoneNumber().equals(phoneNumber)){
                    return user;
                }
            }
        }
        return userDao.findUserByPhoneFromDB(phoneNumber);
    }

    @Override
    public User updateUserInformation(User user) {
        if(user == null){
            return user;
        }
        userDao.updateUser(user);
        User newUser = userDao.findUserByIdFromDB(user.getUserId());
        if(newUser == null){
            return newUser;
        }
        synchronized (loginedUsers) {
            loginedUsers.remove(newUser.getUserId());
            loginedUsers.put(newUser.getUserId(), newUser);
        }
        return newUser;
    }

    @Override
    public boolean loginIfPhoneAndPasswordIsCorrect(String phoneNumber, String password) {
        User user;
        user = userDao.findUserByPhoneAndPasswordFromDB(phoneNumber, password);
        if(user == null){
            return false;
        } else {
            synchronized (loginedUsers) {
                loginedUsers.remove(user.getUserId());
                loginedUsers.put(user.getUserId(), user);
            }
            return true;
        }
    }

    @Override
    public void logout(User user) {
        if(user == null){
            return;
        }
        synchronized (loginedUsers) {
            loginedUsers.remove(user.getUserId());
        }
    }
}
