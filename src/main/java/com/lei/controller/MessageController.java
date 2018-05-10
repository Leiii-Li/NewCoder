package com.lei.controller;

import com.lei.dao.ViewObject;
import com.lei.model.HostHolder;
import com.lei.model.Message;
import com.lei.model.User;
import com.lei.service.MessageService;
import com.lei.service.UserService;
import com.lei.util.FilterContent;
import com.lei.util.LoggerUtil;
import com.lei.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;

/**
 * Created by John on 2017/5/23.
 */
@Controller
@RequestMapping("/msg")
public class MessageController {

    @Autowired
    private FilterContent filterContent;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;


    @RequestMapping("/addMessage")
    @ResponseBody
    public String sendMessage(String toName, String content) {
        User user = hostHolder.get();
        if (user == null) {
            return WendaUtil.getJSONString(1, "用户未登录");
        }
        // 通过用户名查询用户信息
        User toUser = userService.selectByName(toName);
        if (toUser == null) {
            return WendaUtil.getJSONString(1, "没有该用户");
        }
        content = filterContent.filter(content);
        messageService.addMessage(toUser, toName, content, user);
        // 发送私信成功
        String jsonString = WendaUtil.getJSONString(0);
        int i = 0;
        return jsonString;
    }

    /**
     * 私信列表
     */
    @RequestMapping("/list")
    public String toMsgList(Model model) {
        User user = hostHolder.get();
        if (user == null) { //如果用户没有登录无法进入私信列表界面
            return "redirect:/reglogin";
        }
        List<Message> messageList = messageService.selectConversationList(user.getId(), 0, 10);
        List<ViewObject> messages = new ArrayList<>();
        for (Message message : messageList) {
            LoggerUtil.Logger(message.toString());
            // 通过Message查询formId 的用户信息
            ViewObject viewObject = new ViewObject();
            viewObject.put("conversation", message);
            viewObject.put("user", userService.selectByUserId(message.getFormid()));
            // 通过两个用户的conversationId 查询所有的message 且hasRead为0的记录数
            viewObject.put("unread", messageService.selectUnreadConversationCount(message.getConversationId()));
            messages.add(viewObject);
        }
        model.addAttribute("conversations", messages);
        return "/letter";
    }

    @RequestMapping("/detail/{conversationId}")
    public String toMsgDetail(Model model, @PathVariable(value = "conversationId") String conversationId) {
        User user = hostHolder.get();
        if (user == null) {
            return "redirect:/reglogin";
        }
        // 进入会话详情页后，需要将当前conversationId的message的has_red 修改为1
        List<Message> messageList = messageService.selectByConversationId(conversationId);
        List<ViewObject> messages = new ArrayList<>();
        for (Message message : messageList) {
            ViewObject viewObject = new ViewObject();
            viewObject.put("message", message);
            viewObject.put("user", userService.selectByUserId(message.getFormid()));
            messages.add(viewObject);
        }
        model.addAttribute("messages", messages);
        return "/letterDetail";
    }
}
