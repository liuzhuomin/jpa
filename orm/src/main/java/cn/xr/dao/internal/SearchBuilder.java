package cn.xr.dao.internal;

import java.util.Collection;
import java.util.List;

import com.googlecode.genericdao.search.Field;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;

/**
 * Page Search Object.
 */
class SearchBuilder {
  private final Search search = new Search();
  private int page = 1;
  private int start = 0;

  private boolean autoCount = true;
  private boolean fromPage = true;
  private int limit = 10;

  /**
   * 获取构建器.
   */
  public static SearchBuilder get() {
    return new SearchBuilder();
  }

  public static SearchBuilder fromPage(int page, int pagesize) {
    return get().setPage(page).setPagesize(pagesize);
  }

  public static SearchBuilder fromRange(int start, int limit) {
    return get().setStart(start).setLimit(limit);
  }

  /**
   * 生成Search对象.
   */
  public Search build() {
    return search;
  }

  public static Search buildDefaultSearch() {
    return SearchBuilder.get().build();
  }

  public boolean isFromPage() {
    return fromPage;
  }

  /**
   * page no. start from 1.
   */
  public SearchBuilder setPage(int page) {
    this.fromPage = true;
    this.page = page;

    search.setPage(page - 1);
    return this;
  }

  /**
   * pagesize.
   * 
   * @param pagesize
   * @return
   */
  public SearchBuilder setPagesize(int pagesize) {
    return setLimit(pagesize);
  }

  public SearchBuilder setPage(int page, int pagesize) {
    this.setPage(page);
    return setPagesize(pagesize);
  }

  public int getPagesize() {
    return getLimit();
  }

  public int getLimit() {
    return limit;
  }

  /**
   * start 从 0 开始
   * 
   * @param firstResult
   * @return
   */
  public SearchBuilder setStart(int firstResult) {
    this.start = firstResult;
    this.fromPage = false;

    search.setFirstResult(firstResult);
    return this;
  }

  /**
   * limit
   * 
   * @param limit
   * @return
   */
  public SearchBuilder setLimit(int limit) {
    this.limit = limit;
    search.setMaxResults(limit);
    return this;
  }

  public SearchBuilder setStart(int start, int limit) {
    this.setStart(start);
    return setLimit(limit);
  }

  public int getPage() {
    if (isFromPage()) {
      return this.page;
    }
    int page = start / limit + 1;
    return page;
  }

  public int getStart() {
    if (!isFromPage()) {
      return this.start;
    }
    int start = (page - 1) * limit;
    return start;
  }

  public boolean isAutoCount() {
    return autoCount;
  }

  public SearchBuilder setAutoCount(boolean autoCount) {
    this.autoCount = autoCount;
    return this;
  }

  // //////////////////////////////////////////////////////////////////////////////
  // proxy methods

  public SearchBuilder setSearchClass(Class<?> searchClass) {
    search.setSearchClass(searchClass);
    return this;
  }

  public SearchBuilder addFilter(Filter filter) {
    search.addFilter(filter);
    return this;
  }

  public SearchBuilder addFilters(Filter... filters) {
    search.addFilters(filters);
    return this;
  }

  public SearchBuilder addFilterEqual(String property, Object value) {
    search.addFilterEqual(property, value);
    return this;
  }

  public SearchBuilder addFilterGreaterOrEqual(String property, Object value) {
    search.addFilterGreaterOrEqual(property, value);
    return this;
  }

  public SearchBuilder addFilterGreaterThan(String property, Object value) {
    search.addFilterGreaterThan(property, value);
    return this;
  }

  public SearchBuilder addFilterIn(String property, Collection<?> value) {
    search.addFilterIn(property, value);
    return this;
  }

  public SearchBuilder addFilterIn(String property, Object... value) {
    search.addFilterIn(property, value);
    return this;
  }

  public SearchBuilder addFilterNotIn(String property, Collection<?> value) {
    search.addFilterNotIn(property, value);
    return this;
  }

  public SearchBuilder addFilterNotIn(String property, Object... value) {
    search.addFilterNotIn(property, value);
    return this;
  }

  public SearchBuilder addFilterLessOrEqual(String property, Object value) {
    search.addFilterLessOrEqual(property, value);
    return this;
  }

  public SearchBuilder addFilterLessThan(String property, Object value) {
    search.addFilterLessThan(property, value);
    return this;
  }

  public SearchBuilder addFilterLike(String property, String value) {
    search.addFilterLike(property, value);
    return this;
  }

  public SearchBuilder addFilterILike(String property, String value) {
    search.addFilterILike(property, value);
    return this;
  }

  public SearchBuilder addFilterNotEqual(String property, Object value) {
    search.addFilterNotEqual(property, value);
    return this;
  }

  public SearchBuilder addFilterNull(String property) {
    search.addFilterNull(property);
    return this;
  }

  public SearchBuilder addFilterNotNull(String property) {
    search.addFilterNotNull(property);
    return this;
  }

  public SearchBuilder addFilterEmpty(String property) {
    search.addFilterEmpty(property);
    return this;
  }

  public SearchBuilder addFilterNotEmpty(String property) {
    search.addFilterNotEmpty(property);
    return this;
  }

  public SearchBuilder addFilterAnd(Filter... filters) {
    search.addFilterAnd(filters);
    return this;
  }

  public SearchBuilder addFilterOr(Filter... filters) {
    search.addFilterOr(filters);
    return this;
  }

  public SearchBuilder addFilterNot(Filter filter) {
    search.addFilterNot(filter);
    return this;
  }

  public SearchBuilder addFilterSome(String property, Filter filter) {
    search.addFilterSome(property, filter);
    return this;
  }

  public SearchBuilder addFilterAll(String property, Filter filter) {
    search.addFilterAll(property, filter);
    return this;
  }

  public SearchBuilder addFilterNone(String property, Filter filter) {
    search.addFilterNone(property, filter);
    return this;
  }

  public SearchBuilder removeFilter(Filter filter) {
    search.removeFilter(filter);
    return this;
  }

  public SearchBuilder removeFiltersOnProperty(String property) {
    search.removeFiltersOnProperty(property);
    return this;
  }

  public SearchBuilder clearFilters() {
    search.clearFilters();
    return this;
  }

  public SearchBuilder addSort(Sort sort) {
    search.addSort(sort);
    return this;
  }

  public SearchBuilder addSorts(Sort... sorts) {
    search.addSorts(sorts);
    return this;
  }

  public SearchBuilder addSortAsc(String property) {
    search.addSortAsc(property);
    return this;
  }

  public SearchBuilder addSortAsc(String property, boolean ignoreCase) {
    search.addSortAsc(property, ignoreCase);
    return this;
  }

  public SearchBuilder addSortDesc(String property) {
    search.addSortDesc(property);
    return this;
  }

  public SearchBuilder addSortDesc(String property, boolean ignoreCase) {
    search.addSortDesc(property, ignoreCase);
    return this;
  }

  public SearchBuilder addSort(String property, boolean desc) {
    search.addSort(property, desc);
    return this;
  }

  public SearchBuilder addSort(String property, boolean desc, boolean ignoreCase) {
    search.addSort(property, desc, ignoreCase);
    return this;
  }

  public SearchBuilder removeSort(Sort sort) {
    search.removeSort(sort);
    return this;
  }

  public SearchBuilder removeSort(String property) {
    search.removeSort(property);
    return this;
  }

  public SearchBuilder clearSorts() {
    search.clearSorts();
    return this;
  }

  public SearchBuilder addField(Field field) {
    search.addField(field);
    return this;
  }

  public SearchBuilder addFields(Field... fields) {
    search.addFields(fields);
    return this;
  }

  public SearchBuilder addField(String property) {
    search.addField(property);
    return this;
  }

  public SearchBuilder addField(String property, String key) {
    search.addField(property, key);
    return this;
  }

  public SearchBuilder addField(String property, int operator) {
    search.addField(property, operator);
    return this;
  }

  public SearchBuilder addField(String property, int operator, String key) {
    search.addField(property, operator, key);
    return this;
  }

  public SearchBuilder removeField(Field field) {
    search.removeField(field);
    return this;
  }

  public SearchBuilder removeField(String property) {
    search.removeField(property);
    return this;
  }

  public SearchBuilder removeField(String property, String key) {
    search.removeField(property, key);
    return this;
  }

  public SearchBuilder clearFields() {
    search.clearFields();
    return this;
  }

  public SearchBuilder setDistinct(boolean distinct) {
    search.setDistinct(distinct);
    return this;
  }

  public SearchBuilder setResultMode(int resultMode) {
    search.setResultMode(resultMode);
    return this;
  }

  public SearchBuilder addFetch(String property) {
    search.addFetch(property);
    return this;
  }

  public SearchBuilder addFetches(String... properties) {
    search.addFetches(properties);
    return this;
  }

  public SearchBuilder removeFetch(String property) {
    search.removeFetch(property);
    return this;
  }

  public SearchBuilder clearFetches() {
    search.clearFetches();
    return this;
  }

  public SearchBuilder clear() {
    search.clear();
    return this;
  }

  public SearchBuilder clearPaging() {
    search.clearPaging();
    return this;
  }

  public SearchBuilder setFilters(List<Filter> filters) {
    search.setFilters(filters);
    return this;
  }

  public SearchBuilder setSorts(List<Sort> sorts) {
    search.setSorts(sorts);
    return this;
  }

  public SearchBuilder setFields(List<Field> fields) {
    search.setFields(fields);
    return this;
  }

  public SearchBuilder setFetches(List<String> fetches) {
    search.setFetches(fetches);
    return this;
  }

}
