package cn.xr.dao.internal.support;

import com.googlecode.genericdao.search.MetadataUtil;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

public class MetadataUtilAdapter {

    public static MetadataUtil getInstanceForEntityManagerFactory(EntityManagerFactory emf) {
        EntityManagerFactory underlied = getUnderliedEntityManagerFactory(emf);
        if (underlied instanceof HibernateEntityManagerFactory) {
            return HibernateMetadataUtilAdapter.getInstanceForEntityManagerFactory(underlied);
        }
        throw new UnsupportedOperationException("目前只支持HibernateEntityManagerFactory");
    }

    public static DataSource getDataSource(EntityManagerFactory emf) {
        EntityManagerFactory underlied = getUnderliedEntityManagerFactory(emf);
        if (underlied instanceof HibernateEntityManagerFactory) {
            return ((HibernateEntityManagerFactory) underlied).unwrap(DataSource.class);
        }
        throw new UnsupportedOperationException("目前只支持HibernateEntityManagerFactory");
    }

    private static EntityManagerFactory getUnderliedEntityManagerFactory(EntityManagerFactory emf) {
        return emf;
    }

    /**
     * 按照实体类类型获取表名称
     * @param em EntityManager对象
     * @param clazz 类型
     * @return  此实体类对应的数据库的名称
     */
    public static String getTableName(EntityManager em, Class<?> clazz) {
        EntityManagerFactory entityManagerFactory = em.getEntityManagerFactory();
        SessionFactoryImpl sessionFactory = (SessionFactoryImpl)entityManagerFactory.unwrap(SessionFactory.class);
        Map<String, EntityPersister> persisterMap = sessionFactory.getEntityPersisters();
        for(Map.Entry<String,EntityPersister> entity : persisterMap.entrySet()){
            Class targetClass = entity.getValue().getMappedClass();
            if(targetClass.equals(clazz)){
                SingleTableEntityPersister persister = (SingleTableEntityPersister)entity.getValue();
                return persister.getTableName();//Entity对应的表的英文名s
            }
        }
        throw new RuntimeException("can not found the table");
    }
}
