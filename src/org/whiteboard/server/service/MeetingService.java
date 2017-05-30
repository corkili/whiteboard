package org.whiteboard.server.service;

import net.sf.json.JSONObject;
import org.whiteboard.server.model.Meeting;

import java.util.List;

/**
 * Created by 李浩然 on 2017/5/15.
 */
public interface MeetingService {
    public static final long DEFAULT_MEETING_ID = -1L;
    public static final String DEFAULT_MEETING_NAME = "unnamed meeting";
    public static final int DEFAULT_PARTNER_NUMBER = 1; // 组织者
    public static final long DEFAULT_ORGANIZER_ID = -1L;
    public static final String DEFAULT_NOTE_PATH = "meeting/notes/default.doc";
    public static final int DEFAULT_ROOM_ID = 0;

    public Meeting initMeeting(String meetingName, long organizerId, int maxPartnerNumber, String roomPassword);
    public void startMeeting(int roomId);
    public Meeting finishAndSaveMeeting(int roomId);
    public boolean destroyMeeting(int roomId);

    public boolean joinMeeting(long userId, int roomId, String password);
    public boolean quitMeeting(long userId, int roomId);
    public boolean enterMeeting(long userId);
    public boolean leaveMeeting(long userId);

    public int getRoomIdByUserId(long userId);
    public Meeting getRunningMeetingByUserId(long userId);
    public JSONObject getMeetingJSON(Meeting meeting);
    public Meeting getRunningMeetingByRoomId(int roomId);

    public Meeting getMeetingById(long meetingId);
    public List<Meeting> getMeetingsByUserId(long userId);
}
