package cn.xr.test;

import cn.xr.utils.DataUtil;
import com.googlecode.genericdao.search.Field;
import com.googlecode.genericdao.search.InternalUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author liuliuliu
 * @version 1.0
 * 2019/8/28 18:37
 */
@RestController
@RequestMapping("/boot")
public class TestController {

    @Autowired
    TestService testService;

    @Autowired
    TestTargetService testTargetService;

    @RequestMapping("/testSome")
    public List<Test> test() throws Exception {
        testTargetService.save(new TestTarget());
        TestTarget testTarget = testTargetService.findAll().get(0);
        Test ceshi = new Test("ceshi" + System.currentTimeMillis());
        ceshi.setTestTarget(testTarget);
        Test save = testService.save(ceshi);
        testTarget.setTest(save);
        testTargetService.update(testTarget);
//
//        List<Test> ceshi1 = testService.query().field(new Field("ceshi",0)).list();
//        for (Test test : ceshi1) {
//            System.out.println(test);
//        }

//        List list = testService.query().field("ceshi","ceshi").field("id","id")
//              /*  .field("testTarget","testTarget")*/.list();
//        Object o = list.get(list.size()-1);
//        System.out.println(o.getClass());
//        Map<String,Object> m=(Map)o;
//        Set<Map.Entry<String, Object>> entries = m.entrySet();
//        for (Map.Entry<String, Object> entry : entries) {
//            System.out.println("entrykey"+ entry.getKey());
//            System.out.println("entryvalue"+ entry.getValue());
//        }
//        Test test = DataUtil.mapToBean((Map) o, Test.class);
//        System.out.println(test.getId());
//        System.out.println(test.getCeshi());
//
//        String s = InternalUtil.paramDisplayString(list);
//        System.out.println("s:"+s);
        return null;
    }
}
