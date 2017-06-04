package org.whiteboard.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.whiteboard.server.dao.WhiteboardDao;
import org.whiteboard.server.model.Whiteboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Service
public class WhiteboardServiceImpl implements WhiteboardService {
    @Autowired
    private WhiteboardDao whiteboardDao;

    private WhiteboardManager whiteboardManager;

    private Map<Integer, String> whiteboardInfos;

    public WhiteboardServiceImpl() {
        whiteboardManager = WhiteboardManager.getInstance();
        whiteboardInfos = new HashMap<>();
    }

    @Override
    public boolean saveWhiteboards(int roomId) {
        return whiteboardDao.addBoardsToDB(whiteboardManager.getWhiteboard(roomId));
    }

    @Override
    public void addWhiteboard(int roomId, Whiteboard whiteboard) {
        whiteboardManager.addWhiteboard(roomId, whiteboard);
    }

    @Override
    public void updateWhiteboardInfo(int roomId, String content) {
        whiteboardInfos.remove(roomId);
        whiteboardInfos.put(roomId, content);
    }

    @Override
    public String getWhiteboardInfo(int roomId) {
        String content = whiteboardInfos.get(roomId);
        if(content != null) {
            return content;
        } else {
            return "";
        }
    }

    @Override
    public void setMeetingId(long meetingId, int roomId) {
        whiteboardManager.setMeetingIdForWhiteboards(meetingId, roomId);
    }

    @Override
    public List<Whiteboard> getWhiteboardsByMeetingId(long meetingId) {
        return whiteboardDao.findBoardsByMeetingId(meetingId);
    }
}
