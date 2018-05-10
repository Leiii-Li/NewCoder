package com.lei.service;

import com.lei.constant.KeyConstant;
import com.lei.dao.UserMapper;
import com.lei.model.LoginTicket;
import com.lei.model.User;
import com.lei.model.UserExample;
import com.lei.util.UuidUtil;
import com.lei.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by John on 2017/5/20.
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketService loginTicketService;

    public List<User> findAll() {
        UserExample example = new UserExample();
        List<User> userList = userMapper.selectByExample(example);
        if (null == userList) {
            return new ArrayList<>();
        }
        return userList;
    }

    public User get(String id) {
        return userMapper.selectByPrimaryKey(id);
    }

    public User selectByName(String username) {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(username);
        List<User> userList = userMapper.selectByExample(example);
        if (null == userList || userList.size() == 0) {
            return null;
        }
        return userList.get(0);
    }

    public Map register(String username, String password) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            map.put("msg", "用户名或密码为空");
            return map;
        }
        User user = this.selectByName(username);
        if (user != null) {
            map.put("msg", "用户名已经被注册");
            return map;
        }
        try {
            user = new User();
            user.setId(UuidUtil.get32UUID());
            user.setSalt(UUID.randomUUID().toString().substring(0, 5));
            user.setName(username);
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
            user.setPassword(WendaUtil.MD5(password + user.getSalt()));
            userMapper.insert(user);
            // 生成Ticket返回至Controller
            LoginTicket loginTicket = new LoginTicket();
            Date now = new Date();
            now.setTime(now.getTime() + 3600 * 24 * 7); //将过期时间推后7天，7天内访问网站不需要输入用户密码
            loginTicket.setExpired(now);
            loginTicket.setStatus(0);
            loginTicket.setUserId(user.getId());
            loginTicket.setTicket(UuidUtil.get32UUID()); // 生成随机ticket

            // 插入数据库
            loginTicketService.insert(loginTicket);
            map.put(KeyConstant.TICKET_KEY, loginTicket.getTicket());
        } catch (Exception e) {
            map.put("msg", "注册异常");
            e.printStackTrace();
        }
        return map;
    }

    public Map login(String username, String password) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            map.put("msg", "用户名或密码为空");
            return map;
        }
        // 通过username 获取用户
        User dbUser = selectByName(username);
        if (null == dbUser) {
            map.put("msg", "用户名不存在");
            return map;
        }
        String md5Password = WendaUtil.MD5(password + dbUser.getSalt());
        if (!dbUser.getPassword().equals(md5Password)) { // 如果密码通过加密后跟数据库中的密码一致则登录
            map.put("msg", "用户名密码密码不一致");
            return map;
        }
        // 生成Ticket返回至Controller
        LoginTicket loginTicket = new LoginTicket();
        Date now = new Date();
        now.setTime(now.getTime() + 3600 * 24 * 7); //将过期时间推后7天，7天内访问网站不需要输入用户密码
        loginTicket.setExpired(now);
        loginTicket.setStatus(0);
        loginTicket.setUserId(dbUser.getId());
        loginTicket.setTicket(UuidUtil.get32UUID()); // 生成随机ticket
        // 插入数据库
        loginTicketService.insert(loginTicket);
        map.put(KeyConstant.TICKET_KEY, loginTicket.getTicket());
        return map;
    }

    public User selectByUserId(String userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        return user;
    }
}
