package cn.xr.test;

import cn.xr.service.JPAManager;
import cn.xr.service.support.DefaultJPAService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuliuliu
 * @version 1.0
 * 2019/8/28 17:14
 */
@Service
public class TestServiceImpl extends DefaultJPAService<Test> implements TestService {

    private JPAManager<Test> testJPAManager;

    public TestServiceImpl() {
        super(Test.class);
    }

    @Override
    protected void initManager() {
        super.initManager();
        testJPAManager = getJPAManagerFactory().getJPAManager(Test.class);
    }

    @Override
    public void queryAll() {
        List<Test> all = testJPAManager.findAll();
        System.out.println("all:" + all);
    }


}
