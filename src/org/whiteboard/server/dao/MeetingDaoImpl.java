package org.whiteboard.server.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.whiteboard.server.model.Meeting;
import org.whiteboard.server.model.Part;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Repository
public class MeetingDaoImpl implements MeetingDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Meeting findMeetingById(long meetingId) {
        Meeting meeting = null;
        String sql = getSqlForSelectMeeting(COL_MEETING_ID);
        try {
            RowMapper<Meeting> rowMapper = BeanPropertyRowMapper.newInstance(Meeting.class);
            meeting = jdbcTemplate.queryForObject(sql, new Object[]{ meetingId }, rowMapper);
            if(meeting != null) {
                List<Long> partnerIds = findUserIdsByMeetingId(meetingId);
                if (partnerIds.size() == meeting.getPartnerNumber()) {
                    meeting.setPartnerIds(partnerIds);
                } else {    // 信息不匹配
                    meeting = null;
                }
            }
        } catch (Exception e) {
            // do nothing
        }
        return meeting;
    }

    @Override
    public Meeting findMeetingByOrganizerAndStartTime(long organizerId, Date startTime) {
        Meeting meeting = null;
        String sql = getSqlForSelectMeeting(COL_ORGANIZER_ID, COL_START_TIME);
        try {
            RowMapper<Meeting> rowMapper = BeanPropertyRowMapper.newInstance(Meeting.class);
            meeting = jdbcTemplate.queryForObject(sql, new Object[]{ organizerId, dateFormat.format(startTime) }, rowMapper);
            if(meeting != null) {
                List<Long> partnerIds = findUserIdsByMeetingId(meeting.getMeetingId());
                if (partnerIds.size() == meeting.getPartnerNumber()) {
                    meeting.setPartnerIds(partnerIds);
                } else {    // 信息不匹配
                    meeting = null;
                }
            }
        } catch (Exception e) {
            // do nothing
        }
        return meeting;
    }


    @Override
    public List<Long> findMeetingIdsByUserId(long userId) {
        List<Part> parts = null;
        List<Long> meetingIds = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_PARTICIPATE + " WHERE " + UserDao.COL_USER_ID + "=?";
        try {
            RowMapper<Part> rowMapper = BeanPropertyRowMapper.newInstance(Part.class);
            parts = jdbcTemplate.query(sql, new Object[]{ userId }, rowMapper);
        } catch (Exception e) {
            // do nothing
        }
        if (parts != null) {
            for (Part part : parts) {
                meetingIds.add(part.getMeetingId());
            }
        }
        return meetingIds;
    }

    @Override
    public List<Long> findUserIdsByMeetingId(long meetingId) {
        List<Long> userIds = new ArrayList<>();
        List<Part> parts = null;
        String sql = "SELECT * FROM " + TABLE_PARTICIPATE + " WHERE " + COL_MEETING_ID + "=?";
        try {
            RowMapper<Part> rowMapper = BeanPropertyRowMapper.newInstance(Part.class);
            parts = jdbcTemplate.query(sql, new Object[]{ meetingId }, rowMapper);
        } catch (Exception e) {
            // e.printStackTrace();
        }

        if (parts != null) {
            for (Part part : parts) {
                userIds.add(part.getUserId());
            }
        }
        return userIds;
    }


    @Override
    public Meeting addMeetingToDB(Meeting meeting) {
        Meeting tMeeting = null;
        System.out.println("sql: " + getSqlForInsertMeeting());
        jdbcTemplate.update(getSqlForInsertMeeting(), getParamsForInsertMeeting(meeting));
        RowMapper<Meeting> rowMapper = BeanPropertyRowMapper.newInstance(Meeting.class);
        List<Meeting> meetings  = jdbcTemplate.query(getSqlForSelectMeeting(COL_ORGANIZER_ID),
                new Object[] { meeting.getOrganizerId() }, rowMapper);
        tMeeting = meetings.get(0);
        for (Meeting m : meetings) {
            if (tMeeting.getMeetingId() < m.getMeetingId()) {
                tMeeting = m;
            }
        }
        System.out.println(meetings.size() + " " + tMeeting.getMeetingId());
        List<Long> partnerIds = meeting.getPartnerIds();
        String sql = getSqlForInsertParticipate();
        System.out.println(getSqlForInsertParticipate());
        for (long userId : partnerIds) {
            jdbcTemplate.update(sql, userId, tMeeting.getMeetingId());
        }
        tMeeting = findMeetingById(tMeeting.getMeetingId());
//        jdbcTemplate.update(getSqlForDeleteMeeting(COL_MEETING_ID), meeting.getMeetingId());
//        tMeeting = null;
        return tMeeting;
    }

    @Override
    public void updateMeeting(Meeting meeting) {
        if (meeting == null) {
            return;
        }
        long meetingId = meeting.getMeetingId();
        Meeting oldMeeting = findMeetingById(meetingId);
        if (oldMeeting != null) {
            jdbcTemplate.update(getSqlForUpdateMeeting(), getParamsForUpdateMeeting(meeting));
            if (oldMeeting.getPartnerNumber() != meeting.getPartnerNumber()) {
                List<Long> olds = oldMeeting.getPartnerIds();
                List<Long> news = meeting.getPartnerIds();
                for (long partnerId : news) {
                    if(!isListContainsId(olds, partnerId)) {
                        jdbcTemplate.update(getSqlForInsertParticipate(), new Object[]{ partnerId, meetingId });
                    }
                }
                for(long partnerId : olds) {
                    if (!isListContainsId(news, partnerId)) {
                        jdbcTemplate.update(getSqlForDeleteParticipate(UserDao.COL_USER_ID, COL_MEETING_ID),
                                new Object[]{ partnerId, meetingId });
                    }
                }
            }
        }
    }

    private String getSqlForSelectMeeting(String col) {
        return "select " + "*" + " from " + TABLE_MEETING + " where "
                + col + "=?";
    }

    private String getSqlForSelectMeeting(String col1, String col2) {
        return "select " + "*" + " from " + TABLE_MEETING + " where "
                + col1 + "=? and " + col2 + "=?";
    }

    private String getSqlForSelectMeetingIds(String col) {
        return "select " + COL_MEETING_ID + " from " + TABLE_PARTICIPATE + " where "
                + col + "=?";
    }

    private String getSqlForSelectUserIds(String col) {
        return "select " + UserDao.COL_USER_ID + " from " + TABLE_PARTICIPATE
                + " where " + col + "=?";
    }

    private String getSqlForInsertMeeting() {
        return "insert into " + TABLE_MEETING + " (" + COL_MEETING_NAME + ','
                + COL_PARTNER_NUMBER + ',' + COL_ORGANIZER_ID + ',' + COL_START_TIME
                + ',' + COL_END_TIME + ',' + COL_NOTE_PATH + ',' + COL_MEETING_ROOM_ID
                + ") values(?,?,?,?,?,?,?)";
    }

    private Object[] getParamsForInsertMeeting(Meeting meeting) {
        return new Object[] { meeting.getMeetingName(), meeting.getPartnerNumber(),
                meeting.getOrganizerId(), meeting.getStartTime(), meeting.getEndTime(),
                meeting.getNotePath(), meeting.getMeetingRoomId() };
    }

    private String getSqlForInsertParticipate() {
        return "insert into " + TABLE_PARTICIPATE + " (" + UserDao.COL_USER_ID
                + ',' + COL_MEETING_ID + ") values(?,?)";
    }

    private String getSqlForDeleteMeeting(String col) {
        return "delete from " + TABLE_MEETING + " where " + col + "=?";
    }

    private String getSqlForDeleteParticipate(String col1, String col2) {
        return "delete from " + TABLE_PARTICIPATE + " where " + col1
                + "=? and " + col2 + "=?";
    }

    private String getSqlForUpdateMeeting() {
        return "update " + TABLE_MEETING + " set " + COL_MEETING_NAME + "=?,"
                + COL_PARTNER_NUMBER + "=?," + COL_ORGANIZER_ID + "=?," + COL_START_TIME
                + "=?," + COL_END_TIME + "=?," + COL_NOTE_PATH + "=?," + COL_MEETING_ROOM_ID
                + "=? where " + COL_MEETING_ID + "=?";
    }

    private Object[] getParamsForUpdateMeeting(Meeting meeting) {
        return new Object[] { meeting.getMeetingName(), meeting.getPartnerNumber(),
                meeting.getOrganizerId(), meeting.getStartTime(), meeting.getEndTime(),
                meeting.getNotePath() + meeting.getMeetingRoomId(), meeting.getMeetingId() };
    }

    private boolean isListContainsId(List<Long> list, long id) {
        for(long testId : list) {
            if(id == testId) {
                return true;
            }
        }
        return false;
    }
}
