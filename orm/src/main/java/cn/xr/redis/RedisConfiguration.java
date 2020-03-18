package cn.xr.redis;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@Configuration
@EnableCaching
public class RedisConfiguration extends CachingConfigurerSupport {

    /**
     * 自定义生成key的规则
     *
     * @return KeyGenerator
     */
    @Override
    public KeyGenerator keyGenerator() {
        return (o, method, objects) -> {
            //格式化缓存key字符串
            StringBuilder sb = new StringBuilder();
            //追加类名
            sb.append(o.getClass().getName());
            //追加方法名
            sb.append(method.getName());
            //遍历参数并且追加
            for (Object obj : objects) {
                sb.append(obj.toString());
            }
            log.info("调用Redis缓存Key :{}", sb);
            return sb.toString();
        };
    }


    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        return  RedisCacheManager.builder(factory).build();
    }

    @Bean
    public RedisTemplate<String, String> redisTemplateSome(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        GenericFastJsonRedisSerializer jackson2JsonRedisSerializer = new GenericFastJsonRedisSerializer();
        template.setKeySerializer(jackson2JsonRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

}
