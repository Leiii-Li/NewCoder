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
 * Created by John on 2018/4/17.
 */
@Component
public class AddCommentHandler implements EventHandler {
    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;
    @Autowired
    private MessageService messageService;

    @Override
    public void doHandler(EventModel model) {
        if (EventType.COMMENT.equals(model.getType())) {
            // 当有用户评论时，需要给该帖子的发帖者发送站内信
            String actorId = model.getActorId(); // 评论的用户
            String entityId = model.getEntityId(); // 给那个帖子的评论 帖子ID

            User addCommentUser = userService.selectByUserId(actorId);
            Question question = questionService.selectById(entityId);
            String questionUserId = question.getUserId();

            Message message = new Message();
            message.setId(UuidUtil.get32UUID());
            message.setFormid(KeyConstant.SYSTEM_USER_ID); // 设置为系统账号发送的站内信
            message.setToid(questionUserId);
            message.setContent("用户" + addCommentUser.getName()
                    + "评论了你发布的帖子,http://127.0.0.1:8080/question/" + entityId);
            message.setHasRead(0);
            message.setCreatedDate(new Date());
            messageService.sendMessage(message);
        }
        if (EventType.LIKE_COMMENT.equals(model.getType())) {
            // 当有用户评论时，需要给该帖子的发帖者发送站内信
            String actorId = model.getActorId(); // 评论的用户
            String entityId = model.getEntityId(); // 给那个帖子的评论 帖子ID

            User addCommentUser = userService.selectByUserId(actorId);
            Question question = questionService.selectById(entityId);
            String questionUserId = question.getUserId();
            if (actorId.equals(questionUserId)) {
                return;
            }
            if (model.getEntityOwnerId().equals(questionUserId)) {
                return;
            }
            Message message = new Message();
            message.setId(UuidUtil.get32UUID());
            message.setFormid(KeyConstant.SYSTEM_USER_ID); // 设置为系统账号发送的站内信
            message.setToid(questionUserId);
            message.setContent("用户" + addCommentUser.getName()
                    + "评论了你关注的帖子,http://127.0.0.1:8080/question/" + entityId);
            message.setHasRead(0);
            message.setCreatedDate(new Date());
            messageService.sendMessage(message);
        }
    }

    @Override
    public List<EventType> getSupportTypes() {
        return Arrays.asList(EventType.COMMENT, EventType.LIKE_COMMENT);
    }
}
