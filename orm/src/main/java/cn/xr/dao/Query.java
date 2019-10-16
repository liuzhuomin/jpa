package cn.xr.dao;

import com.googlecode.genericdao.search.Field;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Sort;

import java.util.Collection;
import java.util.List;

/**
 * 查询器接口.
 */
public interface Query<T, RT> extends Resulted<RT> {

    Query<T, RT> searchClass(Class<T> searchClass);

    /**
     * page no. start from 1.
     */
    Query<T, RT> page(int page);

    /**
     * page no. start from 1.
     */
    Query<T, RT> page(int page, int pagesize);

    int thePage();

    /**
     * pagesize.
     */
    Query<T, RT> pagesize(int pagesize);

    int pagesize();

    /**
     * start 从 0 开始
     */
    Query<T, RT> start(int firstResult);

    Query<T, RT> limit(int limit);

    Query<T, RT> start(int start, int limit);

    Query<T, RT> autoCount(boolean autoCount);

    boolean autoCount();

    Query<T, RT> filter(Filter filter);

    Query<T, RT> filters(Filter... filters);

    Query<T, RT> filterEqual(String property, Object value);

    Query<T, RT> filterGreaterOrEqual(String property, Object value);

    Query<T, RT> filterGreaterThan(String property, Object value);

    Query<T, RT> filterIn(String property, Collection<?> value);

    Query<T, RT> filterIn(String property, Object... value);

    Query<T, RT> filterNotIn(String property, Collection<?> value);

    Query<T, RT> filterNotIn(String property, Object... value);

    Query<T, RT> filterLessOrEqual(String property, Object value);

    Query<T, RT> filterLessThan(String property, Object value);

    Query<T, RT> filterLike(String property, String value);

    Query<T, RT> filterILike(String property, String value);

    Query<T, RT> filterNotEqual(String property, Object value);

    Query<T, RT> filterNull(String property);

    Query<T, RT> filterNotNull(String property);

    Query<T, RT> filterEmpty(String property);

    Query<T, RT> filterNotEmpty(String property);

    Query<T, RT> filterAnd(Filter... filters);

    Query<T, RT> filterOr(Filter... filters);

    Query<T, RT> filterNot(Filter filter);

    Query<T, RT> filterSome(String property, Filter filter);

    Query<T, RT> filterAll(String property, Filter filter);

    Query<T, RT> filterNone(String property, Filter filter);

    Query<T, RT> removeFilter(Filter filter);

    Query<T, RT> removeFiltersOnProperty(String property);

    Query<T, RT> clearFilters();

    Query<T, RT> sort(Sort sort);

    Query<T, RT> sorts(Sort... sorts);

    Query<T, RT> sortAsc(String property);

    Query<T, RT> sortAsc(String property, boolean ignoreCase);

    Query<T, RT> sortDesc(String property);

    Query<T, RT> sortDesc(String property, boolean ignoreCase);

    Query<T, RT> sort(String property, boolean desc);

    Query<T, RT> sort(String property, boolean desc, boolean ignoreCase);

    Query<T, RT> removeSort(Sort sort);

    Query<T, RT> removeSort(String property);

    Query<T, RT> clearSorts();

    Query<T, RT> sorts(List<Sort> sorts);

    Query<T, RT> field(Field field);

    Query<T, RT> fields(Field... fields);

    Query<T, RT> field(String property);

    Query<T, RT> field(String property, String key);

    Query<T, RT> field(String property, int operator);

    Query<T, RT> field(String property, int operator, String key);

    Query<T, RT> removeField(Field field);

    Query<T, RT> removeField(String property);

    Query<T, RT> removeField(String property, String key);

    Query<T, RT> fields(List<Field> fields);

    Query<T, RT> clearFields();

    Query<T, RT> distinct(boolean distinct);

    Query<T, RT> resultMode(int resultMode);

    Query<T, RT> fetch(String property);

    Query<T, RT> fetches(String... propeTies);

    Query<T, RT> removeFetch(String property);

    Query<T, RT> filters(List<Filter> filters);

    Query<T, RT> fetches(List<String> fetches);

    Query<T, RT> clearFetches();

    Query<T, RT> clear();

    Query<T, RT> clearPaging();

    List<RT> list();

    ISearch toSearch();
}
