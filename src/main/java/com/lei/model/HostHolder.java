package com.lei.model;

import org.springframework.stereotype.Component;

/**
 * Created by John on 2017/5/21.
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();
    public User get(){
        return users.get();
    }
    public void set(User user){
        users.set(user);
    }
    public void clear(){
        users.remove();
    }
}
