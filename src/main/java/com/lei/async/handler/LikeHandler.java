package com.lei.async.handler;

import com.lei.async.EventHandler;
import com.lei.async.EventModel;
import com.lei.async.EventType;
import com.lei.constant.KeyConstant;
import com.lei.model.Message;
import com.lei.model.User;
import com.lei.service.MessageService;
import com.lei.service.UserService;
import com.lei.util.UuidUtil;
import org.apache.commons.digester.annotations.rules.CallMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by John on 2017/5/25.
 * 该Handler为专门处理点赞事务的Handler
 */
@Component
public class LikeHandler implements EventHandler {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public void doHandler(EventModel model) {
        // 当有用户点赞时，需要给该评论的用户发送站内信  点赞的有
        String actorId = model.getActorId(); // 点赞的用户
        String entityId = model.getEntityId(); // 给那条评论点赞
        String entityOwnerId = model.getEntityOwnerId();// 当前评论时谁评论的
        String questionId = model.getExt("questionId"); //  当前评论属于那个问题
        if (actorId.equals(entityOwnerId)) {
            return;
        }
        User user = userService.selectByUserId(actorId);
        Message message = new Message();
        message.setId(UuidUtil.get32UUID());
        message.setFormid(KeyConstant.SYSTEM_USER_ID); // 设置为系统账号发送的站内信
        message.setToid(entityOwnerId);
        String url = "http://127.0.0.1:8080/question/" + questionId;
        message.setContent("用户" + user.getName()
                + "赞了你的评论,<a href=\"" + url + "\"</a>");
        message.setHasRead(0);
        message.setCreatedDate(new Date());

        messageService.sendMessage(message);
    }

    @Override
    public List<EventType> getSupportTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
