package org.whiteboard.server.dao;

import org.whiteboard.server.model.User;

import java.util.List;

/**
 * Created by 李浩然 on 2017/5/15.
 */
public interface UserDao {
    public static final String TABLE_USER = "table_user";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_PHONE_NUMBER = "phone_number";
    public static final String COL_AUTHORITY = "authority";
    public static final String COL_HEAD_IMAGE = "head_image";

    // 查询
    public User findUserByIdFromDB(long userId);
    public User findUserByNameFromDB(String username);
    public User findUserByPhoneFromDB(String phoneNumber);
    public User findUserByPhoneAndPasswordFromDB(String phoneNumber, String password);

    // 添加
    public void addUserToDB(User user);

    // 查询
    public void updateUser(User user);
}
