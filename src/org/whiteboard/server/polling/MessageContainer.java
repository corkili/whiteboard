package org.whiteboard.server.polling;

import org.springframework.web.context.request.async.DeferredResult;
import org.whiteboard.server.model.MeetingMessage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 李浩然 on 2017/5/27.
 */
public class MessageContainer {

    private ConcurrentHashMap<String, DeferredResult<MeetingMessage>> userMessages = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String ,DeferredResult<MeetingMessage>> getUserMessages() {
        return userMessages;
    }
}
