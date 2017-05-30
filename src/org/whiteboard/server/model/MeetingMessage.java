package org.whiteboard.server.model;

import java.io.Serializable;

/**
 * Created by 李浩然 on 2017/5/27.
 */
public class MeetingMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;

    private String message;

    private String userId;

    private String roomId;

    private String boardContent;

    public MeetingMessage(String code, String message, String userId, String roomId, String boardContent) {
        this.code = code;
        this.message = message;
        this.userId = userId;
        this.roomId = roomId;
        this.boardContent = boardContent;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getBoardContent() {
        return boardContent;
    }

    public void setBoardContent(String boardContent) {
        this.boardContent = boardContent;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
