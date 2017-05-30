package org.whiteboard.server.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.whiteboard.server.model.MeetingMessage;
import org.whiteboard.server.model.Whiteboard;
import org.whiteboard.server.polling.MessageContainer;
import org.whiteboard.server.service.MeetingService;
import org.whiteboard.server.service.WhiteboardManager;
import org.whiteboard.server.service.WhiteboardService;
import org.whiteboard.server.session.SessionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Controller
@RequestMapping(value = "/whiteboard", method = RequestMethod.POST)
public class WhiteboardController {
    @Autowired
    private WhiteboardService whiteboardService;

    @Autowired
    private MeetingService meetingService;

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updateContent(HttpServletRequest request, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        int roomId = Integer.parseInt(session.getAttribute(SessionContext.ATTR_ROOM_ID).toString());
        String content = request.getParameter("content");
        whiteboardService.updateWhiteboardInfo(roomId, content);
        map.put("code", "100");
        map.put("message", "更新成功");
        return map;
    }

    @RequestMapping(value = "sync", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> syncContent(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        int roomId = Integer.parseInt(session.getAttribute(SessionContext.ATTR_ROOM_ID).toString());
        String content = whiteboardService.getWhiteboardInfo(roomId);
        String code = "100";
        String message = "获取白板内容成功";
        if("".equals(content)) {
            code = "212";
            message = "获取白板内容失败";
        }
        map.put("code", code);
        map.put("message", message);
        map.put("content", content);
        return map;
    }

    @RequestMapping(value = "save_whiteboard", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> saveWhiteboard(@RequestParam("content") String content, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        int roomId = Integer.parseInt(session.getAttribute(SessionContext.ATTR_ROOM_ID).toString());
        String whiteboardName = meetingService.getRunningMeetingByRoomId(roomId).getMeetingName() + "_"
                + String.valueOf(WhiteboardManager.getInstance().getWhiteboardNumber(roomId));
        whiteboardService.addWhiteboard(roomId, new Whiteboard(-1L, content, -1L, whiteboardName));
        map.put("code", "100");
        map.put("message", "保存白板成功");
        return map;
    }


}
