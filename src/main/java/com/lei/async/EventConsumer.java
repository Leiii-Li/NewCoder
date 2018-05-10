package com.lei.async;

import com.lei.constant.RedisKeyUtil;
import com.lei.service.JedisAdapter;
import com.lei.util.JsonUtils;
import com.lei.util.LoggerUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by John on 2017/5/25.
 * 处理线程，在Redis任务对象取出任务对象进行处理
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    @Autowired
    private JedisAdapter jedisAdapter;

    private ApplicationContext mContext;

    // 所有EventType 所注册的Handler
    private Map<EventType, List<EventHandler>> config = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        // 获取所有实现Handler的类
        Map<String, EventHandler> beans = mContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                // 将每个EventHandler遍历出来，将其注册到响应的List
                List<EventType> types = entry.getValue().getSupportTypes();
                for (EventType eventType : types) {
                    if (!config.containsKey(eventType)) {
                        // 如果没有包含当前Key说明还没有注册响应EventType,那么就新建一个
                        config.put(eventType, new ArrayList<EventHandler>());
                    }
                    config.get(eventType).add(entry.getValue()); // 将其注册到响应的队列
                }
            }
        }
        // 新建一个处理Redis任务队列的线程，一直进行处理
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String key = RedisKeyUtil.getBizQueueModelKey();
                    List<String> events = jedisAdapter.brpop(0, key);
                    //取出任务队列  没移除一个任务 会包含Key 以及其EventModel
                    if (events != null) {
                        System.out.println("接受到点赞事件：" + events);
                        for (String message : events) {
                            if (key.equals(message)) {
                                continue;
                            }
                            EventModel eventModel = JsonUtils.jsonToPojo(message, EventModel.class);
                            System.out.println("EventModel:  " + eventModel);
                            // 获得其EventModel
                            if (!config.containsKey(eventModel.getType())) {
                                // 如果当前EventType 已经被注册在config内说明已经有与之对应处理的EventHadler，否则视为非法事件
                                LoggerUtil.Logger("不能识别的事务" + eventModel.getEntityType());
                                continue;
                            }
                            for (EventHandler handler : config.get(eventModel.getType())) { // 获取EventType 对应的EventType处理Handler队列
                                handler.doHandler(eventModel);
                            }
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.mContext = applicationContext;
    }
}
