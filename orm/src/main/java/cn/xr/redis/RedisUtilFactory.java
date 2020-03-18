package cn.xr.redis;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 创建redis的工厂类
 *
 * @author liuliuliu
 * @since 2019/11/21
 */
@Component
@Slf4j
public class RedisUtilFactory {
    /**
     * redis链接工厂
     */
    private static RedisConnectionFactory factory;

    private static List<DeleteRedisCache> deleteRedisCaches;

    /**
     * local cache save current factory
     */
    private static ConcurrentMap<String, RedisUtilEnhance<?, ?>> TEMP_MAP = new ConcurrentHashMap<>();

    /**
     * 默认的key为string，value为Object的redis工具
     */
    private static RedisUtilEnhance<String, Object> defaultUtil;

    /**
     * 获取默认的redis工具类（string，value为Object的redis工具）
     *
     * @return 返回的是对应kv的{@link com.weimai.wmcard.common.redis.RedisUtilEnhance}
     */
    public static RedisUtilEnhance<String, Object> getDefaultUtil() {
        if (defaultUtil == null) {
            defaultUtil = create(String.class, Object.class);
            Assert.notNull(defaultUtil, "defaultUtil create error");
            try {
                defaultUtil.set("test redis connection", "test redis connection");
            } catch (Exception e) {
                throw new RuntimeException("无法连接redis");
            }
        }
        return defaultUtil;
    }

    /**
     * 自动注入{@link RedisConnectionFactory}
     *
     * @param factory RedisConnectionFactory
     */
    public RedisUtilFactory(@Autowired RedisConnectionFactory factory, @Autowired List<DeleteRedisCache> deleteRedisCaches) {
        RedisUtilFactory.factory = factory;
        RedisUtilFactory.deleteRedisCaches = deleteRedisCaches;
        defaultUtil = create(String.class, Object.class);
        Assert.notNull(defaultUtil, "defaultUtil create error");
    }

    /**
     * 根据key和value的类型创造一个对应类型的{@link RedisUtilEnhance}对象
     *
     * @param keyType   key的类型
     * @param valueType value的类型
     * @param <K>       任意Object
     * @param <V>       任意Object
     * @return 返回的是对应kv的{@link com.weimai.wmcard.common.redis.RedisUtilEnhance}
     */
    @SuppressWarnings("unchecked")
    public static <K extends Serializable, V> RedisUtilEnhance<K, V> create(Class<K> keyType, Class<V> valueType) {
        if (factory == null) {
            setFactory();
        }
        Assert.notNull(factory, "redis factory must not be null!");
        String key = keyType.getSimpleName() + valueType.getSimpleName();
        synchronized (Byte.class) {
            if (TEMP_MAP.containsKey(key)) {
                RedisUtilEnhance<?, ?> redisUtil = TEMP_MAP.get(key);
                return (RedisUtilEnhance<K, V>) redisUtil;
            } else {
                RedisTemplate<K, V> template = new RedisTemplate<K, V>();
                template.setConnectionFactory(factory);
                GenericFastJsonRedisSerializer jackson2JsonRedisSerializer = new GenericFastJsonRedisSerializer();
                template.setKeySerializer(jackson2JsonRedisSerializer);
                template.setValueSerializer(jackson2JsonRedisSerializer);
                template.setHashKeySerializer(jackson2JsonRedisSerializer);
                template.setHashValueSerializer(jackson2JsonRedisSerializer);
                template.afterPropertiesSet();
                RedisUtilEnhance<K, V> redisUtilEnhance = new RedisUtilEnhance<K, V>(template, deleteRedisCaches);
                TEMP_MAP.put(key, redisUtilEnhance);
                return redisUtilEnhance;
            }
        }
    }

    public static boolean isInit() {
        return factory != null;
    }

    public static boolean isNotInit() {
        return factory == null;
    }

    public static RedisConnectionFactory getFactory() {
        return factory;
    }

    /**
     * 如果自动注入工厂未曾注入，则手动设置redis链接工厂
     */
    public static void setFactory() {
        PropertySource propertySource = null;
        try {
            propertySource = PropertyUtil.get("application.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert propertySource != null;
        Object database = propertySource.getProperty("spring.redis.database");
        Object host = propertySource.getProperty("spring.redis.host");
        Object port = propertySource.getProperty("spring.redis.port");
        Object password = propertySource.getProperty("spring.redis.password");
        log.info("redis-database:{}", database);
        log.info("redis-host:{}", host);
        log.info("redis-port:{}", port);
        RedisStandaloneConfiguration re = new RedisStandaloneConfiguration();
        if (database != null) {
            re.setDatabase(Integer.parseInt(database.toString()));
        }
        if (password != null) {
            String s = password.toString();
            re.setPassword(RedisPassword.of(s));
        }
        if (host != null) {
            re.setHostName(host.toString());
        }
        if (port != null) {
            re.setPort(Integer.parseInt(port.toString()));
        }
        factory = new LettuceConnectionFactory(re);
        //LettuceConnectionFactory必须调用afterPropertiesSet函数进行初始化
        ((LettuceConnectionFactory) factory).afterPropertiesSet();
    }


}
