package org.whiteboard.server.dao;

/**
 * Created by 李浩然 on 2017/5/15.
 */
public interface MeetingDao {
    public static final String TABLE_MEETING = "table_meeting";
    public static final String COL_MEETING_ID = "meeting_id";
    public static final String COL_MEETING_NAME = "meeting_name";
    public static final String COL_PARTNER_NUMBER = "partner_number";
    public static final String COL_ORGANIZER_ID = "organizer_id";
    public static final String COL_START_TIME = "start_time";
    public static final String COL_END_TIME = "end_time";
    public static final String COL_NOTE_PATH = "note_path";
    public static final String COL_MEETING_ROOM_ID = "meeting_room_id";
}
