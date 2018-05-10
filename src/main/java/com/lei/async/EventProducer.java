package com.lei.async;

import com.google.gson.JsonObject;
import com.lei.constant.RedisKeyUtil;
import com.lei.service.JedisAdapter;
import com.lei.util.JsonUtils;
import com.lei.util.LoggerUtil;
import org.apache.commons.digester.annotations.rules.SetRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by John on 2017/5/25.
 * 将任务添加至Redis任务队列，在另一一个处理线程进行处理
 */
@Service
public class EventProducer {
    @Autowired
    private JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel) {
        try {
            LoggerUtil.Logger("发送推送消息");
            String json = JsonUtils.objectToJson(eventModel);
            String key = RedisKeyUtil.getBizQueueModelKey();
            jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
