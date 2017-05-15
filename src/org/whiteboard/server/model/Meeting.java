package org.whiteboard.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Entity
@Table(name = "table_meeting")
public class Meeting {
    @Column(name = "meeting_id")
    private long meetingId;
    @Column(name = "meeting_name")
    private String meetingName;
    @Column(name = "partner_number")
    private int partnerNumber;
    @Column(name = "organizer_id")
    private long organizerId;
    @Column(name = "start_time")
    private Date startTime;
    @Column(name = "end_time")
    private Date endTime;
    @Column(name = "note_path")
    private String notePath;
    @Column(name = "meeting_room_id")
    private int MeetingRoomId;

    public Meeting() {

    }

    public Meeting(long meetingId, String meetingName, int partnerNumber, long organizerId, Date startTime, Date endTime, String notePath, int meetingRoomId) {
        this.meetingId = meetingId;
        this.meetingName = meetingName;
        this.partnerNumber = partnerNumber;
        this.organizerId = organizerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.notePath = notePath;
        MeetingRoomId = meetingRoomId;
    }

    public long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(long meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public int getPartnerNumber() {
        return partnerNumber;
    }

    public void setPartnerNumber(int partnerNumber) {
        this.partnerNumber = partnerNumber;
    }

    public long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(long organizerId) {
        this.organizerId = organizerId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getNotePath() {
        return notePath;
    }

    public void setNotePath(String notePath) {
        this.notePath = notePath;
    }

    public int getMeetingRoomId() {
        return MeetingRoomId;
    }

    public void setMeetingRoomId(int meetingRoomId) {
        MeetingRoomId = meetingRoomId;
    }
}
