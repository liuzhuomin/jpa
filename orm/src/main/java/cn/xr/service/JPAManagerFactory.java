package cn.xr.service;


import cn.xr.model.base.JPAEntity;


public interface JPAManagerFactory {
	<T extends JPAEntity<Long>> JPAManager<T> getJPAManager(Class<T> entityClass);
}
