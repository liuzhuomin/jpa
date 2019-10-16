package cn.xr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据源配置
 * @author liuliuliu
 * @version 1.0
 * 2019/10/12 16:57
 */
@Configuration
public class DataSourceConfig {

//    @Bean
//    public DriverManagerDataSource driverManagerDataSource(){
//        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
//        driverManagerDataSource.setDriverClassName("");
//        driverManagerDataSource.setCatalog("");
//        driverManagerDataSource.setUrl("");
//        driverManagerDataSource.setUsername("");
//        driverManagerDataSource.setPassword("");
//        return driverManagerDataSource;
//    }
}

