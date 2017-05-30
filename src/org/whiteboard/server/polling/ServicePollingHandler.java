package org.whiteboard.server.polling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.async.DeferredResult;
import org.whiteboard.server.model.MeetingMessage;

import java.util.Map;

/**
 * Created by 李浩然 on 2017/5/27.
 */
public class ServicePollingHandler {
    @Autowired
    MessageContainer messageContainer;

    public void pollingHandle(MeetingMessage meetingMessage) {
        Map<String, DeferredResult<MeetingMessage>> msgContainer = messageContainer.getUserMessages();
        DeferredResult<MeetingMessage> deferredResult = msgContainer.get(meetingMessage.getRoomId());
        if(deferredResult != null) {
            deferredResult.setResult(meetingMessage);   // 调用setResult，线程返回消息
        }
    }
}
