package org.whiteboard.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.whiteboard.server.dao.UserDao;
import org.whiteboard.server.model.User;
import org.whiteboard.server.session.SessionContext;
import org.whiteboard.server.session.SessionListener;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    private Map<Long, User> loginedUsers = new ConcurrentHashMap<>();

    @Override
    public boolean addUserIfNotExist(User user) {
        if(userDao.findUserByPhoneFromDB(user.getPhoneNumber()) != null) {
            return false;
        }
        userDao.addUserToDB(user);
        return true;
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

    @Override
    public void sessionHandlerByCacheMap(HttpSession session) {
        synchronized (SessionListener.sessionContext) {
            String userId = session.getAttribute(SessionContext.ATTR_USER_ID).toString();
            HttpSession userSession = (HttpSession) SessionListener.sessionContext
                    .getSessionMap().get(userId);
            if (userSession != null) {
                // 注销在线用户
                userSession.invalidate();
                SessionListener.sessionContext.getSessionMap().remove(userId);
                // 清楚在线用户后，更新map，替换map
                SessionListener.sessionContext.getSessionMap().remove(session.getId());
                SessionListener.sessionContext.getSessionMap().put(userId, session);
            } else {
                // 根据当前sessionId取session对象，更新map
                SessionListener.sessionContext.getSessionMap().put(userId, SessionListener.sessionContext
                        .getSessionMap().get(session.getId()));
                SessionListener.sessionContext.getSessionMap().remove(session.getId());
            }
        }
    }


}
