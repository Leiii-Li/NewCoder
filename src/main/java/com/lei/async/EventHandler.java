package com.lei.async;

import java.util.List;

/**
 * Created by John on 2017/5/25.
 * 每个Handler 都会一个处理任务的方法
 * 每个Handler 都会有一个List每种可以处理的EventType都会注册在List中
 */
public interface EventHandler {
    void doHandler(EventModel model);

    List<EventType> getSupportTypes();
}
