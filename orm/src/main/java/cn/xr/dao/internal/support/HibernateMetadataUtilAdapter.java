package cn.xr.dao.internal.support;

import com.googlecode.genericdao.search.Metadata;
import com.googlecode.genericdao.search.MetadataUtil;
import com.googlecode.genericdao.search.hibernate.HibernateEntityMetadata;
import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.persistence.EntityManagerFactory;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Component
public class HibernateMetadataUtilAdapter implements MetadataUtil {

    private SessionFactory sessionFactory;

    private final Logger logger = LoggerFactory.getLogger(HibernateMetadataUtilAdapter.class);

    static MetadataUtil getInstanceForEntityManagerFactory(EntityManagerFactory emf) {
        HibernateEntityManagerFactory hemf = (HibernateEntityManagerFactory) emf;
        return HibernateMetadataUtilAdapter.getInstanceForSessionFactory(hemf.getSessionFactory());
    }

    private static Map<SessionFactory, HibernateMetadataUtilAdapter> map = new HashMap<SessionFactory, HibernateMetadataUtilAdapter>();

    public static MetadataUtil getInstanceForSessionFactory(SessionFactory sessionFactory) {
        HibernateMetadataUtilAdapter instance = map.get(sessionFactory);
        if (instance == null) {
            instance = new HibernateMetadataUtilAdapter();
            instance.sessionFactory = sessionFactory;
            map.put(sessionFactory, instance);
        }
        return instance;
    }

    protected HibernateMetadataUtilAdapter() {
    }

    // --- Public Methods ---

    public Serializable getId(Object entity) {
        if (entity == null)
            throw new NullPointerException("Cannot get ID from null object.");
        return get(entity.getClass()).getIdValue(entity);
    }

    public boolean isId(Class<?> rootClass, String propertyPath) {
        if (propertyPath == null || "".equals(propertyPath))
            return false;
        // with hibernate, "id" always refers to the id property, no matter what
        // that property is named. just make sure the segment before this "id"
        // refers to an entity since only entities have ids.
        if (propertyPath.equals("id")
                || (propertyPath.endsWith(".id") && get(rootClass,
                propertyPath.substring(0, propertyPath.length() - 3))
                .isEntity()))
            return true;

        // see if the property is the identifier property of the entity it
        // belongs to.
        int pos = propertyPath.lastIndexOf(".");
        if (pos != -1) {
            Metadata parentType = get(rootClass, propertyPath.substring(0, pos));
            if (!parentType.isEntity())
                return false;
            return propertyPath.substring(pos + 1).equals(
                    parentType.getIdProperty());
        } else {
            return propertyPath.equals(sessionFactory.getClassMetadata(
                    rootClass).getIdentifierPropertyName());
        }
    }

    public Metadata get(Class<?> entityClass) {
        Assert.notNull(entityClass, "entityClass must not be null.");

        Class<?> unproxied = getUnproxiedClass(entityClass);
        if (unproxied == null) {
            logger.warn("Unable to get unproxied class of {}, maybe the class is not a registered Hibernate entity.", entityClass);
        }
        ClassMetadata cm = sessionFactory.getClassMetadata(unproxied != null ? unproxied : entityClass);
        if (cm == null) {
            throw new IllegalArgumentException("Unable to introspect "
                    + (unproxied != null ? unproxied : entityClass).toString()
                    + ". The class is not a registered Hibernate entity.");
        } else {
            return new HibernateEntityMetadata(sessionFactory, cm, null);
        }
    }

    public Metadata get(Class<?> rootEntityClass, String propertyPath) {
        try {
            Metadata md = get(rootEntityClass);
            if (propertyPath == null || "".equals(propertyPath))
                return md;

            String[] chain = propertyPath.split("\\.");

            for (int i = 0; i < chain.length; i++) {
                md = md.getPropertyType(chain[i]);
            }

            return md;

        } catch (HibernateException ex) {
            throw new PropertyNotFoundException("Could not find property '"
                    + propertyPath + "' on class " + rootEntityClass + ".");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getUnproxiedClass(Class<?> klass) {
        // cm will be null if entityClass is not registered with Hibernate or
        // when
        // it is a Hibernate proxy class (e.x.
        // test.googlecode.genericdao.model.Person_$$_javassist_5).
        // So if a class is not recognized, we will look at superclasses to see
        // if
        // it is a proxy.
        while (sessionFactory.getClassMetadata(klass) == null) {
            klass = klass.getSuperclass();
            if (Object.class.equals(klass))
                return null;
        }

        return (Class<T>) klass;
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getUnproxiedClass(Object entity) {
        return HibernateProxyHelper.getClassWithoutInitializingProxy(entity);
    }

}
