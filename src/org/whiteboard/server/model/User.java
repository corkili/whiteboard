package org.whiteboard.server.model;

import net.sf.json.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Entity
@Table(name = "table_user")
public class User {
    @Column(name = "user_id")
    private long userId;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "authority")
    private int authority;
    @Column(name = "head_image")
    private String headImage;

    public User(){

    }

    public User(long userId, String username, String password, String phoneNumber, int authority, String headImage) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.authority = authority;
        this.headImage = headImage;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        jsonObject.put("phone_number", phoneNumber);
        return jsonObject.toString();
    }
}
