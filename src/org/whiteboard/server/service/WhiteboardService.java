package org.whiteboard.server.service;

import org.whiteboard.server.model.Whiteboard;

import java.util.List;

/**
 * Created by 李浩然 on 2017/5/15.
 */
public interface WhiteboardService {
    public boolean saveWhiteboards(int roomId);

    public void addWhiteboard(int roomId, Whiteboard whiteboard);
    public void updateWhiteboardInfo(int roomId, String content);

    public String getWhiteboardInfo(int roomId);
    public void setMeetingId(long meetingId, int roomId);

}
