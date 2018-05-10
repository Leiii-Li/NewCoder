package com.lei.interceptor;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.lei.constant.KeyConstant;
import com.lei.model.HostHolder;
import com.lei.model.LoginTicket;
import com.lei.model.User;
import com.lei.service.LoginTicketService;
import com.lei.service.UserService;
import com.lei.util.LoggerUtil;
import com.lei.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import sun.rmi.runtime.Log;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by John on 2017/5/21.
 */
@Component
public class PassportHandler implements HandlerInterceptor {
    @Autowired
    private LoginTicketService loginTicketService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        // 在实际的handler被执行前被调用
        // 获取Ticket
        Cookie[] cookies = httpServletRequest.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (KeyConstant.TICKET_KEY.equals(cookie.getName())) { // 有ticket
                    String ticket = cookie.getValue();
                    // 通过ticket获取LoginTicket
                    LoginTicket loginTicket = loginTicketService.findByTicket(ticket);
                    if (null != loginTicket && loginTicket.getExpired().after(new Date())) {
                        updateTicket(loginTicket);
                        // 不能为空不能过期状态必须为0
                        User user = userService.selectByUserId(loginTicket.getUserId());
                        if (null != user) {
                            hostHolder.set(user);
                        }
                    }
                    break;
                }
            }
        }
        return true;
    }

    private void updateTicket(LoginTicket loginTicket) {
        Date now = new Date();
        now.setTime(now.getTime() + 3600 * 24 * 7); //将过期时间推后7天，7天内访问网站不需要输入用户密码
        loginTicket.setExpired(now);
        // 插入数据库
        loginTicketService.updateTicket(loginTicket);
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        // 在handler被执行后被调用
        User user = hostHolder.get();
        if (null != user) {
            user.setSalt(null);
            user.setPassword(null);
            if (null != modelAndView && null != user) {
                modelAndView.addObject("userHeader", user);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        // 当request处理完成后被调用
        hostHolder.clear();
    }
}
