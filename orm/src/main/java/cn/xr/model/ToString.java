package cn.xr.model;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

@SuppressWarnings("unused")
public abstract interface ToString
{
  public abstract Objects.ToStringHelper toStringHelper();
}