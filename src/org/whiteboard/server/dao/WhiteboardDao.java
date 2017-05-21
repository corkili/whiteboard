package org.whiteboard.server.dao;

import org.whiteboard.server.model.Whiteboard;

import java.util.List;

/**
 * Created by 李浩然 on 2017/5/15.
 */
public interface WhiteboardDao {
    public static final String TABLE_WHITEBOARD = "table_whiteboard";
    public static final String COL_BOARD_ID = "board_id";
    public static final String COL_BOARD_CONTENT = "board_content";
    public static final String COL_MEETING_ID = "meeting_id";
    public static final String COL_BOARD_NAME = "board_name";

    // 查询
    public Whiteboard findBoardById(long boardId);
    public List<Whiteboard> findBoardsByMeetingId(long meetingId);

    // 添加
    public Whiteboard addBoardToDB(Whiteboard whiteboard);

    // 更新
    public Whiteboard updateBoard(Whiteboard whiteboard);

    // 删除
    public Whiteboard deleteBoard(Whiteboard whiteboard);
    public int deleteBoards(long meetingId);
}
