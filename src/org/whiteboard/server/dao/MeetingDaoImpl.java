package org.whiteboard.server.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.whiteboard.server.model.Meeting;

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
        List<Long> meetingIds = null;
        String sql = getSqlForSelectMeetingIds(UserDao.COL_USER_ID);
        try {
            RowMapper<Long> rowMapper = BeanPropertyRowMapper.newInstance(Long.class);
            meetingIds = jdbcTemplate.query(sql, new Object[]{ userId }, rowMapper);
        } catch (Exception e) {
            // do nothing
        }
        if(meetingIds == null) {
            meetingIds = new ArrayList<>();
        }
        return meetingIds;
    }

    @Override
    public List<Long> findUserIdsByMeetingId(long meetingId) {
        List<Long> userIds = null;
        String sql = getSqlForSelectUserIds(COL_MEETING_ID);
        try {
            RowMapper<Long> rowMapper = BeanPropertyRowMapper.newInstance(Long.class);
            userIds = jdbcTemplate.query(sql, new Object[]{ meetingId }, rowMapper);
        } catch (Exception e) {
            // do nothing
        }
        if(userIds == null) {
            userIds = new ArrayList<>();
        }
        return userIds;
    }

    @Override
    public Meeting addMeetingToDB(Meeting meeting) {
        Meeting tMeeting = null;
        if(jdbcTemplate.update(getSqlForInsertMeeting(),
                getParamsForInsertMeeting(meeting)) == 1) {
            tMeeting = jdbcTemplate.queryForObject(getSqlForSelectMeeting(COL_ORGANIZER_ID, COL_START_TIME),
                    new Object[]{ meeting.getOrganizerId(), dateFormat.format(meeting.getStartTime()) },
                    BeanPropertyRowMapper.newInstance(Meeting.class));
            List<Object[]> batchArgs = new ArrayList<>();
            List<Long> partnerIds = meeting.getPartnerIds();
            for (long userId : partnerIds) {
                batchArgs.add(new Object[] { userId, meeting.getMeetingId() });
            }
            if (batchArgs.size() == meeting.getPartnerNumber()) {
                jdbcTemplate.batchUpdate(getSqlForInsertParticipate(), batchArgs);
                tMeeting = findMeetingById(tMeeting.getMeetingId());
            } else {
                jdbcTemplate.update(getSqlForDeleteMeeting(COL_MEETING_ID), meeting.getMeetingId());
                tMeeting = null;
            }
        }
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
                meeting.getNotePath() + meeting.getMeetingRoomId() };
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
