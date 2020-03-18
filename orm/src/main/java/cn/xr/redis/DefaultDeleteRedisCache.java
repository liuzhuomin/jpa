package cn.xr.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 默认实现定时删除redis特殊结构的实现类
 *
 * @author liuliuliu
 * @since 2020/03/12
 */
@Component
@Slf4j
public class DefaultDeleteRedisCache implements DeleteRedisCache {

    private final Timer timer = new Timer();
    private RedisUtilEnhance<String, Object> defaultUtil = RedisUtilFactory.getDefaultUtil();

    @Override
    public void delRedisSetSingleValueCache(Object key, Object[] values, long deleteTime) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("delete redis set single value : key={} values={}", key, values);
                defaultUtil.setRemove(key.toString(), values);
            }
        }, deleteTime * 1000);
    }

    @Override
    public void delRedisMapSingleValueCache(Object key, Object item, long deleteTime) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("delete redis map single value : key={} ,item={} ", key, item);
                defaultUtil.hdel(key.toString(), item);
            }
        }, deleteTime * 1000);
    }

    @Override
    public void delRedisListSingleValueCache(Object key, List<Object> values, long deleteTime) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("delete redis list single value : key={} values={}", key, values);
                for (Object value : values) {
                    defaultUtil.lRemove(key.toString(), 1, value);
                }
            }
        }, deleteTime * 1000);
    }

    @Override
    public void delRedisGeoSingleValueCache(Object key, Object member, long deleteTime) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("delete redis geo single value : key={} member={} ", key, member);
                defaultUtil.geoRemove(key.toString(), member);
            }
        }, deleteTime * 1000);
    }


}
