package com.lei.service;

import com.lei.dao.LoginTicketMapper;
import com.lei.model.LoginTicket;
import com.lei.model.LoginTicketExample;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by John on 2017/5/21.
 */
@Service
public class LoginTicketService {

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public LoginTicket findByTicket(String ticket) {
        LoginTicketExample example = new LoginTicketExample();
        LoginTicketExample.Criteria criteria = example.createCriteria();
        criteria.andTicketEqualTo(ticket);
        List<LoginTicket> tickets = loginTicketMapper.selectByExample(example);
        if (null != tickets && tickets.size() > 0) {
            return tickets.get(0);
        }
        return null;
    }

    public void insert(LoginTicket loginTicket) {
        loginTicketMapper.insert(loginTicket);
    }

    public void logout(String ticket) {
        if (!StringUtils.isEmpty(ticket)) {
            // 通过ticke删除登录或注册的记录
            LoginTicketExample example = new LoginTicketExample();
            LoginTicketExample.Criteria criteria = example.createCriteria();
            criteria.andTicketEqualTo(ticket);
            loginTicketMapper.deleteByExample(example);
        }
    }

    public void updateTicket(LoginTicket loginTicket) {
        loginTicketMapper.updateByPrimaryKeySelective(loginTicket);
    }
}
