package com.lei.configration;

import com.lei.interceptor.CommentInterceptor;
import com.lei.interceptor.LoginInterceptor;
import com.lei.interceptor.PassportHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by nowcoder on 2016/7/3.
 */
@Component
public class NewCoderConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private PassportHandler passportHandler;

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private CommentInterceptor commentInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportHandler); // 添加用户信息获取拦截器
        registry.addInterceptor(loginInterceptor).addPathPatterns("/user/*");
        registry.addInterceptor(commentInterceptor).addPathPatterns("/addComment");
        super.addInterceptors(registry);
    }
}
