package com.lei.controller;

import com.lei.async.EventModel;
import com.lei.async.EventProducer;
import com.lei.async.EventType;
import com.lei.constant.KeyConstant;
import com.lei.model.Comment;
import com.lei.model.HostHolder;
import com.lei.model.User;
import com.lei.service.CommentService;
import com.lei.service.LikeService;
import com.lei.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by John on 2017/5/24.
 */
@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private CommentService commentService;

    /**
     * 通过评论id进行点赞
     *
     * @param commentId
     * @return
     */
    @RequestMapping("/like")
    @ResponseBody
    public String like(@RequestParam("commentId") String commentId) {
        User user = hostHolder.get();
        if (user == null) {
            return WendaUtil.getJSONString(999);
        }

        long likeCount = likeService.like(user.getId(), KeyConstant.ENTITY_TYPE_COMMENT, commentId);
        // 当用户点赞某条评论时，发送给此条评论的用户
        // 需要查找当前评论所属者
        Comment comment = commentService.selectByCommentId(commentId);
        EventModel eventModel = new EventModel(EventType.LIKE);
        eventModel.setActorId(user.getId()).setEntityId(commentId).setEntityType(KeyConstant.ENTITY_TYPE_COMMENT).setEntityOwnerId(comment.getUserId()).setExt("questionId", comment.getEntityId());
        eventProducer.fireEvent(eventModel);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }

    /**
     * 通过评论id进行踩
     *
     * @param commentId
     * @return
     */
    @RequestMapping("/dislike")
    @ResponseBody
    public String disLike(@RequestParam("commentId") String commentId) {
        User user = hostHolder.get();
        if (user == null) {
            return WendaUtil.getJSONString(999);
        }
        long likeCount = likeService.disLike(user.getId(), KeyConstant.ENTITY_TYPE_COMMENT, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
