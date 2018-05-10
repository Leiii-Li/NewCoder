package com.lei.controller;

import com.lei.constant.KeyConstant;
import com.lei.dao.ViewObject;
import com.lei.model.Question;
import com.lei.model.User;
import com.lei.service.FollowService;
import com.lei.service.QuestionService;
import com.lei.service.UserService;
import com.lei.util.LoggerUtil;
import com.lei.util.UuidUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by John on 2017/5/18.
 */
@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @RequestMapping(path = {"/", "/index/"})
    public String index(Model model, @RequestParam(value = "limit", defaultValue = "0") String limit) {
        List<Question> questions = questionService.selectLatesQuestions(0, Integer.valueOf(limit), 10);
        List<ViewObject> objects = new ArrayList<>();
        for (Question question : questions) {
            ViewObject viewObject = new ViewObject();
            viewObject.put("question", question);
            viewObject.put("user", userService.get(question.getUserId()));
            viewObject.put("followCount", followService.getFolloweesQuestionCount(question.getId(), KeyConstant.ENTITY_TYPE_FOLLOW_QUESTION));
            objects.add(viewObject);
        }
        model.addAttribute("viewObjects", objects);
        model.addAttribute("limit", limit);
        return "index";
    }

    @RequestMapping(path = {"/{limit}", "/index/{limit}"})
    public String indexLimnit(Model model, @PathVariable(value = "limit") String offset) {
        LoggerUtil.Logger("Offset : " + offset);
        List<Question> questions = questionService.selectLatesQuestions(0, Integer.valueOf(offset), 10);
        List<ViewObject> objects = new ArrayList<>();
        for (Question question : questions) {
            ViewObject viewObject = new ViewObject();
            viewObject.put("question", question);
            viewObject.put("user", userService.get(question.getUserId()));
            viewObject.put("followCount", followService.getFolloweesQuestionCount(question.getId(), KeyConstant.ENTITY_TYPE_FOLLOW_QUESTION));
            objects.add(viewObject);
        }
        model.addAttribute("viewObjects", objects);
        model.addAttribute("limit", offset);
        return "index";
    }

    @RequestMapping("/initData")
    @ResponseBody
    public String initData() {
        List<User> userList = userService.findAll();
        for (int i = 0; i < 10; i++) {
            Question question = new Question();
            question.setComentCount(i + 1 * 4);
            question.setContent("Balalalalalalalal");
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 3600 * i);
            question.setCreateDate(date);
            question.setTitle("问题" + i);
            question.setId(UuidUtil.get32UUID());
            question.setUserId(userList.get(i % userList.size()).getId());
            questionService.insert(question);
        }
        return "数据初始化成功";
    }
}
