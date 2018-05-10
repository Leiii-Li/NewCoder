package com.lei.service;

import com.lei.constant.RedisKeyUtil;
import com.lei.model.User;
import com.lei.util.WendaUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by John on 2017/5/26.
 */
@Service
public class FollowService {
    @Autowired
    private JedisAdapter jedisAdapter;


    /**
     * 用户id 类型 被关注的用户id
     *
     * @param userId
     * @param entityType
     * @param followUserId
     */
    public boolean followUser(String userId, int entityType, String followUserId) {
        // 关注人key 添加关注人
        String followerKey = RedisKeyUtil.getBizFollowerKey(entityType, userId);
        // 被关注人key 添加粉丝
        String followeeKey = RedisKeyUtil.getBizFansKey(entityType, followUserId);
        // 事务处理
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        // 关注的人 添加关注的人
        tx.zadd(followerKey, date.getTime(), followUserId);
        // 被关注的人 添加粉丝
        tx.zadd(followeeKey, date.getTime(), userId);
        List<Object> ret = jedisAdapter.exec(tx, jedis);
//        if (jedis != null) {
//            jedis.close();
//            jedis = null;
//        }
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    /**
     * 返回当前用关注的人数
     */
    public long getFolloweesCount(String userId, int entityType) {
        String followerKey = RedisKeyUtil.getBizFollowerKey(entityType, userId);
        long count = jedisAdapter.zcard(followerKey);
        return count;
    }

    /**
     * 获得当前用户粉丝的数量
     *
     * @param userId
     * @param entityType
     * @return
     */
    public long getFansCount(String userId, int entityType) {
        String followeeKey = RedisKeyUtil.getBizFansKey(entityType, userId);
        long count = jedisAdapter.zcard(followeeKey);
        return count;
    }

    /**
     * 参数一 当前用户Id 参数二 当前是那种类型 参数三被取消关注用户的Id
     */
    public boolean unfollowUser(String id, int entityType, String followUserId) {
        // 关注人key 删除关注人
        String followerKey = RedisKeyUtil.getBizFollowerKey(entityType, id);
        // 被关注人key 删除粉丝粉丝
        String followeeKey = RedisKeyUtil.getBizFansKey(entityType, followUserId);
        // 事务处理
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        // 关注的人 删除关注的人
        tx.zrem(followerKey, followUserId);
        // 被关注的人 删除粉丝
        tx.zrem(followeeKey, id);
        List<Object> ret = jedisAdapter.exec(tx, jedis);
//        if (jedis != null) {
//            jedis.close();
//            jedis = null;
//        }
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;

    }

    public List<String> getAllFollower(String userId, int entityType) {
        String followerKey = RedisKeyUtil.getBizFollowerKey(entityType, userId);
        Set<String> set = jedisAdapter.zRange(followerKey, 0, -1);
        return getIdsFromSet(set);
    }

    private List<String> getIdsFromSet(Set<String> idset) {
        List<String> ids = new ArrayList<>();
        for (String str : idset) {
            ids.add(str);
        }
        return ids;
    }

    public boolean getIsFollowed(String id, int entityType, String userId) {
        // 被关注人key 删除粉丝粉丝
        String followeeKey = RedisKeyUtil.getBizFansKey(entityType, userId);
        Double zscore = jedisAdapter.zscore(followeeKey, id);
        if (zscore != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean followQuestion(String questionId, int entityType, String userId) {
        // 通过问题Id获取key ,当需要获取当前问题被那些人关注时，可以通过QuestionId来获取
        // QuestionId 存入的是UserId
        String questionFollowkKey = RedisKeyUtil.getBizFollowerKey(entityType, questionId);
        // 通过UserId获取Key,当需要知道用户关注了哪些问题时，可以通过UserId来获取
        // 存入的是QuestionId
        String userFollowQuestionKey = RedisKeyUtil.getBizFansKey(entityType, userId);
        // 事务处理
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zadd(questionFollowkKey, date.getTime(), userId);
        tx.zadd(userFollowQuestionKey, date.getTime(), questionId);
        List<Object> ret = jedisAdapter.exec(tx, jedis);
//        if (jedis != null) {
//            jedis.close();
//            jedis = null;
//        }
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    public long getFolloweesQuestionCount(String questionId, int entityType) {
        String questionFollowkKey = RedisKeyUtil.getBizFollowerKey(entityType, questionId);
        Jedis jedis = jedisAdapter.getJedis();
        Long zcard = jedis.zcard(questionFollowkKey);
        if (jedis != null) {
            jedis.close();
            jedis = null;
        }
        return zcard;
    }

    public List<String> getFolloweesQuestions(String questionId, int entityType) {
        String questionKey = RedisKeyUtil.getBizFollowerKey(entityType, questionId);
        Jedis jedis = jedisAdapter.getJedis();
        Set<String> zrange = jedis.zrange(questionKey, 0, -1); // 查询所有关注该问题的用户
        if (jedis != null) {
            jedis.close();
            jedis = null;
        }
        return WendaUtil.changeToList(zrange);
    }

    public boolean isFollower(String id, int entityType, String questionId) {
        String questionFollowkKey = RedisKeyUtil.getBizFollowerKey(entityType, questionId);
        // 获取用户的zscore 来判断当前用户是否关注了该问题
        return jedisAdapter.zscore(questionFollowkKey, id) != null;
    }

    public boolean unfollowQuestion(String questionId, int entityType, User user) {
        // 问题key 移除当前UserId
        String questionKey = RedisKeyUtil.getBizFollowerKey(entityType, questionId);

        // UserKey  移除当前问题Id
        String userFanKey = RedisKeyUtil.getBizFansKey(entityType, user.getId());
        // 进行事务处理
        Jedis jedis = jedisAdapter.getJedis();
        Transaction multi = jedisAdapter.multi(jedis);
        multi.zrem(questionKey, user.getId());
        multi.zrem(userFanKey, questionId);
        List<Object> exec = jedisAdapter.exec(multi, jedis);
//        if (jedis != null) {
//            jedis.close();
//            jedis = null;
//        }
        return exec.size() == 2 && (Long) exec.get(0) > 0 && (Long) exec.get(1) > 0;
    }

    /**
     * 判断当前登录用用户是否为当前查看用户的粉丝
     * @param user
     * @param entityType
     * @param userId
     * @return
     */
    public boolean isUserFollower(User user, int entityType, String userId) {
        // 通过当前查看用户的UserId 的key 获取其members是否包含user,getId();
        String fansKey = RedisKeyUtil.getBizFansKey(entityType, userId);
        return jedisAdapter.zscore(fansKey, user.getId()) != null;
    }

    public List<String> getFans(String userId, int entityType) {
        // 通过UserId获取粉丝Key
        String fansKey = RedisKeyUtil.getBizFansKey(entityType, userId);
        return WendaUtil.changeToList(jedisAdapter.zRange(fansKey,0,-1));
    }
}
