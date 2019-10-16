package cn.xr.dao;

/**
 *
 * 结果集
 * @param <T> 项类型
 * @author liuliuliu
 */
interface Resulted<T> {

    /**
     * 获取查询结果集.
     *
     * @return 结果列表
    List<T> list();
     */

    /**
     * 获取查询分页数据.
     *
     * @return 分页数据
     */
    Page<T> page();

    /**
     * 获取查询结果集第一项.
     *
     * @return 结果集第一项
     */
    T first();

    /**
     * 获取查询结果集唯一项.
     *
     * @return 唯一项
     * @throws RuntimeException 如果结果集多于一项.
     */
    T unique();

    /**
     * 获取查询结果集数量.
     *
     * @return 结果集数量
     */
    int count();
}
