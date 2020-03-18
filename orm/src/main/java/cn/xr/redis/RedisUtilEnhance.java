package cn.xr.redis;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 操作redis的工具类
 *
 * @author liuliuliu
 * @since 2019/11/27
 */
@SuppressWarnings({"unchecked", "unused" })
@Slf4j
public final class RedisUtilEnhance<K extends Serializable, V> {

    private List<DeleteRedisCache> deleteRedisCaches;
    private RedisTemplate<K, V> redisTemplate;
    private DeleteRedisCache deleteRedisCache;

    RedisUtilEnhance(RedisTemplate<K, V> redisTemplate, List<DeleteRedisCache> deleteRedisCaches) {
        this.redisTemplate = redisTemplate;
        this.deleteRedisCaches = deleteRedisCaches;
    }

    /**
     * 刷新token 键值对
     */
    public final String TO_STRING_USERS = "SYS_TOStringEN_USERS";

    // =============================common============================

    /**
     * 根据key的hashcode和toString()创建一个缓存key
     *
     * @param key 任意对象
     * @return 最终生成的key
     */
    public String createKey(Object key) {
        Assert.notNull(key, "key不能为空");
        return key.toString() + "#" + hash(key);
    }

    /**
     * 根据key的hashcode和toString()和指定后缀创建一个缓存key
     *
     * @param key    任意对象
     * @param suffix 后缀
     * @return 最终生成的key
     */
    public String createKey(Object key, String suffix) {
        Assert.notNull(key, "key不能为空");
        return (key.toString() + "#" + hash(key) + "#" + suffix);
    }

    /**
     * {@link java.util.HashMap}的hashcode函数
     *
     * @param key 任意对象
     * @return hashcode码
     */
    private int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    private void expire(K key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logError("设置redis缓存失效时间失败: key={} time={}", new Object[]{key, time}, e);
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(K key) {
        Assert.notNull(key, "key must not be null");
        return handlerResult(redisTemplate.getExpire(key, TimeUnit.SECONDS));
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean haskey(K key) {
        try {
            return handlerResult(redisTemplate.hasKey(key));
        } catch (Exception e) {
            logError("判断缓存key是否存在失败: key={}", new Object[]{key}, e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void del(K... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public V get(K key) {
        return ObjectUtils.isEmpty(key) ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     */
    public void set(K key, V value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            logError("插入新缓存失败: key={} value={}", new Object[]{key, value}, e);
        }

    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     */
    public void set(K key, V value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
        } catch (Exception e) {
            logError("插入新缓存失败: key={} value={} time={}", new Object[]{key, value, time}, e);
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return 结果可能为0
     */
    public long incr(K key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        Long increment = redisTemplate.opsForValue().increment(key, delta);
        return handlerResult(increment);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return 结果可能为0
     */
    public long decr(K key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        Long increment = redisTemplate.opsForValue().increment(key, -delta);
        return handlerResult(increment);
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(K key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * HashGet
     *
     * @param key 键 不能为null
     * @return 值
     */
    public Map<Object, Object> hGetAll(K key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取hashStringey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(K key) {
        return redisTemplate.opsForHash().entries(key);
    }


    /**
     * HashSet,设置失败抛出异常
     *
     * @param key 键
     * @param map 对应多个键值
     */
    public void hmset(K key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            logError("插入新hash缓存失败: key={} map={}", new Object[]{key, map}, e);
        }
    }

    /**
     * HashSet并设置整个map失效的时间
     *
     * @param key        键
     * @param map        对应多个键值
     * @param deleteTime 时间(秒)
     */
    public void hmset(K key, Map<String, Object> map, long deleteTime) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (deleteTime > 0) {
                expire(key, deleteTime);
            }
        } catch (Exception e) {
            logError("插入新hash缓存失败: key={} map={} deleteTime={}", new Object[]{key, map, deleteTime}, e);
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     */
    public void hset(K key, String item, V value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
        } catch (Exception e) {
            logError("插入新hash缓存失败: key={} item={} value={}", new Object[]{key, item, value}, e);
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key        键
     * @param item       项
     * @param value      值
     * @param deleteTime 时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     */
    public void hset(K key, final K item, V value, long deleteTime) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (deleteTime > 0) {
                getDeleteRedisCache().delRedisMapSingleValueCache(key, item, deleteTime);
            }
        } catch (Exception e) {
            logError("插入新hash缓存失败: key={} item={} value={} deleteTime={}", new Object[]{key, item, value, deleteTime}, e);
        }
    }


    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(K key, Object... item) {
//        Assert.notNull(key, "key must not be null");
//        Assert.state(ArrayUtils.isNotEmpty(item), "item must not be null");
        try {
            redisTemplate.opsForHash().delete(key, item);
        } catch (Exception e) {
            logError("hash删除缓存失败: key={} item={}", new Object[]{key, item}, e);
        }
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(K key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return 结果值
     */
    public double hincr(K key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return 结果值
     */
    public double hdecr(K key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return 对应key的set集合
     */
    public Set<V> sGet(K key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            logError("set获取缓存失败: key={} ", new Object[]{key}, e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(K key, V value) {
        try {
            return handlerResult(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(K key, V... values) {
        try {
            Long add = redisTemplate.opsForSet().add(key, values);
            return handlerResult(add);
        } catch (Exception e) {
            logError("set设置缓存失败: key={} values={} ", new Object[]{key, values}, e);
            return 0;
        }
    }

    /**
     * 将多个数据放置入set缓存中，并且指定这多个数据的定时删除时间
     *
     * @param key        键
     * @param deleteTime 时间(秒)
     * @param values     值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(K key, final long deleteTime, final V... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (deleteTime > 0) {
                getDeleteRedisCache().delRedisSetSingleValueCache(key, values, deleteTime);
            }
            return handlerResult(count);
        } catch (Exception e) {
            logError("set设置缓存失败: key={} values={} deleteTime={}", new Object[]{key, values, deleteTime}, e);
            return 0;
        }
    }

    /**
     * 将多个数据放置入set缓存中，并且指定整个set定时删除时间
     *
     * @param key        键
     * @param deleteTime 时间(秒)
     * @param values     值 可以是多个
     * @return 成功个数
     */
    public long sSetAndALLTime(K key, final long deleteTime, final V... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (deleteTime > 0) {
                expire(key, deleteTime);
            }
            return handlerResult(count);
        } catch (Exception e) {
            logError("set设置缓存失败: key={} values={} deleteTime={}", new Object[]{key, values, deleteTime}, e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return 获取set的size
     */
    public long sGetSetSize(K key) {
        try {
            return handlerResult(redisTemplate.opsForSet().size(key));
        } catch (Exception e) {
            logError("set获取指定key的缓存个数失败: key={}", new Object[]{key}, e);
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(K key, V... values) {
        try {
            Assert.noNullElements(values, "value must not be null");
            Object[] objects = Arrays.stream(values).map((Function<V, Object>) v -> v).toArray();
            return handlerResult(redisTemplate.opsForSet().remove(key, objects));
        } catch (Exception e) {
            logError("set获取指定key的缓存个数失败: key={}", new Object[]{key}, e);
            return 0;
        }
    }


    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key 键
     * @return {@literal null} 如果没有找到
     */
    public List<V> lGet(K key) {
        try {
            return redisTemplate.opsForList().range(key, 0, lGetListSize(key));
        } catch (Exception e) {
            logError("list获取指定key的缓存失败: key={}", new Object[]{key}, e);
            return null;
        }
    }

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return 截取到的list对象
     */
    public List<V> lGet(K key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            logError("list获取指定key的指定范围内的缓存失败: key={} start={} end={}", new Object[]{key, start, end}, e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return 获取list大小
     */
    public long lGetListSize(K key) {
        try {
            return handlerResult(redisTemplate.opsForList().size(key));
        } catch (Exception e) {
            logError("list获取指定key的缓存个数失败: key={} ", new Object[]{key}, e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return 获取指定索引的元素
     */
    public V lGetIndex(K key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            logError("list获取指定key的指定索引的缓存失败: key={} index={} ", new Object[]{key, index}, e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public void lSet(K key, V value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            logError("list设置单个元素失败: key={} value={} ", new Object[]{key, value}, e);
        }
    }

    /**
     * 将一个元素放入redis的list缓存中，且根据deleteTime秒数，定时删除此value，如果多次设置会以最新的时间为标准，如果时间未曾超过最新时间则不删除
     *
     * @param key        键
     * @param value      值
     * @param deleteTime 时间(秒) 必须大于0否则无效
     */
    public void lSet(K key, V value, long deleteTime) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (deleteTime > 0) {
                getDeleteRedisCache().delRedisListSingleValueCache(key, Lists.newArrayList(value), deleteTime);
            }
        } catch (Exception e) {
            logError("list设置单个元素失败: key={} value={} deleteTime={}", new Object[]{key, value, deleteTime}, e);
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public void lSet(K key, List<V> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
        } catch (Exception e) {
            logError("list设置多个元素失败: key={} value={}", new Object[]{key, value}, e);
        }
    }

    /**
     * 将list放入缓存且指定时间失效
     *
     * @param key        键
     * @param values     值
     * @param deleteTime 时间(秒) 必须大于0否则无效
     */
    public void lSetAndTime(K key, List<V> values, long deleteTime) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            if (deleteTime > 0) {
                List<Object> collect = values.stream().map((Function<V, Object>) v -> v).collect(Collectors.toList());
                getDeleteRedisCache().delRedisListSingleValueCache(key, collect, deleteTime);
            }
        } catch (Exception e) {
            logError("list设置多个元素失败: key={} value={}", new Object[]{key, values}, e);
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(K key) {
        try {
            return handlerResult(redisTemplate.hasKey(key));
        } catch (Exception e) {
            logError("list判断key是否存在失败: key={}", new Object[]{key}, e);
            return false;
        }
    }

    /**
     * 将list放入缓存,且定时删除整个list
     *
     * @param key        键
     * @param value      值
     * @param deleteTime 时间(秒) 必须大于0否则无效
     */
    public void lSetAndAllTime(final K key, List<V> value, final long deleteTime) {
        try {
            if (!CollectionUtils.isEmpty(value)) {
                redisTemplate.opsForList().rightPushAll(key, value);
            }
            if (deleteTime > 0) {
                expire(key, deleteTime);
            }
        } catch (Exception e) {
            logError("list置入缓存失败: key={} value={} deleteTime={}", new Object[]{key, value, deleteTime}, e);
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     */
    public void lUpdateIndex(K key, long index, V value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
        } catch (Exception e) {
            logError("根据索引修改list中的某条数据失败: key={} index={} value={}", new Object[]{key, index, value}, e);
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(K key, long count, V value) {
        try {
            return handlerResult(redisTemplate.opsForList().remove(key, count, value));
        } catch (Exception e) {
            logError("移除N个值为value失败: key={} count={} value={}", new Object[]{key, count, value}, e);
            return 0;
        }
    }

    //=====================GEO=======================

    /**
     * 添加一组位置数据到缓存中
     *
     * @param key    此缓存的key(可装入多个经纬度)
     * @param lng    经度
     * @param lat    纬度
     * @param member 与经纬度绑定的缓存key
     * @return 响应行数
     */
    public long geoAdd(K key, double lng, double lat, V member) {
        Point point = new Point(lng, lat);
        return handlerResult(redisTemplate.opsForGeo().add(key, point, member));
    }

    /**
     * 添加一组位置数据到缓存中
     *
     * @param key        此缓存的key(可装入多个经纬度)
     * @param lng        经度
     * @param lat        纬度
     * @param member     与经纬度绑定的缓存keyl
     * @param deleteTime 时间(秒) 必须大于0否则无效
     * @return 响应行数
     */
    public long geoAddWithTime(K key, double lng, double lat, final V member, long deleteTime) {
        Point point = new Point(lng, lat);
        Long add = redisTemplate.opsForGeo().add(key, point, member);
        if (deleteTime > 0) {
            getDeleteRedisCache().delRedisGeoSingleValueCache(key, member, deleteTime);
        }
        return handlerResult(add);
    }

    /**
     * 根据key获取缓存中位置信息
     *
     * @param key          此缓存的key(可装入多个经纬度)
     * @param memberString 与经纬度绑定的缓存key
     * @return 响应行数
     */
    public List<Point> geoGet(K key, V... memberString) {
        return redisTemplate.opsForGeo().position(key, memberString);
    }

    /**
     * 根据key删除缓存中位置信息
     *
     * @param key          此缓存的key(可装入多个经纬度)
     * @param memberString 与经纬度绑定的缓存key
     * @return 响应行数
     */
    public long geoRemove(K key, V... memberString) {
        Long remove = redisTemplate.opsForGeo().remove(key, memberString);
        return handlerResult(remove);
    }


    /**
     * 判断经纬度间距离
     *
     * @param key     经纬度缓存key
     * @param member1 经纬度的key-key1
     * @param member2 经纬度的key-key2
     * @param metric  科学计算单位
     * @return 两组经纬度间的距离
     */
    public double distance(K key, V member1, V member2, Metric metric) {
        Distance distance = redisTemplate.opsForGeo().distance(key, member1, member2, metric);
        return handlerResult(distance);
    }

    /**
     * 判断经纬度间距离 默认按照科学距离 mi
     *
     * @param key     经纬度信息的key
     * @param member1 与经纬度绑定的key1
     * @param member2 与经纬度绑定的key2
     * @return 两组经纬度的距离
     */
    public double distance(K key, V member1, V member2) {
        Distance distance = redisTemplate.opsForGeo().distance(key, member1, member2);
        return handlerResult(distance);
    }


    /**
     * 判断key内存中member代表的经纬度为中心，范围内的width之间所有符合的地理点
     * 默认一次性查询所有且按照升序排序，包含距离
     *
     * @param key    经纬度信息的key
     * @param member 与经纬度绑定的key1
     * @param width  中心范围
     * @return 两组经纬度的距离
     */
    public List<GeoResult<RedisGeoCommands.GeoLocation<V>>> radius(K key, V member, double width) {
        RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs = getGeoRadiusCommandArgs();
        GeoResults<RedisGeoCommands.GeoLocation<V>> radius = redisTemplate.opsForGeo().radius(key, member, new Distance(width), geoRadiusCommandArgs);
        return radius == null ? Lists.newArrayList() : radius.getContent();
    }

    /**
     * 判断key内存中member代表的经纬度为中心，范围内的width之间所有符合的地理点
     * 默认一次性查询所有且按照升序排序，包含距离
     *
     * @param key    经纬度信息的key
     * @param member 与经纬度绑定的key1
     * @param width  中心范围
     * @return 两组经纬度的距离
     */
    public List<GeoResult<RedisGeoCommands.GeoLocation<V>>> radius(K key, V member, double width, int limit) {
        RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs = getGeoRadiusCommandArgs();
        geoRadiusCommandArgs.limit(limit);
        GeoResults<RedisGeoCommands.GeoLocation<V>> radius = redisTemplate.opsForGeo().radius(key, member, new Distance(width), geoRadiusCommandArgs);
        return radius == null ? Lists.newArrayList() : radius.getContent();
    }


    private RedisGeoCommands.GeoRadiusCommandArgs getGeoRadiusCommandArgs() {
        RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs();
        geoRadiusCommandArgs.sortAscending();
        geoRadiusCommandArgs.limit(Integer.MAX_VALUE);
        geoRadiusCommandArgs.includeDistance();
        return geoRadiusCommandArgs;
    }

    //=====================MESSAGE=======================

    public void convertAndSend(K key, V value) {
        redisTemplate.convertAndSend(key.toString(), value);
    }


    /**
     * 判断key内存中member代表的经纬度为中心，范围内的width之间所有符合的地理点 mi
     *
     * @param geoRadiusCommandArgs 查询的参数
     * @param key                  经纬度信息的key
     * @param member               与经纬度绑定的key1
     * @param width                中心范围
     * @return 两组经纬度的距离
     */
    @Nullable
    public List<GeoResult<RedisGeoCommands.GeoLocation<V>>> radius(K key, V member, double width, RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs) {
        GeoResults<RedisGeoCommands.GeoLocation<V>> radius = redisTemplate.opsForGeo().radius(key, member, new Distance(width), geoRadiusCommandArgs);
        return radius == null ? null : radius.getContent();
    }

    private long handlerResult(Long size) {
        if (size != null) {
            return size;
        }
        return 0;
    }

    private boolean handlerResult(Boolean b) {
        if (b != null) {
            return b;
        }
        return false;
    }


    private double handlerResult(Distance distance) {
        if (distance != null) {
            return distance.getValue();
        }
        return -1;
    }


    private DeleteRedisCache getDeleteRedisCache() {
        if (deleteRedisCache == null) {
            if (!CollectionUtils.isEmpty(deleteRedisCaches)) {
                this.deleteRedisCache = deleteRedisCaches.get(0);
            } else {
                this.deleteRedisCache = new DefaultDeleteRedisCache();
            }
        }
        return this.deleteRedisCache;
    }


    private void logError(String context, Object[] arr, Exception e) throws RuntimeException {
        String message = MessageFormatter.arrayFormat(context, arr, e).getMessage();
        throw new RuntimeException(message);
    }

}