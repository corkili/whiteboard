package org.whiteboard.server.service;

import org.whiteboard.server.model.Whiteboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 李浩然 on 2017/5/26.
 */
public class WhiteboardManager {
    private static WhiteboardManager Instance = new WhiteboardManager();

    public static WhiteboardManager getInstance() {
        return Instance;
    }

    private Map<Integer, List<Whiteboard>> whiteboardLists;

    private WhiteboardManager() {
        whiteboardLists = new ConcurrentHashMap<>();
    }

    public void addWhiteboard(int roomId, Whiteboard whiteboard) {
        List<Whiteboard> whiteboards = whiteboardLists.computeIfAbsent(roomId, k -> new ArrayList<>());
        whiteboards.add(whiteboard);
    }

    public List<Whiteboard> getWhiteboard(int roomId) {
        return whiteboardLists.computeIfAbsent(roomId, k -> new ArrayList<>());
    }

    public void removeWhiteboards(int roomId) {
        whiteboardLists.remove(roomId);
    }

    public int size() {
        return whiteboardLists.size();
    }

    public int getWhiteboardNumber(int roomId) {
        return whiteboardLists.computeIfAbsent(roomId, k -> new ArrayList<>()).size();
    }

    public void setMeetingIdForWhiteboards(long meetingId, int roomId) {
        List<Whiteboard> whiteboards = getWhiteboard(roomId);
        for(int i = 0; i < whiteboards.size(); i++) {
            whiteboards.get(i).setMeetingId(meetingId);
        }
    }

}
