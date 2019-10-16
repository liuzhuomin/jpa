package cn.xr.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.googlecode.genericdao.search.InternalUtil;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 数据处理工具类
 * @author liuliuliu
 * @version 1.0
 * 2019/8/7 10:32
 */
@SuppressWarnings("rawtypes")
public final class DataUtil {

    private final static String OBJECT_NOT_NULL = "object must not be null";

    private final static String FILED_NOT_NULL = "filed must not be null";


    /**
     * 判断object是否为基本类型
     *
     * @param object    任意对象
     * @return  是返回true,否则而返回false
     */
    @SuppressWarnings("unused")
    public static boolean isBaseType(Object object) {
        return isBaseType(object.getClass());
    }

    /**
     * 判断此class类型的对象是否是基本数据类型
     *
     * @param classObj  字节码对象
     * @return  如果是基础类型返回true，否则返回false
     */
    public static boolean isBaseType(Class classObj) {
        if (classObj.equals(Integer.class) || classObj.equals(Byte.class)
                || classObj.equals(Long.class) || classObj.equals(Double.class)
                || classObj.equals(Float.class) || classObj.equals(Character.class)
                || classObj.equals(Short.class) || classObj.equals(Boolean.class)
                || classObj.equals(String.class)) {
            return true;
        }
        return false;
    }

    /**
     * java bean 对象转换成map结构
     *
     * @param obj 需要被转换的对象
     * @return
     * @throws Exception
     */
    @SuppressWarnings({"serial","unused"})
    public static Map<String, Object> beanToMap(Object obj)
            throws Exception {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = Maps.newHashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass(), Object.class);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            Method getter = property.getReadMethod();
            @SuppressWarnings("unused")
            /*
             * Class<?> returnType = null; if (getter != null) { returnType =
             * getter.getReturnType(); }
             */
                    Object value = getter != null ? getter.invoke(obj) : null;
            map.put(key, value);
        }
        return map;
    }

    /**
     * map 对象转换成java bean结构
     *
     * @param clazz 需要被转换的对象类型
     * @return 根据字段和字段对应的值生成的Map结构
     * @throws Exception
     */
    public static <T> T mapToBean(Map<String, Object> mapByObj, Class<T> clazz) throws Exception {
        if (mapByObj == null) {
            return null;
        }
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        T object = clazz.newInstance();
        for (Map.Entry<String, Object> entry : mapByObj.entrySet()) {
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (key.equals(entry.getKey())) {
                    Method setter = property.getWriteMethod();
                    Class<?>[] parameterTypes = setter.getParameterTypes();
                    if (setter != null) {
                        Object value = entry.getValue();
                        Class<?> parameterClass = parameterTypes[0];
                        setter.invoke(object, InternalUtil.convertIfNeeded(value, parameterClass));
                    }
                    break;
                }
            }
        }
        return object;
    }

    /**
     * 根据obj和field取到指定field的值
     *
     * @param obj   指定对象
     * @param field 指定field
     * @return 指定字段生成的getter方法在当前对象执行后返回的结果对象
     * @throws Exception
     */
    public static Object invokeGetByObject(Object obj, Field field) throws Exception {
        checkObjectAndField(obj, field);
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.equals(field.getName())) {
                Method getter = property.getReadMethod();
                return getter.invoke(obj);
            }
        }
        return null;
    }

    /**
     * 根据object和field运行setter方法
     *
     * @param obj   指定对象
     * @param field 指定field
     * @return 指定字段生成的setter方法在当前对象执行后返回的结果对象
     * @throws Exception
     */
    public static Object invokeSetByObject(Object obj, Field field, Object... objects) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.equals(field.getName())) {
                Method setter = property.getWriteMethod();
                setter.invoke(obj, objects);
            }
        }
        return null;
    }

    /**
     * 从当前{@link Field}字段中取出他的泛型类型如果他的泛型不是{@link List}的子类,则返回null 如果没有泛型返回null
     *
     * @param field 预期一个{@link Field}类型的字段
     * @return 返回取出的字段的泛型的类型
     */
    @SuppressWarnings("unused")
    public static Class<?> getGenericClassNameByList(Field field) {
        checkField(field);
        if (field.getType().isAssignableFrom(List.class)) {
            ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();
            Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
            Type type = listActualTypeArguments[0];
            return (Class<?>) type;
        }
        return null;
    }

    /**
     * 根据逗号分割，如果没有则返回当前的，如果为空返回空集合
     *
     * @param fields 需要被分割的字符串
     * @return 被分隔后的集合
     */
    public static List<String> split(String fields) {
        if (fields.contains(",")) {
            String[] split = fields.split(",");
            return Arrays.asList(split);
        }
        return Lists.newArrayList(fields);
    }

    /**
     * 检查vlaue是否村子啊与array中,必须重写equals方法
     *
     * @param array 源数组
     * @param value 被检查的类型
     * @param <T>
     * @return
     */
    public static <T> boolean checkFieldIsIgnore(T[] array, T value) {
        if (array != null && array.length > 0) {
            for (Object s : array) {
                if (s.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void checkObjectAndField(Object object, Field field) {
        checkObject(object);
        checkField(field);

    }

    private static void checkField(Field field) {
        if (field == null) {
            throw new RuntimeException(FILED_NOT_NULL);
        }
    }

    private static void checkObject(Object object) {
        if (object == null) {
            throw new RuntimeException(OBJECT_NOT_NULL);
        }
    }


}
