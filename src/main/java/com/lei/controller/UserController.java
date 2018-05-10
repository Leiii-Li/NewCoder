package com.lei.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.lei.constant.KeyConstant;
import com.lei.dao.ViewObject;
import com.lei.model.HostHolder;
import com.lei.model.Question;
import com.lei.model.User;
import com.lei.service.*;
import com.lei.util.LoggerUtil;
import com.lei.util.PrintUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.View;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by John on 2017/5/21.
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginTicketService ticketService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    SearchService searchService;

    // 登录成功或注册成功需要存入一个Toekn ，当用户在Token过期时间内访问网站便不需要再次输入用户名密码进行登录
    @RequestMapping("/reg")
    public String register(Model model, String username, String rememberme, String password, HttpServletResponse response) {
        // 执行注册业务
        Map map = userService.register(username, password);
        if (!StringUtils.isEmpty(rememberme) && "true".equals(rememberme)) {
            addTicket2Cookie(response, map);
        }
        if (map.containsKey("msg")) {
            // 说明在注册过程中出现错误,返回注册界面并打印出错信息
            model.addAttribute("msg", map.get("msg"));
            return "/login";
        }
        return "redirect:/";
    }

    @RequestMapping("/login")
    public String login(Model model, String username, String rememberme, String password, HttpServletResponse response, String next, String questionId) {
        // 执行登录
        LoggerUtil.Logger("UserName : " + username + "  PassWord : " + password + " Remenberme : " + rememberme);
        Map map = userService.login(username, password);
        if (!StringUtils.isEmpty(rememberme) && "true".equals(rememberme)) {
            addTicket2Cookie(response, map);
        }
        if (map.containsKey("msg")) {
            // 说明在注册过程中出现错误,返回注册界面并打印出错信息
            model.addAttribute("msg", map.get("msg"));
            return "/login";
        }
        if (!StringUtils.isEmpty(next)) {
            return "redirect:" + next;
        }
        return "redirect:/";
    }

    public void addTicket2Cookie(HttpServletResponse response, Map map) {
        if (map.containsKey(KeyConstant.TICKET_KEY)) { // 设置ticket至浏览器
            String ticket = (String) map.get(KeyConstant.TICKET_KEY);
            Cookie cookie = new Cookie(KeyConstant.TICKET_KEY, ticket);
            cookie.setPath("/");
            cookie.setMaxAge(3600 * 24 * 7);
            response.addCookie(cookie);
        }
    }

    /**
     * 进入登录或注册界面
     *
     * @param model
     * @return
     */
    @RequestMapping("/reglogin")
    public String reg(Model model, String next) {
        model.addAttribute("next", next);
        return "/login";
    }

    /**
     * 登出
     */
    @RequestMapping("/logout")
    public String logout(@CookieValue(KeyConstant.TICKET_KEY) String ticket) {
        System.out.println("ticket:" + ticket);
        ticketService.logout(ticket);
        return "redirect:/";
    }

    /**
     * 进入人首页
     * 查询出所有关注的人
     *
     * @return
     */
    @RequestMapping("/user/{userId}")
    public String profile(Model model, @PathVariable("userId") String userId) {
        // 查询前10条
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        User user = userService.selectByUserId(userId);
        ViewObject vo = new ViewObject();
        vo.put("user", user);
        // 获取粉丝的人数
        vo.put("followerCount", followService.getFansCount(userId, KeyConstant.ENTITY_TYPE_USER));
        // 获取关注的人数
        vo.put("followeeCount", followService.getFolloweesCount(userId, KeyConstant.ENTITY_TYPE_USER));
        // 判断当前登录用户是否是当前查看用户的粉丝
        if (hostHolder.get() != null) {
            vo.put("followed", followService.isUserFollower(hostHolder.get(), KeyConstant.ENTITY_TYPE_USER, userId));
        } else {
            vo.put("followed", false);
        }
        // 如果自己查看自己则不显示关注跟取消关注
        if (null != hostHolder.get()) {
            // 如果Id相同视为自己查看自己
            if (hostHolder.get().getId().equals(userId)) {
                vo.put("isOwer", true);
            } else {
                vo.put("isOwer", false);
            }
        }
        model.addAttribute("profileUser", vo);
        return "/profile";
    }

    private List<ViewObject> getQuestions(String userId, int offset, int limit) {
        List<Question> questionList = questionService.selectLatesQuestionsByUserId(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.put("question", question);
            vo.put("followCount", followService.getFolloweesQuestionCount(question.getId(), KeyConstant.ENTITY_TYPE_FOLLOW_QUESTION));
            vo.put("user", userService.selectByUserId(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

    /**
     * 通过用户Id进入粉丝界面
     *
     * @param userId
     * @return
     */
    @RequestMapping("/user/{userId}/fans")
    public String toFansJsp(Model model, @PathVariable("userId") String userId) {
        // 查询所有的粉丝
        List<String> userIds = followService.getFans(userId, KeyConstant.ENTITY_TYPE_USER);
        if (hostHolder.get() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.get(), userIds));
        } else {
            model.addAttribute("followees", getUsersInfo(hostHolder.get(), userIds));
        }
        model.addAttribute("followeeCount", followService.getFansCount(userId, KeyConstant.ENTITY_TYPE_USER));
        model.addAttribute("curUser", userService.selectByUserId(userId));
        return "followees";
    }

    @RequestMapping("/user/toQuestionEdit/{questionId}")
    public String deleteQuestion(Model model, @PathVariable(value = "questionId") String questionId) {
        Question question = questionService.selectById(questionId);
        model.addAttribute("question", question);
        return "/questionedit";
    }

    @RequestMapping(path = "/user/updateQuestion", method = RequestMethod.POST)
    public String deleteQuestion(String id, String title, String content) throws IOException, SolrServerException {
        LoggerUtil.Logger("ID : " + id + "  Title : " + title);
        User user = hostHolder.get();
        questionService.updateQuestion(id, title, content);
        searchService.addQuestionIndex(id, title, content);
        return "redirect:/user/" + user.getId();
    }

    @RequestMapping("/user/questionDelete/{questionId}")
    public String deleteQuestion(@PathVariable(value = "questionId") String questionId) throws IOException, SolrServerException {
        LoggerUtil.Logger("Delete Question : " + questionId);
        User user = hostHolder.get();
        if (user != null) {
            questionService.deleteById(questionId);
            searchService.deleteQuestionIndex(questionId);
            return "redirect:/user/" + user.getId();
        }
        return "redirect:/";
    }

    /**
     * 通过UserId 进入 关注界面
     *
     * @param userId
     * @return
     */
    @RequestMapping("/user/{userId}/followers")
    public String toFolloweesJsp(Model model, @PathVariable("userId") String userId) {
        List<String> followerIds = followService.getAllFollower(userId, KeyConstant.ENTITY_TYPE_USER);
        if (hostHolder.get() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.get(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(hostHolder.get(), followerIds));
        }
        model.addAttribute("followerCount", followService.getFolloweesCount(userId, KeyConstant.ENTITY_TYPE_USER));
        model.addAttribute("curUser", userService.selectByUserId(userId));
        return "followers";
    }

    private List<ViewObject> getUsersInfo(User localUser, List<String> userIds) {
        // 后面userIds是所有粉丝id的集合
        // 前面的localUserId 是当前登录用户的Id
        List<ViewObject> userInfos = new ArrayList<ViewObject>();
        for (String uid : userIds) {
            User user = userService.selectByUserId(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.put("user", user);
            // 关注人
            vo.put("followeeCount", followService.getFolloweesCount(uid, KeyConstant.ENTITY_TYPE_USER));
            // 粉丝
            vo.put("followerCount", followService.getFansCount(uid, KeyConstant.ENTITY_TYPE_USER));
            vo.put("isOwner", true);
            if (localUser != null) {
                // 如果当前登录用户与当前遍历的用户的id相同那么视为一个用户
                if (localUser.getId().equals(uid)) {
                    vo.put("isOwner", false);
                }
                vo.put("followed", followService.isUserFollower(localUser, KeyConstant.ENTITY_TYPE_USER, uid));
            } else {
                vo.put("followed", false);
            }
            //  判断当前登录用户跟当前遍历的用户是否为同一用户
            userInfos.add(vo);
        }
        return userInfos;
    }


}
