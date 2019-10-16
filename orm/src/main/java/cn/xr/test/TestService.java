package cn.xr.test;

import cn.xr.service.JPAService;

/**
 * @author liuliuliu
 * @version 1.0
 * 2019/8/28 17:14
 */
public interface TestService extends JPAService<Test> {

    void queryAll();

}
