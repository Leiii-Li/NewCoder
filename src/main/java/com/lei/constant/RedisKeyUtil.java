package com.lei.constant;

/**
 * Created by John on 2017/5/24.
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String BIZ_LIKE = "BIZ_LIKE";
    private static final String BIZ_DISLIKE = "BIZ_DISLIKE";
    private static final String BIZ_QUEUE_MODEL_KEY = "BIZ_QUEUE_MODEL_KEY";
    // 关注
    private static final String BIZ_FOLLOWER_KEY = "BIZ_FOLLOWER_KEY";
    // 粉丝
    private static final String BIZ_FANS_KEY = "BIZ_FANS_KEY";

    /**
     * 获取关注的Key
     */
    public static String getBizFollowerKey(int entityType, String entityId) {
        return BIZ_FOLLOWER_KEY + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 获取粉丝的Key
     */
    public static String getBizFansKey(int entityType, String entityId) {
        return BIZ_FANS_KEY + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 点赞
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getLikeKey(int entityType, String entityId) {
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 取消点赞
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getDisLikeKey(int entityType, String entityId) {
        return BIZ_DISLIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getBizQueueModelKey() {
        return BIZ_QUEUE_MODEL_KEY;
    }

}
