package com.lei.controller;

import com.lei.async.EventConsumer;
import com.lei.async.EventModel;
import com.lei.async.EventProducer;
import com.lei.async.EventType;
import com.lei.constant.KeyConstant;
import com.lei.model.HostHolder;
import com.lei.model.Question;
import com.lei.model.User;
import com.lei.service.FollowService;
import com.lei.service.QuestionService;
import com.lei.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by John on 2017/5/26.
 */
@Controller
public class FollowController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping("/followUser")
    @ResponseBody
    public String followUser(String userId) {
        User user = hostHolder.get();
        // 想要进行关注必须要先登录
        if (user == null) {
            return WendaUtil.getJSONString(999); // 进入登录界面
        }
        boolean b = followService.followUser(user.getId(), KeyConstant.ENTITY_TYPE_USER, userId);
        //  返回当前用户粉丝的人数
        String fansCount = String.valueOf(followService.getFansCount(userId, KeyConstant.ENTITY_TYPE_USER));
        if (b) {
            fireUserFollowEvent(userId, user);
        }
        return WendaUtil.getJSONString(b ? 0 : 1, fansCount);
    }

    private void fireUserFollowEvent(String userId, User user) {
        EventModel eventModel = new EventModel(EventType.FOLLOW_USER);
        eventModel.setEntityId(userId);
        eventModel.setActorId(user.getId());
        eventProducer.fireEvent(eventModel);
    }

    /**
     * 取消关注
     *
     * @param userId
     * @return
     */
    @RequestMapping("/unfollowUser")
    @ResponseBody
    public String unfollowUser(String userId) {
        User user = hostHolder.get();
        // 想要进行关注必须要先登录
        if (user == null) {
            return WendaUtil.getJSONString(999); // 进入登录界面
        }
        boolean b = followService.unfollowUser(user.getId(), KeyConstant.ENTITY_TYPE_USER, userId);
        //  返回当前用户关注的人数
        String fansCount = String.valueOf(followService.getFansCount(userId, KeyConstant.ENTITY_TYPE_USER));
        return WendaUtil.getJSONString(b ? 0 : 1, fansCount);
    }

    /**
     * 关注问题服务
     *
     * @param questionId
     * @return
     */
    @RequestMapping("/followQuestion")
    @ResponseBody
    public String followQuestion(String questionId) {
        User user = hostHolder.get();
        // 想要进行关注必须要先登录
        if (user == null) {
            return WendaUtil.getJSONString(999); // 进入登录界面
        }
        boolean b = followService.followQuestion(questionId, KeyConstant.ENTITY_TYPE_FOLLOW_QUESTION, user.getId());

        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.get().getHeadUrl());
        info.put("name", hostHolder.get().getName());
        info.put("id", hostHolder.get().getId());
        info.put("count", followService.getFolloweesQuestionCount(questionId, KeyConstant.ENTITY_TYPE_FOLLOW_QUESTION));
        // 当问题被关注时向问题的发帖人发送站内信
        if (b) {
            fireFollowQuestionEvent(questionId, user);
        }
        return WendaUtil.getJSONString(b ? 0 : 1, info);
    }

    private void fireFollowQuestionEvent(String questionId, User user) {
        EventModel eventModel = new EventModel(EventType.FOLLOW_QUESTION);
        eventModel.setActorId(user.getId());
        eventModel.setEntityId(questionId);
        eventProducer.fireEvent(eventModel);
    }

    /**
     * 取消关注问题服务
     */
    @RequestMapping("/unfollowQuestion")
    @ResponseBody
    public String unfollowQuestion(String questionId) {
        User user = hostHolder.get();
        if (user == null) {
            return WendaUtil.getJSONString(999);
        }
        Question q = questionService.selectById(questionId);
        if (q == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }
        boolean b = followService.unfollowQuestion(questionId, KeyConstant.ENTITY_TYPE_FOLLOW_QUESTION, user);
        // 如果取消关注成功返回当前问题还有多少人关注
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("count", followService.getFolloweesQuestionCount(questionId, KeyConstant.ENTITY_TYPE_FOLLOW_QUESTION));
        return WendaUtil.getJSONString(b ? 0 : 1, info);
    }
}
