package org.whiteboard.server.service;

import org.whiteboard.server.model.Meeting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 李浩然 on 2017/5/21.
 */
public class MeetingManager {
    private static MeetingManager Instance = new MeetingManager();

    public static MeetingManager getInstance() {
        return Instance;
    }

    private Map<Integer, Meeting> runningMeetings;  // <roomId, Meeting>
    private Map<Long, Integer> participating;       // <userId, roomId>
    private Map<Long, Boolean> inRoom;              // <userId, Boolean>
    private List<Integer> roomIds;                  // <roomId>

    private MeetingManager() {
        runningMeetings = new ConcurrentHashMap<>();
        participating = new ConcurrentHashMap<>();
        inRoom = new ConcurrentHashMap<>();
        roomIds = new ArrayList<>();
    }

    public Meeting getMeeting(int roomId) {
        return runningMeetings.get(roomId);
    }

    public boolean addUserToMeeting(long userId, int roomId, String password){
        if (!isJoinedMeeting(userId) && !isPartnerNumberReachedMax(roomId)) {
            // 没有参与任何会议
            Meeting meeting = runningMeetings.get(roomId);
            if (!meeting.getRoomPassword().equals(password)) {
                return false;
            }
            if (meeting.addPartner(userId)) {
                participating.put(userId, roomId);
                inRoom.put(userId, true);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean removeUserFromMeeting(long userId, int roomId) {
        Meeting meeting = runningMeetings.get(roomId);
        if (meeting != null) {
            participating.remove(userId);
            inRoom.remove(userId);
            meeting.removePartner(userId);
            if (userId == meeting.getOrganizerId()) {
                return removeMeeting(roomId);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean addMeeting(Meeting meeting) {
        if (!isJoinedMeeting(meeting.getOrganizerId()) && !isExistMeeting(meeting.getMeetingRoomId())) {
            runningMeetings.put(meeting.getMeetingRoomId(), meeting);
            roomIds.add(meeting.getMeetingRoomId());
            for (long partnerId : meeting.getPartnerIds()) {
                participating.put(partnerId, meeting.getMeetingRoomId());
                inRoom.put(partnerId, true);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean removeMeeting(int roomId) {
        Meeting meeting = runningMeetings.get(roomId);
        if (meeting != null) {
            for(long partnerId : meeting.getPartnerIds()) {
                participating.remove(partnerId);
                inRoom.remove(partnerId);
            }
            runningMeetings.remove(roomId);
            roomIds.remove((Integer)roomId);
            return true;
        } else {
            return false;
        }
    }

    public boolean enterRoom(long partnerId) {
        if (isJoinedMeeting(partnerId)) {
            inRoom.remove(partnerId);
            inRoom.put(partnerId, true);
            return true;
        } else {
            return false;
        }
    }

    public boolean leaveRoom(long partnerId) {
        if (isJoinedMeeting(partnerId)) {
            inRoom.remove(partnerId);
            inRoom.put(partnerId, false);
            return true;
        } else {
            return false;
        }
    }

    public int generateRoomId() {
        int roomId;
        do {
            roomId = (int)((Math.random() * 9 + 1) * 100000);
        } while (isExistRoomId(roomId));
        return roomId;
    }

    public void startMeeting(int roomId) {
        Meeting meeting = getMeeting(roomId);
        if (meeting != null && !meeting.isStarted()) {
            meeting.setStarted(true);
        }
    }

    public boolean isJoinedMeeting(long userId) {
        return participating.get(userId) != null;
    }

    public boolean isExistMeeting(int roomId) {
        return runningMeetings.get(roomId) != null;
    }

    public boolean isInRoom(long partnerId) {
        return inRoom.get(partnerId);
    }

    public boolean isPartnerNumberReachedMax(int roomId) {
        Meeting meeting = runningMeetings.get(roomId);
        if (meeting == null) {
            return true;
        } else  {
            if (meeting.getPartnerNumber() < meeting.getMaxPartnerNumber()) {
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean isExistRoomId(int roomId) {
        for (int id : roomIds) {
            if (id == roomId) {
                return true;
            }
        }
        return false;
    }

    public int getRoomId(long userId) {
       Integer roomId = participating.get(userId);
       if (roomId != null) {
           return roomId;
       } else {
           return MeetingService.DEFAULT_ROOM_ID;
       }
    }

}
