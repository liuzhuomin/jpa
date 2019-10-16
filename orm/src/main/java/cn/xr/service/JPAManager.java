package cn.xr.service;

import cn.xr.model.base.JPAEntity;
import cn.xr.dao.PersistenceService;
import org.springframework.jdbc.core.JdbcTemplate;


public interface JPAManager<T extends JPAEntity<Long>> extends PersistenceService<T, Long> {

	JdbcTemplate getJdbcTemplate();

}
