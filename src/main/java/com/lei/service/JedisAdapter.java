package com.lei.service;

import com.lei.util.LoggerUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by John on 2017/5/24.
 */
@Component
public class JedisAdapter implements InitializingBean {
    String host = "192.168.56.150";
    int port = 6379;
    private JedisPool jedisPool;

    @Override
    public void afterPropertiesSet() throws Exception {
        jedisPool = new JedisPool(host, port);
    }

    /**
     * 集合添加
     *
     * @param key
     * @param value
     * @return
     */
    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.Logger("发生异常：" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 集合移除成员
     *
     * @param key
     * @param value
     * @return
     */
    public long sremove(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.Logger("发生异常：" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 集合长度
     *
     * @param key
     * @return
     */
    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.Logger("发生异常：" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 是否为集合的成员
     *
     * @param key
     * @param value
     * @return
     */
    public boolean sismenber(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.Logger("发生异常：" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            LoggerUtil.Logger("发送异常:" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 返回值 key  value 会有两个字符串
     *
     * @param timeout
     * @param key
     * @return
     */
    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            LoggerUtil.Logger("发生异常:" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 获取jedis对象
     *
     * @return
     */
    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 开启Jedis事务
     */
    public Transaction multi(Jedis jedis) {
        try {
            return jedis.multi();
        } catch (Exception e) {
            LoggerUtil.Logger("Jedis事务开启异常:" + e.getMessage());
        }
        return null;
    }

    public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            LoggerUtil.Logger("发生异常" + e.getMessage());
            tx.discard();
        } finally {
            if (tx != null) {
                try {
                    tx.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zcard(String followerKey) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zcard(followerKey);
        } catch (Exception e) {
            LoggerUtil.Logger("发生异常:" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Set<String> zRange(String key, int startIndex, int endIndex) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Set<String> zrange = jedis.zrange(key, startIndex, endIndex);
            return zrange;
        } catch (Exception e) {
            LoggerUtil.Logger("发生异常:" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return new HashSet<>();
    }

    public Double zscore(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            LoggerUtil.Logger("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
}
