package com.lei.controller;

import com.lei.async.EventModel;
import com.lei.async.EventProducer;
import com.lei.async.EventType;
import com.lei.constant.KeyConstant;
import com.lei.dao.ViewObject;
import com.lei.model.*;
import com.lei.service.*;
import com.lei.util.LoggerUtil;
import com.lei.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 2017/5/22.
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping("/question/toAddQuestionPage")
    public String questionAdd() {
        return "addquestion";
    }

    @RequestMapping("/question/add")
    public String questionAdd(String title, String content) {
        LoggerUtil.Logger("Title  : " + title + "  Content : " + content);
        if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(content)) {
            try {
                int i = questionService.addQuestion(title, content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/";
    }

    @RequestMapping("/question/{questionId}")
    public String toQuestionDetail(Model model, @PathVariable("questionId") String questionId) {
        //通过问题Id查询所有的评论
        Question question = questionService.selectById(questionId);
        model.addAttribute("question", question);
        // 通过QuestionId 查询所有的评论  以及评论的用户信息
        List<Comment> comments = commentService.selectByQuestionId(questionId);
        List<ViewObject> viewObjs = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            ViewObject viewObject = new ViewObject();
            Comment comment = comments.get(i);
            viewObject.put("comment", comment);
            User user = userService.selectByUserId(comment.getUserId());
            viewObject.put("user", user);
            viewObjs.add(viewObject);
            //  这里需要进行判断，当前用户是否已经点击喜欢  或者踩
            if (hostHolder.get() == null) { // 当前没有用户登录
                viewObject.put("liked", 0);
            } else {
                viewObject.put("liked", likeService.getLikeStatus(hostHolder.get().getId(), KeyConstant.ENTITY_TYPE_COMMENT, comment.getId()));
            }
            // 取出当前评论所有点赞的数量
            viewObject.put("likeCount", likeService.getLikeCount(KeyConstant.ENTITY_TYPE_COMMENT, comment.getId()));
        }
        model.addAttribute("comments", viewObjs);
        // 进入问题详情，通过Redis查询当前问题所有关注的用户，并将用户信息展示在页面上
        List<String> userIds = followService.getFolloweesQuestions(questionId, KeyConstant.ENTITY_TYPE_FOLLOW_QUESTION);
        List<ViewObject> followUsers = new ArrayList<>();
        // 获取关注的用户信息
        for (String userId : userIds) {
            ViewObject vo = new ViewObject();
            User u = userService.get(userId);
            if (u == null) {
                continue;
            }
            vo.put("name", u.getName());
            vo.put("headUrl", u.getHeadUrl());
            vo.put("id", u.getId());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers", followUsers);
        if (hostHolder.get() != null) {
            model.addAttribute("followed", followService.isFollower(hostHolder.get().getId(), KeyConstant.ENTITY_TYPE_FOLLOW_QUESTION, questionId));
        } else {
            model.addAttribute("followed", false);
        }
        return "/detail";
    }

    /**
     * 添加评论帖子
     */
    @RequestMapping("/addComment")
    public String addComment(String questionId, String content) {
        try {
            User user = hostHolder.get();
            commentService.addComment(questionId, content, user);
            List<String> userIds = followService.getFolloweesQuestions(questionId, KeyConstant.ENTITY_TYPE_FOLLOW_QUESTION);
            //通知所有关注了帖子的用户
            fireQuestionAddCommentEvent(userIds, questionId, user);
            // 通知发布此帖子的用户
            fireQuestionAddCommentEvent(questionId, user);
        } catch (Exception e) {
            LoggerUtil.Logger("添加评论失败" + e);
        }
        return "redirect:question/" + questionId;
    }

    private void fireQuestionAddCommentEvent(List<String> userIds, String questionId, User user) {
        for (String userId : userIds) {
            EventModel eventModel = new EventModel(EventType.LIKE_COMMENT);
            eventModel.setActorId(user.getId());
            eventModel.setEntityId(questionId);
            eventModel.setEntityOwnerId(userId);
            eventProducer.fireEvent(eventModel);
        }
    }

    private void fireQuestionAddCommentEvent(String questionId, User user) {
        EventModel eventModel = new EventModel(EventType.COMMENT);
        eventModel.setActorId(user.getId());
        eventModel.setEntityId(questionId);
        eventProducer.fireEvent(eventModel);
    }
}
