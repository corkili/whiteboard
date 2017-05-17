package org.whiteboard.server.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.whiteboard.server.model.User;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public User findUserByIdFromDB(long userId) {
        User user = null;
        String sql = getSqlForSelectUser(COL_USER_ID);
        try {
            RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
            user = jdbcTemplate.queryForObject(sql, new Object[]{ userId }, rowMapper);
        } catch (Exception e) {
            // do nothing
        }
        return user;
    }

    @Override
    public User findUserByNameFromDB(String username) {
        User user = null;
        String sql = getSqlForSelectUser(COL_USERNAME);
        try {
            RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
            user = jdbcTemplate.queryForObject(sql, new Object[]{ username }, rowMapper);
        } catch (Exception e) {
            // do nothing
        }
        return user;
    }

    @Override
    public User findUserByPhoneFromDB(String phoneNumber) {
        User user = null;
        String sql = getSqlForSelectUser(COL_PHONE_NUMBER);
        try {
            RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
            user = jdbcTemplate.queryForObject(sql, new Object[]{ phoneNumber }, rowMapper);
        } catch (Exception e) {
            // do nothing
        }
        return user;
    }

    @Override
    public User findUserByPhoneAndPasswordFromDB(String phoneNumber, String password) {
        User user = null;
        String sql = getSqlForSelectUser(COL_PHONE_NUMBER, COL_PASSWORD);
        try {
            RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
            user = jdbcTemplate.queryForObject(sql, new Object[]{ phoneNumber, password }, rowMapper);
        } catch (Exception e) {
            // do nothing
        }
        return user;
    }

    @Override
    public void addUserToDB(User user) {
        String sql = getSqlForInsertUser();
        Object[] params = new Object[] { user.getUsername(), user.getPassword(),
                user.getPhoneNumber(), user.getAuthority(), user.getHeadImage() };
        try {
            jdbcTemplate.update(sql, params);
        } catch (Exception e) {
            e.printStackTrace();
            // log
        }
    }

    @Override
    public void updateUser(User user) {
        String sql = getSqlForUpdateUser();
        Object[] params = new Object[] { user.getUsername(), user.getPassword(), user.getPhoneNumber(),
                user.getAuthority(), user.getHeadImage(), user.getUserId() };
        try {
            jdbcTemplate.update(sql,params);
        } catch (Exception e) {
            e.printStackTrace();
            // log
        }
    }

    private String getSqlForSelectUser(String col) {
        return "select " + "*" + " from " + TABLE_USER + " where "
                + col + "=?";
    }

    private String getSqlForSelectUser(String col1, String col2) {
        return "select " + "*" + " from " + TABLE_USER + " where "
                + col1 + "=?" + " and " + col2 + "=?";
    }

    private String getSqlForInsertUser() {
        return "insert into " + TABLE_USER + " (" + COL_USERNAME + ',' + COL_PASSWORD + ','
                + COL_PHONE_NUMBER + ',' + COL_AUTHORITY + ',' + COL_HEAD_IMAGE + ") values(?,?,?,?,?)";
    }

    private String getSqlForUpdateUser() {
        return "update " + TABLE_USER + " set " + COL_USERNAME + "=?," + COL_PASSWORD + "=?,"
                + COL_PHONE_NUMBER + "=?," + COL_AUTHORITY + "=?," + COL_HEAD_IMAGE + "=? where "
                + COL_USER_ID + "=?";
    }
}
