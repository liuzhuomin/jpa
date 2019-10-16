package cn.xr.dao.exception;


import cn.xr.model.Idable;

public class PersistenceException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public PersistenceException() {
    super();
  }

  public PersistenceException(Class<?> type, Object id, String action, String msg, Throwable throwable) {
    super(String.format("%s实体{class:%s, id:%s}: %s", action, type.getName(), String.valueOf(id), msg), throwable);
  }

  public PersistenceException(Object entity, String action, String msg, Throwable throwable) {
    this(entity.getClass(), id(entity), action, msg, throwable);
  }

  private static Object id(Object entity) {
    if (entity instanceof Idable) {
      return ((Idable<?>) entity).getId();
    }
    return "?";
  }
}
