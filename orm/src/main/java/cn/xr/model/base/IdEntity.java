package cn.xr.model.base;

import com.google.common.base.Objects;

/**
 * @author Jihy
 * @since 2019-09-10 14:46
 */
public abstract class IdEntity extends JPAEntity<Long> {

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("id", getId()).toString();
  }
}
