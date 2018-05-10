package com.lei.controller;

import com.lei.async.EventModel;
import com.lei.async.EventProducer;
import com.lei.async.EventType;
import com.lei.constant.KeyConstant;
import com.lei.dao.ViewObject;
import com.lei.model.Comment;
import com.lei.model.HostHolder;
import com.lei.model.Question;
import com.lei.model.User;
import com.lei.service.*;
import com.lei.util.LoggerUtil;
import com.lei.util.WendaUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 2017/5/22.
 */
@Controller
public class SearchController {

    @Autowired
    SearchService searchService;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @RequestMapping(path = "/search")
    public String addComment(Model model, @RequestParam("q") String keyWord, @RequestParam(value = "offset", defaultValue = "offset=0") String offset) throws IOException, SolrServerException {
        int count = Integer.valueOf(offset.substring(offset.lastIndexOf("=") + 1, offset.length()));
        LoggerUtil.Logger("Q : " + keyWord + "  offset : " + count);
        List<Question> questions = searchService.searchQuestion(keyWord, count, 10, "<em>", "</em>");
        List<ViewObject> objects = new ArrayList<>();
        for (Question question : questions) {
            ViewObject viewObject = new ViewObject();
            Question q = questionService.selectById(question.getId());
            if (q == null) {
                continue;
            }
            viewObject.put("question", q);
            viewObject.put("user", userService.get(q.getUserId()));
            viewObject.put("followCount", followService.getFolloweesQuestionCount(q.getId(), KeyConstant.ENTITY_TYPE_FOLLOW_QUESTION));
            objects.add(viewObject);
        }
        model.addAttribute("viewObjects", objects);
        model.addAttribute("q",keyWord);
        return "result";
    }
}
