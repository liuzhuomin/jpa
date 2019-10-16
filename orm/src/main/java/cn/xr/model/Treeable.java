package cn.xr.model;

import java.util.List;

public abstract interface Treeable<E>
{
  public abstract E getParent();

  public abstract void setParent(E paramE);

  public abstract List<E> getChildren();

  public abstract void setChildren(List<E> paramList);

  public abstract Boolean getHasChild();
}