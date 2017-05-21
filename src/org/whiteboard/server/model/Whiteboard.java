package org.whiteboard.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Entity
@Table(name = "table_whiteboard")
public class Whiteboard {
    @Column(name = "board_id")
    private long boardId;
    @Column(name = "board_content")
    private String boardContent;
    @Column(name = "meeting_id")
    private long meetingId;
    @Column(name = "board_name")
    private String boardName;

    public Whiteboard(long boardId, String boardContent, long meetingId, String boardName) {
        this.boardId = boardId;
        this.boardContent = boardContent;
        this.meetingId = meetingId;
        this.boardName = boardName;
    }

    public long getBoardId() {
        return boardId;
    }

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }

    public String getBoardContent() {
        return boardContent;
    }

    public void setBoardContent(String boardContent) {
        this.boardContent = boardContent;
    }

    public long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(long meetingId) {
        this.meetingId = meetingId;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }
}
