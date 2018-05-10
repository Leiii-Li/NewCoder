package com.lei.service;

import com.lei.dao.MessageMapper;
import com.lei.model.HostHolder;
import com.lei.model.Message;
import com.lei.model.MessageExample;
import com.lei.model.User;
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
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private HostHolder hostHolder;

    public void addMessage(User toUser, String toName, String content, User user) {
        Message message = new Message();
        message.setCreatedDate(new Date());
        message.setId(UuidUtil.get32UUID());
        message.setContent(content);
        message.setHasRead(0);
        message.setFormid(user.getId());
        message.setToid(toUser.getId());
        messageMapper.insert(message);
    }

    public List<Message> selectByConversationId(String conversationId) {
        MessageExample example = new MessageExample();
        example.setOrderByClause("created_date");
        MessageExample.Criteria criteria = example.createCriteria();
        criteria.andConversationIdEqualTo(conversationId);
        List<Message> messages = messageMapper.selectByExample(example);
        // 更新MessageHasRead为已读
        messageMapper.updateMessageHasReadByConversationId(conversationId);
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }

    public List<Message> selectConversationList(String id, int offset, int limit) {
        List<Message> messageList = messageMapper.selectConversationList(id, offset, limit);
        if (messageList == null) {
            messageList = new ArrayList<>();
        }
        return messageList;
    }

    public int selectUnreadConversationCount(String conversationId) {
        MessageExample example = new MessageExample();
        MessageExample.Criteria criteria = example.createCriteria();
        criteria.andConversationIdEqualTo(conversationId);
        criteria.andHasReadEqualTo(0);
        List<Message> messages = messageMapper.selectByExample(example);
        if (messages == null) {
            return 0;
        }
        return messages.size();
    }

    public void sendMessage(Message message) {
        message.setConversationId(message.getConversationId());
        messageMapper.insert(message);
    }
}
