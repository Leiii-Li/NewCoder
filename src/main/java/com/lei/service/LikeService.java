package com.lei.service;

import com.lei.constant.RedisKeyUtil;
import com.lei.model.HostHolder;
import com.lei.model.User;
import com.lei.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

/**
 * Created by John on 2017/5/24.
 */
@Service
public class LikeService {
    @Autowired
    private JedisAdapter jedisAdapter;

    public long like(String userId, int entityType, String entityId) {
        // 当一个用户点赞某条评论， 需要将其从点踩队列中移除  添加至点赞队列
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(likeKey, userId);

        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sremove(disLikeKey, userId);

        // 返回点赞队列集合的数量
        return jedisAdapter.scard(likeKey);
    }

    public long disLike(String userId, int entityType, String entityId) {
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(disLikeKey, userId);

        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.sremove(likeKey, userId);

        // 返回点赞队列集合的数量
        return jedisAdapter.scard(disLikeKey);
    }

    /**
     * 查看当前用户是否在点赞 或者 踩的集合中,因为在前端页面中写到 如果既没有点赞或者踩起liked值就为0
     * 点赞就大于1  踩 就小于1
     *
     * @param userId
     * @param entityType
     * @param commentId
     * @return
     */
    public int getLikeStatus(String userId, int entityType, String commentId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, commentId);
        if (jedisAdapter.sismenber(likeKey, userId)) {
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, commentId);
        return jedisAdapter.sismenber(disLikeKey, userId) ? -1 : 0;
    }

    public Object getLikeCount(int entityType, String entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return jedisAdapter.scard(likeKey);
    }
}
