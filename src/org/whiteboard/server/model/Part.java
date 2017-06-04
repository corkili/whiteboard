package org.whiteboard.server.model;

import org.whiteboard.server.dao.UserDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import static org.whiteboard.server.dao.MeetingDao.COL_PART_ID;
import static org.whiteboard.server.dao.MeetingDao.TABLE_PARTICIPATE;
import static org.whiteboard.server.dao.WhiteboardDao.COL_MEETING_ID;

/**
 * Created by 李浩然 on 2017/6/4.
 */
@Entity
@Table(name = TABLE_PARTICIPATE)
public class Part {
    @Column(name = COL_PART_ID)
    private long partId;

    @Column(name = UserDao.COL_USER_ID)
    private long userId;

    @Column(name = COL_MEETING_ID)
    private long meetingId;

    public Part(){}

    public Part(long partId, long userId, long meetingId) {
        this.partId = partId;
        this.userId = userId;
        this.meetingId = meetingId;
    }

    public long getPartId() {
        return partId;
    }

    public void setPartId(long partId) {
        this.partId = partId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(long meetingId) {
        this.meetingId = meetingId;
    }
}

