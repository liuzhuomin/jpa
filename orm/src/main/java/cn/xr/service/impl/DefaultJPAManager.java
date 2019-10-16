package cn.xr.service.impl;

import cn.xr.model.base.JPAEntity;
import cn.xr.dao.AbstractPersistenceService;
import cn.xr.service.JPAManager;
import org.springframework.jdbc.core.JdbcTemplate;

class DefaultJPAManager<T extends JPAEntity<Long>> extends AbstractPersistenceService<T, Long> implements JPAManager<T> {

	public DefaultJPAManager(Class<T> entityClass) {
		super(entityClass);
	}

	@Override
	public JdbcTemplate getJdbcTemplate() {
		return jpaDao.getJdbcTemplate();
	}
}
