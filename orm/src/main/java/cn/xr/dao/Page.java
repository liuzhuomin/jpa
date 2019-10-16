package cn.xr.dao;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Page类
 *
 * @param <E> 代表整个分页对象的结果集类型
 * @author admin
 */
@SuppressWarnings({"unused", "uncheckd"})
public final class Page<E> implements IPageQuery, IPageResult<E>, Iterable<E>, Serializable {

    private static final long serialVersionUID = 1521935410804036791L;

    /**
     * 代表分页的模式
     */
    enum Mode {
        /**
         * 数据库
         */
        db,
        /**
         * 内存
         */
        memory
    }

    /**
     * 默认的页码
     */
    public static final int DEFAULT_PAGE = 1;
    /**
     * 默认一页大小
     */
    public static final int DEFAULT_PAGESIZE = 1;
    /**
     * 默认查询总数
     */
    public static final boolean DEFAULT_AUTOCOUNT = true;
    /**
     * 默认总数0
     */
    public static final int DEFAULT_TOTALCOUNT = 0;
    /**
     * 默认的页码
     */
    private int page = DEFAULT_PAGE;
    /**
     * 默认一页大小
     */
    private int pagesize = DEFAULT_PAGESIZE;
    /**
     * 默认查询总数
     */
    private boolean autoCount = DEFAULT_AUTOCOUNT;
    /**
     * 默认总数
     */
    private int totalCount = DEFAULT_TOTALCOUNT;
    /**
     * 从数据库查找
     */
    private Mode mode = Mode.db;
    /**
     * 最终的结果集
     */
    private List<E> results = new ArrayList<E>();

    /**
     * 分页查找最终结果
     *
     * @param page      页码
     * @param pagesize  一页大小
     * @param autoCount 是否查询总记录数
     * @param <E>       查询的结果类型
     * @return 最终查询出来的分页对象 {@link Page}
     */
    public static <E> Page<E> get(int page, int pagesize, boolean autoCount) {
        return new Page<E>(page, pagesize, autoCount);
    }


    /**
     * 分页查找最终结果（{@link Page#autoCount}默认为true）
     *
     * @param page     页码
     * @param pagesize 一页大小
     * @param <E>      查询的结果类型
     * @return 最终查询出来的分页对象 {@link Page}
     */
    public static <E> Page<E> get(int page, int pagesize) {
        return get(page, pagesize, true);
    }

    /**
     * 根据总数和最终结果产生一个新的对象
     *
     * @param total 总记录数量
     * @param data  结果集合
     * @return 最终查询出来的分页对象 {@link Page}
     */
    public Page<E> data(int total, List<E> data) {
        return this.setTotal(total).data(data);
    }

    /**
     * 根据传递过来的结果集合创建分页对象，如果mode的类型为{@link Mode#memory}则自动将当前结果集的大小作为totalcount
     *
     * @param data 结果集
     * @return 最终查询出来的分页对象 {@link Page}
     */
    public Page<E> data(List<E> data) {
        if (isMemoryPage(data)) {
            this.mode = Mode.memory;
            setTotal(data.size());
            this.results = MemoryPageUtil.getPageResults(page, pagesize, data);
        } else {
            this.results = data;
        }

        if (this.results == null) {
            this.results = new ArrayList<E>();
        }
        return this;
    }

    /**
     * 是否是内存中的分页对象
     *
     * @param data 结果集合
     * @return 如果是返回true
     */
    private boolean isMemoryPage(List<?> data) {
        return (mode == Mode.memory || data.size() > this.pagesize);
    }

    /**
     * 设置总记录数
     *
     * @param totalCount 总记录数量
     * @return 分页对象
     */
    private Page<E> setTotal(int totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    /**
     * 根据当前页码、一页大小、和是否自动计算总记录数创建一个分页对象
     *
     * @param page      当前页码
     * @param pageSize  一页大小
     * @param autoCount 总记录数
     */
    private Page(int page, int pageSize, boolean autoCount) {
        this.page = (page < DEFAULT_PAGE) ? DEFAULT_PAGE : page;
        this.pagesize = pageSize < DEFAULT_PAGESIZE ? DEFAULT_PAGESIZE : pageSize;
        this.autoCount = autoCount;
    }

    /**
     * 设置分页类型
     *
     * @param mode  分页对象的类型
     * @return  分页类型
     */
    public Page<E> setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    /**
     * 获取分页对象的类型
     *
     * @return  分页类型
     */
    public Page<E> memoryMode() {
        return setMode(Mode.memory);
    }

    /**
     * 当前页序号.
     */
    public int getPage() {
        return page;
    }

    /**
     * 每页结果数.
     */
    public int getPagesize() {
        return pagesize;
    }

    /**
     * 是否计算总数?
     */
    public boolean getAutoCount() {
        return autoCount;
    }

    /**
     * 总数.
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * 获取分页结果集.
     *
     * @return 当页结果集
     */
    public List<E> getResults() {
        return results;
    }

    /**
     * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置.
     * <p/>
     * 序号从0(猴子...)开始.
     *
     * @return the first result item index of total.
     */
    public int getStart() {
        return (page - 1) * pagesize;
    }

    /**
     * 获取总页数.
     *
     * @return 总页数, 则返回 -1 如果记录数为 0
     */
    public int getTotalPage() {
        if (totalCount <= 0) {
            return DEFAULT_TOTALCOUNT;
        }

        int count = totalCount / pagesize;
        if (totalCount % pagesize > 0) {
            count++;
        }

        return count;
    }

    /**
     * 有下一页?
     */
    public boolean isHasPrePage() {
        return page > 1;
    }

    /**
     * 是最后一页?
     */
    public boolean isHasNextPage() {
        return page < getTotalPage();
    }

    /**
     * 上一页 序号.
     */
    public int getPrePage() {
        return isHasPrePage() ? page - 1 : 1;
    }

    /**
     * 下一页序号.
     */
    public int getNextPage() {
        return isHasNextPage() ? page + 1 : page;
    }

    /**
     * 获取当前分页对象的结果集合的遍历器
     *
     * @return {@link Iterator}遍历器
     */
    public Iterator<E> iterator() {
        return results.iterator();
    }

    /**
     * 获取当前分页对象的model类型
     *
     * @return 当前分页对象的model类型
     */
    public Mode getMode() {
        return this.mode;
    }

}

/**
 * 分页结果接口
 *
 * @param <E> 结果集的类型
 * @author admin
 */
@SuppressWarnings("unused")
interface IPageResult<E> {
    /**
     * 获取总记录数
     *
     * @return 总记录数
     */
    int getTotalCount();

    /**
     * 获取结果集
     *
     * @return 结果集对象
     */
    List<E> getResults();

    /**
     * 获取总页数
     *
     * @return 总页数
     */
    int getTotalPage();

    /**
     * 是否有上一页
     *
     * @return 如果有返回true，否则返回false
     */
    boolean isHasPrePage();

    /**
     * 是否有下一页
     *
     * @return 如果有返回true，否则返回false
     */
    boolean isHasNextPage();

    /**
     * 获取上一页
     *
     * @return 上一页页码
     */
    int getPrePage();

    /**
     * 获取下一页
     *
     * @return 下一页页码
     */
    int getNextPage();

    /**
     * 获取分页对象的缓存类型
     *
     * @return  {@link Page.Mode}
     */
    Page.Mode getMode();
}

/**
 * 分页对象接口2
 *
 * @author admin
 */
@SuppressWarnings("unused")
interface IPageQuery {
    /**
     * 获取当前页
     * @return 当前页
     */
    int getPage();

    /**
     * 获取一页大小
     * @return  一页大小
     */
    int getPagesize();

    /**
     * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置.
     * <p/>
     * 序号从0(猴子...)开始.
     *
     * @return the first result item index of total.
     */
    int getStart();

    /**
     * 是否自动计算总数
     * @return  是否自动计算总数
     */
    boolean getAutoCount();
}

/**
 * 分页工具
 */
@SuppressWarnings("unused")
class MemoryPageUtil {

    /**
     * 根据当前页码、一页大小、结果集合获取结果集
     * @param page  当前页码
     * @param pagesize  一页大小
     * @param data  结果集
     * @param <E>   结果集的类型
     * @return     通过页码和一页大小获取到的结果集
     */
    public static <E> List<E> getPageResults(int page, int pagesize, List<E> data) {
        if (data.size() <= pagesize) {
            return data;
        }

        final int start = (page - 1) * pagesize;
        if (start >= data.size()) {
            return Lists.newArrayList();
        }
        final int end = (start + pagesize) > data.size() ? data.size() : (start + pagesize);

        return data.subList(start, end);
    }
}