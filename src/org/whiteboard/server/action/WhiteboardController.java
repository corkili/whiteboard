package org.whiteboard.server.action;

import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.whiteboard.server.model.Whiteboard;
import org.whiteboard.server.service.MeetingService;
import org.whiteboard.server.service.WhiteboardManager;
import org.whiteboard.server.service.WhiteboardService;
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
@RequestMapping(value = "/whiteboard", method = RequestMethod.POST)
public class WhiteboardController {
    @Autowired
    private WhiteboardService whiteboardService;

    @Autowired
    private MeetingService meetingService;

    private Logger logger = Logger.getLogger(WhiteboardController.class);

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updateContent(HttpServletRequest request, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        int roomId = Integer.parseInt(session.getAttribute(SessionContext.ATTR_ROOM_ID).toString());
        String content = request.getParameter("content");
        whiteboardService.updateWhiteboardInfo(roomId, content);
        //logger.info("update： " + whiteboardService.getWhiteboardInfo(roomId).substring(0, 5)
        //        + " - " + whiteboardService.getWhiteboardInfo(roomId).length());
        //logger.info("update： " + content.substring(0, 5) + " - " + content.length());
        map.put("code", "100");
        map.put("message", "更新成功");
        map.put("online_partners", meetingService.getRunningMeetingByRoomId(roomId).getPartnerNumber());
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
        //logger.info("sync: " + content.substring(0, 5) + " - " + content.length());
        map.put("code", code);
        map.put("message", message);
        map.put("content", content);
        map.put("online_partners", meetingService.getRunningMeetingByRoomId(roomId).getPartnerNumber());
        return map;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> saveWhiteboard(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        int roomId = Integer.parseInt(session.getAttribute(SessionContext.ATTR_ROOM_ID).toString());
        String whiteboardName = meetingService.getRunningMeetingByRoomId(roomId).getMeetingName() + "_"
                + String.valueOf(WhiteboardManager.getInstance().getWhiteboardNumber(roomId));
        String content = whiteboardService.getWhiteboardInfo(roomId);
        logger.info("save " +  roomId);
        whiteboardService.addWhiteboard(roomId, new Whiteboard(-1L, content, -1L, whiteboardName));
        map.put("code", "100");
        map.put("message", "保存白板成功");
        return map;
    }

    @RequestMapping(value = "get_whiteboards", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> saveWhiteboard(@RequestParam("meeting_id") String meetingId) {
        Map<String, Object> map = new HashMap<>();
        logger.info("get_whiteboards...");
        logger.info("meetingId: " + meetingId);
        List<Whiteboard> whiteboards = whiteboardService.getWhiteboardsByMeetingId(Long.parseLong(meetingId));
        logger.info("size: " + whiteboards.size());
        map.put("code", 100);
        map.put("message", "获取白板成功");
        map.put("whiteboard_number", whiteboards.size());
        JSONArray contents = new JSONArray();
        for (Whiteboard whiteboard : whiteboards) {
            logger.info(whiteboard.getBoardContent().substring(0, 10) + " : " + whiteboard.getBoardContent().length());
            contents.add(whiteboard.getBoardContent());
        }
        map.put("whiteboards", contents);
        return map;
    }

}
