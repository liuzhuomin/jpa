package cn.xr.dao.internal;

import java.util.Collection;
import java.util.List;


import cn.xr.dao.JPADao;
import cn.xr.dao.Page;
import cn.xr.dao.Query;
import com.googlecode.genericdao.search.Field;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Sort;

/**
 * 查询器默认实现.
 * 
 */
class QueryImpl<T, RT> implements Query<T, RT> {
  private final SearchBuilder _searchBuilder = SearchBuilder.get();
  private final JPADao jpaify;

  public QueryImpl(JPADao jpaify, Class<T> searchClass, Class<RT> resultClass) {
    this.searchClass(searchClass);
    this.jpaify = jpaify;
  }

  public List<RT> list() {
    return jpaify.list(this);
  }

  public Page<RT> page() {
    return jpaify.page(this);
  }

  public RT first() {
    List<RT> list = list();
    if (!list.isEmpty()) {
      return list.get(0);
    }
    return null;
  }

  public RT unique() {
    return jpaify.unique(this);
  }

  public int count() {
    return jpaify.count(this);
  }

  public QueryImpl<T, RT> searchClass(Class<T> searchClass) {
    _searchBuilder.setSearchClass(searchClass);
    return this;
  }

  public QueryImpl<T, RT> page(int page) {
    _searchBuilder.setPage(page);
    return this;
  }

  public QueryImpl<T, RT> page(int page, int pagesize) {
    _searchBuilder.setPage(page, pagesize);
    return this;
  }

  public QueryImpl<T, RT> pagesize(int pagesize) {
    return limit(pagesize);
  }

  public QueryImpl<T, RT> start(int firstResult) {
    _searchBuilder.setStart(firstResult);
    return this;
  }

  public QueryImpl<T, RT> limit(int limit) {
    _searchBuilder.setLimit(limit);
    return this;
  }

  public QueryImpl<T, RT> start(int start, int limit) {
    _searchBuilder.setStart(start, limit);
    return this;
  }

  public QueryImpl<T, RT> autoCount(boolean autoCount) {
    _searchBuilder.setAutoCount(autoCount);
    return this;
  }

  public QueryImpl<T, RT> filter(Filter filter) {
    _searchBuilder.addFilter(filter);
    return this;
  }

  public QueryImpl<T, RT> filters(Filter... filters) {
    _searchBuilder.addFilters(filters);
    return this;
  }

  public QueryImpl<T, RT> filterEqual(String property, Object value) {
    _searchBuilder.addFilterEqual(property, value);
    return this;
  }

  public QueryImpl<T, RT> filterGreaterOrEqual(String property, Object value) {
    _searchBuilder.addFilterGreaterOrEqual(property, value);
    return this;
  }

  public QueryImpl<T, RT> filterGreaterThan(String property, Object value) {
    _searchBuilder.addFilterGreaterThan(property, value);
    return this;
  }

  public QueryImpl<T, RT> filterIn(String property, Collection<?> value) {
    _searchBuilder.addFilterIn(property, value);
    return this;
  }

  public QueryImpl<T, RT> filterIn(String property, Object... value) {
    _searchBuilder.addFilterIn(property, value);
    return this;
  }

  public QueryImpl<T, RT> filterNotIn(String property, Collection<?> value) {
    _searchBuilder.addFilterNotIn(property, value);
    return this;
  }

  public QueryImpl<T, RT> filterNotIn(String property, Object... value) {
    _searchBuilder.addFilterNotIn(property, value);
    return this;
  }

  public QueryImpl<T, RT> filterLessOrEqual(String property, Object value) {
    _searchBuilder.addFilterLessOrEqual(property, value);
    return this;
  }

  public QueryImpl<T, RT> filterLessThan(String property, Object value) {
    _searchBuilder.addFilterLessThan(property, value);
    return this;
  }

  public QueryImpl<T, RT> filterLike(String property, String value) {
    _searchBuilder.addFilterLike(property, value);
    return this;
  }

  public QueryImpl<T, RT> filterILike(String property, String value) {
    _searchBuilder.addFilterILike(property, value);
    return this;
  }

  public QueryImpl<T, RT> filterNotEqual(String property, Object value) {
    _searchBuilder.addFilterNotEqual(property, value);
    return this;
  }

  public QueryImpl<T, RT> filterNull(String property) {
    _searchBuilder.addFilterNull(property);
    return this;
  }

  public QueryImpl<T, RT> filterNotNull(String property) {
    _searchBuilder.addFilterNotNull(property);
    return this;
  }

  public QueryImpl<T, RT> filterEmpty(String property) {
    _searchBuilder.addFilterEmpty(property);
    return this;
  }

  public QueryImpl<T, RT> filterNotEmpty(String property) {
    _searchBuilder.addFilterNotEmpty(property);
    return this;
  }

  public QueryImpl<T, RT> filterAnd(Filter... filters) {
    _searchBuilder.addFilterAnd(filters);
    return this;
  }

  public QueryImpl<T, RT> filterOr(Filter... filters) {
    _searchBuilder.addFilterOr(filters);
    return this;
  }

  public QueryImpl<T, RT> filterNot(Filter filter) {
    _searchBuilder.addFilterNot(filter);
    return this;
  }

  public QueryImpl<T, RT> filterSome(String property, Filter filter) {
    _searchBuilder.addFilterSome(property, filter);
    return this;
  }

  public QueryImpl<T, RT> filterAll(String property, Filter filter) {
    _searchBuilder.addFilterAll(property, filter);
    return this;
  }

  public QueryImpl<T, RT> filterNone(String property, Filter filter) {
    _searchBuilder.addFilterNone(property, filter);
    return this;
  }

  public QueryImpl<T, RT> removeFilter(Filter filter) {
    _searchBuilder.removeFilter(filter);
    return this;
  }

  public QueryImpl<T, RT> removeFiltersOnProperty(String property) {
    _searchBuilder.removeFiltersOnProperty(property);
    return this;
  }

  public QueryImpl<T, RT> clearFilters() {
    _searchBuilder.clearFilters();
    return this;
  }

  public QueryImpl<T, RT> sort(Sort sort) {
    _searchBuilder.addSort(sort);
    return this;
  }

  public QueryImpl<T, RT> sorts(Sort... sorts) {
    _searchBuilder.addSorts(sorts);
    return this;
  }

  public QueryImpl<T, RT> sortAsc(String property) {
    _searchBuilder.addSortAsc(property);
    return this;
  }

  public QueryImpl<T, RT> sortAsc(String property, boolean ignoreCase) {
    _searchBuilder.addSortAsc(property, ignoreCase);
    return this;
  }

  public QueryImpl<T, RT> sortDesc(String property) {
    _searchBuilder.addSortDesc(property);
    return this;
  }

  public QueryImpl<T, RT> sortDesc(String property, boolean ignoreCase) {
    _searchBuilder.addSortDesc(property, ignoreCase);
    return this;
  }

  public QueryImpl<T, RT> sort(String property, boolean desc) {
    _searchBuilder.addSort(property, desc);
    return this;
  }

  public QueryImpl<T, RT> sort(String property, boolean desc, boolean ignoreCase) {
    _searchBuilder.addSort(property, desc, ignoreCase);
    return this;
  }

  public QueryImpl<T, RT> removeSort(Sort sort) {
    _searchBuilder.removeSort(sort);
    return this;
  }

  public QueryImpl<T, RT> removeSort(String property) {
    _searchBuilder.removeSort(property);
    return this;
  }

  public QueryImpl<T, RT> clearSorts() {
    _searchBuilder.clearSorts();
    return this;
  }

  public QueryImpl<T, RT> field(Field field) {
    _searchBuilder.addField(field);
    return this;
  }

  public QueryImpl<T, RT> fields(Field... fields) {
    _searchBuilder.addFields(fields);
    return this;
  }

  public QueryImpl<T, RT> field(String property) {
    _searchBuilder.addField(property);
    return this;
  }

  public QueryImpl<T, RT> field(String property, String key) {
    _searchBuilder.addField(property, key);
    return this;
  }

  public QueryImpl<T, RT> field(String property, int operator) {
    _searchBuilder.addField(property, operator);
    return this;
  }

  public QueryImpl<T, RT> field(String property, int operator, String key) {
    _searchBuilder.addField(property, operator, key);
    return this;
  }

  public QueryImpl<T, RT> removeField(Field field) {
    _searchBuilder.removeField(field);
    return this;
  }

  public QueryImpl<T, RT> removeField(String property) {
    _searchBuilder.removeField(property);
    return this;
  }

  public QueryImpl<T, RT> removeField(String property, String key) {
    _searchBuilder.removeField(property, key);
    return this;
  }

  public QueryImpl<T, RT> clearFields() {
    _searchBuilder.clearFields();
    return this;
  }

  public QueryImpl<T, RT> distinct(boolean distinct) {
    _searchBuilder.setDistinct(distinct);
    return this;
  }

  public QueryImpl<T, RT> resultMode(int resultMode) {
    _searchBuilder.setResultMode(resultMode);
    return this;
  }

  public QueryImpl<T, RT> fetch(String property) {
    _searchBuilder.addFetch(property);
    return this;
  }

  public QueryImpl<T, RT> fetches(String... propeTies) {
    _searchBuilder.addFetches(propeTies);
    return this;
  }

  public QueryImpl<T, RT> removeFetch(String property) {
    _searchBuilder.removeFetch(property);
    return this;
  }

  public QueryImpl<T, RT> clearFetches() {
    _searchBuilder.clearFetches();
    return this;
  }

  public QueryImpl<T, RT> clear() {
    _searchBuilder.clear();
    return this;
  }

  public QueryImpl<T, RT> clearPaging() {
    _searchBuilder.clearPaging();
    return this;
  }

  public QueryImpl<T, RT> filters(List<Filter> filters) {
    _searchBuilder.setFilters(filters);
    return this;
  }

  public QueryImpl<T, RT> sorts(List<Sort> sorts) {
    _searchBuilder.setSorts(sorts);
    return this;
  }

  public QueryImpl<T, RT> fields(List<Field> fields) {
    _searchBuilder.setFields(fields);
    return this;
  }

  public QueryImpl<T, RT> fetches(List<String> fetches) {
    _searchBuilder.setFetches(fetches);
    return this;
  }

  public ISearch toSearch() {
    return this._searchBuilder.build();
  }

  public boolean autoCount() {
    return this._searchBuilder.isAutoCount();
  }

  public int thePage() {
    return this._searchBuilder.getPage();
  }

  public int pagesize() {
    return this._searchBuilder.getPagesize();
  }

}