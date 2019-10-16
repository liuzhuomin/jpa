package cn.xr.service;


import cn.xr.model.base.JPAEntity;
import cn.xr.dao.PersistenceService;

public interface JPAService<T extends JPAEntity<Long>> extends PersistenceService<T, Long> {

}
