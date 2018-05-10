package com.lei.async.handler;

import com.lei.async.EventHandler;
import com.lei.async.EventModel;
import com.lei.async.EventType;
import com.lei.constant.KeyConstant;
import com.lei.model.Message;
import com.lei.model.Question;
import com.lei.model.User;
import com.lei.service.MessageService;
import com.lei.service.QuestionService;
import com.lei.service.UserService;
import com.lei.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by John on 2017/6/1.
 */
@Component
public class FollowHandler implements EventHandler {

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private MessageService messageService;

    @Override
    public void doHandler(EventModel model) {
        if (EventType.FOLLOW_USER.equals(model.getType())) { // 关注用户
            User actorUser = userService.selectByUserId(model.getActorId());
            User entityUser = userService.selectByUserId(model.getEntityId());

            Message message = new Message();
            message.setContent("您被" + actorUser.getName() + "关注咯");
            message.setId(UuidUtil.get32UUID());
            message.setHasRead(0);
            message.setToid(entityUser.getId());
            message.setFormid(KeyConstant.SYSTEM_USER_ID);
            message.setCreatedDate(new Date());
            messageService.sendMessage(message);

        }
        if (EventType.FOLLOW_QUESTION.equals(model.getType())) { // 关注问题
            User actorUser = userService.selectByUserId(model.getActorId());
            Question question = questionService.selectById(model.getEntityId());
            Message message = new Message();
            message.setContent("您的帖子" + question.getTitle() + "被" + actorUser.getName() + "关注咯,http://127.0.0.1:8080/question/" + question.getId());
            message.setId(UuidUtil.get32UUID());
            message.setHasRead(0);
            message.setToid(userService.selectByUserId(question.getUserId()).getId());
            message.setFormid(KeyConstant.SYSTEM_USER_ID);
            message.setCreatedDate(new Date());
            messageService.sendMessage(message);
        }
    }

    @Override
    public List<EventType> getSupportTypes() {
        return Arrays.asList(EventType.FOLLOW_USER, EventType.FOLLOW_QUESTION);
    }
}
