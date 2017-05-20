package org.whiteboard.server.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.whiteboard.server.model.Meeting;

import java.util.List;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Repository
public class MeetingDaoImpl implements MeetingDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Meeting findMeetingById(long meetingId) {
        Meeting meeting = null;
        String sql = getSqlForSelectMeeting(COL_MEETING_ID);
        return meeting;
    }

    @Override
    public List<Long> findMeetingIdsByUserId(long userId) {
        return null;
    }

    @Override
    public List<Long> findUserIdsByMeetingId(long meetingId) {
        return null;
    }

    @Override
    public void addMeetingToDB(Meeting meeting) {

    }

    @Override
    public void updateMeeting(Meeting meeting) {

    }

    private String getSqlForSelectMeeting(String col) {
        return "select " + "*" + " from " + TABLE_MEETING + " where "
                + col + "=?";
    }
}
