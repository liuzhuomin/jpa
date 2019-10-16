package cn.xr.model;

import java.util.Date;

public abstract interface LifecycleTrackable
{
  public abstract Date getCreatedAt();

  public abstract Date getUpdatedAt();

  public abstract Date getInvisibleAt();
}