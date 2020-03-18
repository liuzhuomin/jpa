package cn.xr.redis;

import java.util.List;

/**
 * 用于redis定时删除特殊结构内单个或者多个数据
 *
 * @author liuliuliu
 * @since 2020/3/12
 */
public interface DeleteRedisCache {

    /**
     * 定时删除redis set结构下多个value
     *
     * @param key        set的键
     * @param values     set内多个元素
     * @param deleteTime 定时时间(秒)
     */
    void delRedisSetSingleValueCache(Object key, Object[] values, long deleteTime);

    /**
     * 定时删除redis map结构下多个value
     *
     * @param key        map的键
     * @param item       map内单个元素的键
     * @param deleteTime 定时时间(秒)
     */
    void delRedisMapSingleValueCache(Object key, final Object item, long deleteTime);

    /**
     * 定时删除redis list结构下多个value
     *
     * @param key        list的键
     * @param values     list内的单个或者多个元素
     * @param deleteTime 定时时间(秒)
     */
    void delRedisListSingleValueCache(Object key, List<Object> values, long deleteTime);

    /**
     * 定时删除redis geo结构下多个value
     *
     * @param key        geo的键
     * @param member     geo内的单个键
     * @param deleteTime 定时时间(秒)
     */
    void delRedisGeoSingleValueCache(Object key, Object member, long deleteTime);

}
