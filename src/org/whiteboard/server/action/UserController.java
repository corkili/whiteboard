package org.whiteboard.server.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Controller
@RequestMapping(value = "/user", method = RequestMethod.POST)
@SessionAttributes("userId")
public class UserController {
    @RequestMapping(value = "/login", method = RequestMethod.POST)
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

    @RequestMapping(value = "/register", method = RequestMethod.POST)
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
