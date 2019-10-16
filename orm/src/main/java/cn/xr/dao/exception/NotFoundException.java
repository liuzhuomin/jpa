package cn.xr.dao.exception;

public class NotFoundException extends PersistenceException {
  private static final long serialVersionUID = 1L;
  private final Class<?> type;
  private final Object id;

  public NotFoundException(Class<?> type, Object id, Throwable throwable) {
    super(type, id, "获取", "实体不存在!", throwable);
    this.type = type;
    this.id = id;
  }


  public Class<?> getType() {
    return type;
  }

  public Object getId() {
    return id;
  }
}
