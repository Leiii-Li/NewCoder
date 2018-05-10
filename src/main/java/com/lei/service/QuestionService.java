package com.lei.service;

import com.lei.constant.KeyConstant;
import com.lei.dao.QuestionMapper;
import com.lei.model.*;
import com.lei.util.FilterContent;
import com.lei.util.UuidUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by John on 2017/5/20.
 */
@Service
public class QuestionService {
    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    SearchService searchService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FilterContent filterContent;

    public int insert(Question question) {
        int i = questionMapper.insert(question);
        return i;
    }

    public List<Question> selectLatesQuestions(int id, int offset, int limit) {
        List<Question> list = questionMapper.selectLatestQuestions(id, offset, 10);
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }


    public int addQuestion(String title, String content) throws IOException, SolrServerException {
        Question question = new Question();
        User user = hostHolder.get();
        if (user != null) {
            question.setUserId(user.getId());
        } else {
            question.setUserId(KeyConstant.ANIMOS_USER_ID);
        }
        question.setId(UuidUtil.get32UUID());
        question.setCreateDate(new Date());
        //************
        // html代码过滤
        question.setTitle(filterContent.filter(title));
        question.setContent(filterContent.filter(content));
        //************
        question.setComentCount(0);
        int i = questionMapper.insert(question);
        searchService.addQuestionIndex(question.getId(), question.getTitle(), question.getContent());
        return i;
    }

    public Question selectById(String questionId) {
        Question question = questionMapper.selectByPrimaryKey(questionId);
        return question;
    }

    public void updateQuestionCommentCount(String id) {
        questionMapper.updateQuestionCommentCount(id);
    }

    public List<Question> selectQuestionsByUserId(String userId) {
        QuestionExample example = new QuestionExample();
        QuestionExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<Question> questions = questionMapper.selectByExample(example);
        if (questions == null) {
            questions = new ArrayList<>();
        }
        return questions;
    }

    public List<Question> selectLatesQuestionsByUserId(String userId, int offset, int limit) {
        List<Question> questions = questionMapper.selectLatestQuestionsByUserId(userId, offset, limit);
        if (questions == null) {
            questions = new ArrayList<>();
        }
        return questions;
    }

    public void deleteById(String questionId) {
        questionMapper.deleteByPrimaryKey(questionId);
    }

    public void updateQuestion(String id, String title, String content) {
        Question question = new Question();
        question.setId(id);
        question.setTitle(title);
        question.setContent(content);
        questionMapper.updateByPrimaryKeySelective(question);
    }
}
