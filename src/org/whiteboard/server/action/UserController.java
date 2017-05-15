package org.whiteboard.server.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.whiteboard.server.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Controller
@RequestMapping(name = "/user", method = RequestMethod.POST)
public class UserController {
    @RequestMapping(name = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> login(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        if("15528235793".equals(request.getParameter("phone_number"))
                && "123456".equals(request.getParameter("password"))) {
            map.put("code", "100");
            map.put("message", "登录成功");
            map.put("userId", "0");
        } else {
            map.put("code", "201");
            map.put("message", "用户名或密码错误");
            map.put("userId", "-1");
        }
        return map;
    }

    @RequestMapping(name = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> register(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String phone_number = request.getParameter("phone_number");
        // register
        map.put("code", "100");
        map.put("message", "注册成功");
        return map;
    }

}
