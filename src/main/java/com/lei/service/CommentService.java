package com.lei.service;

import com.lei.constant.KeyConstant;
import com.lei.dao.CommentMapper;
import com.lei.model.Comment;
import com.lei.model.CommentExample;
import com.lei.model.User;
import com.lei.util.FilterContent;
import com.lei.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by John on 2017/5/23.
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FilterContent filterContent;

    public void addComment(String questionId, String content, User user) {
        content = filterContent.filter(content);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setId(UuidUtil.get32UUID());
        comment.setCreateDate(new Date());
        comment.setEntityType(KeyConstant.ENTITY_TYPE_QUESTION);
        comment.setStatus(0);
        comment.setUserId(user.getId());
        comment.setEntityId(questionId);
        commentMapper.insert(comment);
        // 更新问题评论数
        questionService.updateQuestionCommentCount(questionId);
    }

    public List<Comment> selectByQuestionId(String questionId) {
        CommentExample example = new CommentExample();
        example.setOrderByClause("create_date desc");
        CommentExample.Criteria criteria = example.createCriteria();
        criteria.andEntityIdEqualTo(questionId);
        criteria.andStatusEqualTo(0);
        List<Comment> comments = commentMapper.selectByExampleWithBLOBs(example);
        if (comments == null) {
            comments = new ArrayList<>();
        }
        return comments;
    }

    public Comment selectByCommentId(String commentId) {
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        return comment;
    }
}
