package org.whiteboard.server.action;

import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.whiteboard.server.model.Meeting;
import org.whiteboard.server.service.MeetingService;
import org.whiteboard.server.session.SessionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Controller
@RequestMapping(value = "/meeting", method = RequestMethod.POST)
public class MeetingController {
    @Autowired
    private MeetingService meetingService;

    @RequestMapping(value = "/init_meeting", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> initMeeting(HttpServletRequest request, HttpSession session) {
        Map<String, String> map = new HashMap<>();
        String meetingName = request.getParameter("meeting_name");
        long organizerId = Long.parseLong(request.getParameter("organizer_id"));
        int maxPartnerNumber = Integer.parseInt(request.getParameter("max_partner_number"));
        String roomPassword = request.getParameter("room_password");
        if (organizerId != Long.parseLong(session.getAttribute(SessionContext.ATTR_USER_ID).toString())) {
            map.put("code", "205");
            map.put("message", "创建失败，用户信息不匹配!");
            map.put("meeting_info", "");
        } else {
            Meeting meeting = meetingService.initMeeting(meetingName, organizerId, maxPartnerNumber, roomPassword);
            if (meeting != null) {
                session.setAttribute(SessionContext.ATTR_ROOM_ID, meeting.getMeetingRoomId());
                map.put("code", "100");
                map.put("message", "创建成功");
                map.put("meeting_info", meetingService.getMeetingJSONString(meeting));
            } else {
                map.put("code", "205");
                map.put("message", "创建失败，初始化错误，请重试!");
                map.put("meeting_info", "");
            }
        }
        return map;
    }

    @RequestMapping(value = "/join_meeting", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> joinMeeting(HttpServletRequest request, HttpSession session) {
        Map<String, String> map = new HashMap<>();
        int roomId = Integer.parseInt(request.getParameter("room_id"));
        String roomPassword = request.getParameter("room_password");
        long userId = Long.parseLong(session.getAttribute(SessionContext.ATTR_USER_ID).toString());
        if (!meetingService.joinMeeting(userId, roomId, roomPassword)) {
            map.put("code", "206");
            map.put("message", "房间号或密码错误");
            map.put("meeting_info", "");
        } else {
            map.put("code", "100");
            map.put("message", "成功加入会议");
            Meeting meeting = meetingService.getRunningMeetingByUserId(userId);
            session.setAttribute(SessionContext.ATTR_ROOM_ID, meeting.getMeetingRoomId());
            map.put("meeting_info", meetingService.getMeetingJSONString(meeting));
        }
        return map;
    }

    @RequestMapping(value = "/quit_meeting", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> quitMeeting(HttpSession session) {
        Map<String, String> map = new HashMap<>();
        long userId = Long.parseLong(session.getAttribute(SessionContext.ATTR_USER_ID).toString());
        int roomId = Integer.parseInt(session.getAttribute(SessionContext.ATTR_ROOM_ID).toString());
        if (meetingService.quitMeeting(userId, roomId)) {
            map.put("code", "100");
            map.put("message", "成功退出会议");
            session.removeAttribute(SessionContext.ATTR_ROOM_ID);
        } else {
            map.put("code", "207");
            map.put("message", "退出会议失败");
        }
        return map;
    }

    @RequestMapping(value = "/enter_meeting", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> enterMeeting(HttpSession session) {
        Map<String, String> map = new HashMap<>();
        long userId = Long.parseLong(session.getAttribute(SessionContext.ATTR_USER_ID).toString());
        if (meetingService.enterMeeting(userId)) {
            map.put("code", "100");
            map.put("message", "成功回到会议");
            Meeting meeting = meetingService.getRunningMeetingByUserId(userId);
            session.removeAttribute(SessionContext.ATTR_ROOM_ID);
            session.setAttribute(SessionContext.ATTR_ROOM_ID, meeting.getMeetingRoomId());
            map.put("meeting_info", meetingService.getMeetingJSONString(meeting));
        } else {
            map.put("code", "208");
            map.put("message", "进入会议失败");
            map.put("meeting_info", "");
        }
        return map;
    }

    @RequestMapping(value = "/leave_meeting", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> leaveMeeting(HttpSession session) {
        Map<String, String> map = new HashMap<>();
        long userId = Long.parseLong(session.getAttribute(SessionContext.ATTR_USER_ID).toString());
        if (meetingService.leaveMeeting(userId)) {
            map.put("code", "100");
            map.put("message", "暂时离开会议");
            session.removeAttribute(SessionContext.ATTR_ROOM_ID);
        } else {
            map.put("code", "209");
            map.put("message", "暂离会议失败");
        }
        return map;
    }

    @RequestMapping(value = "/start_meeting", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> startMeeting(HttpSession session) {
        Map<String, String> map = new HashMap<>();
        int roomId = Integer.parseInt(session.getAttribute(SessionContext.ATTR_ROOM_ID).toString());
        meetingService.startMeeting(roomId);
        // 通知各客户端会议开始
        map.put("code", "100");
        map.put("message", "会议开始");
        Meeting meeting = meetingService.getRunningMeetingByRoomId(roomId);
        map.put("meeting_info", meetingService.getMeetingJSONString(meeting));
        return map;
    }

    @RequestMapping(value = "/stop_meeting", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> stopMeeting(HttpSession session) {
        Map<String, String> map = new HashMap<>();
        int roomId = Integer.parseInt(session.getAttribute(SessionContext.ATTR_ROOM_ID).toString());
        Meeting meeting = meetingService.finishAndSaveMeeting(roomId);
        if (meeting != null) {
            map.put("code", "100");
            map.put("message", "会议结束，相关信息保存成功");
            map.put("meeting_info", meetingService.getMeetingJSONString(meeting));
            session.removeAttribute(SessionContext.ATTR_ROOM_ID);
        } else {
            map.put("code", "210");
            map.put("message", "会议结束遇到问题，请重试");
            map.put("meeting_info", "");
        }
        return map;
    }

    @RequestMapping(value = "/get_my_meetings", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> getMyMeetings(HttpSession session) {
        Map<String, String> map = new HashMap<>();
        long userId = Long.parseLong(session.getAttribute(SessionContext.ATTR_USER_ID).toString());
        List<Meeting> meetings = meetingService.getMeetingsByUserId(userId);
        map.put("code", "100");
        map.put("message", "获取会议记录成功");
        map.put("meeting_number", String.valueOf(meetings.size()));
        JSONArray jsonArray = new JSONArray();
        for (Meeting meeting : meetings) {
            jsonArray.add(meetingService.getMeetingJSONString(meeting));
        }
        map.put("meeting_list", jsonArray.toString());
        return map;
    }

}
