package org.whiteboard.server.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.whiteboard.server.model.User;
import org.whiteboard.server.service.MeetingService;
import org.whiteboard.server.service.UserService;
import org.whiteboard.server.session.SessionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Controller
@RequestMapping(value = "/user", method = RequestMethod.POST)
@SessionAttributes("userId")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private MeetingService meetingService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> login(HttpServletRequest request, HttpSession session) {
        Map<String, String> map = new HashMap<>();
        String phoneNumber = request.getParameter("phone_number");
        String password = request.getParameter("password");
        if(userService.loginIfPhoneAndPasswordIsCorrect(phoneNumber, password)) {
            User user = userService.getUserByPhone(phoneNumber);
            session.setAttribute(SessionContext.ATTR_USER_ID,
                    String.valueOf(user.getUserId()));
            userService.sessionHandlerByCacheMap(session);
            if (meetingService.enterMeeting(user.getUserId())) {
                int roomId = meetingService.getRoomIdByUserId(user.getUserId());
                session.setAttribute(SessionContext.ATTR_ROOM_ID, String.valueOf(roomId));
                map.put("roomId", String.valueOf(roomId));
            }
            map.put("code", "100");
            map.put("message", "登录成功");
            map.put("user_info", user.toJSONString());
            map.put("roomId", "0");
        } else {
            map.put("code", "201");
            map.put("message", "手机号或密码错误");
            map.put("user_info", "");
        }
        return map;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> logout(HttpServletRequest request, HttpSession session) {
        Map<String, String> map = new HashMap<>();
        long userId = Long.parseLong(request.getParameter("user_id"));
        userService.logout(userService.getUserById(userId));
        session.invalidate();
        map.put("code", "100");
        map.put("message", "注销成功");
        return map;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> register(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String phoneNumber = request.getParameter("phone_number");
        // register
        User user = new User(UserService.DEFAULT_USER_ID, username, password, phoneNumber,
                UserService.DEFAULT_AUTHORITY, UserService.DEFAULT_HEAD_IMAGE);
        if (userService.addUserIfNotExist(user)) {
            map.put("code", "100");
            map.put("message", "注册成功！");
        } else {
            map.put("code", "201");
            map.put("message", "手机号已存在！");
        }
        return map;
    }

    @RequestMapping(value = "/modify_password", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> modifyPassword(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        String phoneNumber = request.getParameter("phone_number");
        String password = request.getParameter("password");
        // modify
        User user = userService.getUserByPhone(phoneNumber);
        if (user != null) {
            user.setPassword(password);
            user = userService.updateUserInformation(user);
            if(user != null) {
                // success
                map.put("code", "100");
                map.put("message", "密码修改成功");
            } else {
                // failed
                map.put("code", "202");
                map.put("message", "密码修改失败");
            }
        } else {
            // 手机号不存在
            map.put("code", "202");
            map.put("message", "手机号不存在");
        }
        return map;
    }

    @RequestMapping(value = "/modify_username", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> modifyUsername(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        String phoneNumber = request.getParameter("phone_number");
        String username = request.getParameter("username");
        // modify
        User user = userService.getUserByPhone(phoneNumber);
        if (user != null) {
            user.setUsername(username);
            user = userService.updateUserInformation(user);
            if(user != null) {
                // success
                map.put("code", "100");
                map.put("message", "用户名修改成功");
            } else {
                // failed
                map.put("code", "203");
                map.put("message", "用户名修改失败");
            }
        } else {
            // 手机号不存在
            map.put("code", "204");
            map.put("message", "手机号不存在");
        }
        return map;
    }

    @RequestMapping(value = "/valid_phone", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> validPhoneNumber(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        String phoneNumber = request.getParameter("phone_number");
        // modify
        User user = userService.getUserByPhone(phoneNumber);
        if (user != null) {
            map.put("code", "100");
            map.put("message", "手机号存在");
        } else {
            // 手机号不存在
            map.put("code", "204");
            map.put("message", "手机号不存在");
        }
        return map;
    }
}
