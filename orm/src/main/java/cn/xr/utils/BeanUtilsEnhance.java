package cn.xr.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class BeanUtilsEnhance {

	private final static Logger logger = Logger.getLogger(BeanUtilsEnhance.class.getName());

	/**
	 * <li>1.从source拷贝所有继承自{@link Date}类型的字段到目标对象(默认采用{@link DateUtil#DEFAULT_STR_STYLE}格式)
	 * <li>2.从source拷贝所有String类型的如果跟target属性名一致,按照{@link DateUtil#DEFAULT_STR_STYLE}格式解析到target中
	 * <li>3.不拷贝除了1、2 所述以外的字段
	 *
	 * @param source 源对象
	 * @param target 目标对象
	 */
	public static void copyDateFiled(Object source, Object target) {
		copyDateFiled(source, target, false);
	}

	/**
	 * <li>1.从source拷贝所有继承自{@link Date}类型的字段到目标对象(默认采用{@link DateUtil#DEFAULT_STR_STYLE}格式)
	 * <li>2.从source拷贝所有String类型的如果跟target属性名一致,按照{@link DateUtil#DEFAULT_STR_STYLE}格式解析到target中
	 * <li>3.默认拷贝其他字段，依然有可能会发生无限递归
	 * <li>4.{@code ignoreFields}中的忽略属性比1、2所述规则更加优先，会导致1，2所述无法拷贝
	 *
	 * @param source       目标
	 * @param target       源
	 * @param ignoreFields 忽略字段
	 */
	public static void copyDateFiledEnhance(Object source, Object target, String... ignoreFields) {
		copyDateFiled(source, target, true, ignoreFields);
	}

	/**
	 * <ul>
	 * <li>1.从source拷贝所有基础类型的字段（包含String）到target对象，详情见{@link DataUtil#isBaseType(Class)}函数
	 * <li>2.从source拷贝所有继承自{@link Date}类型的字段到目标对象(默认采用{@link DateUtil#DEFAULT_STR_STYLE}格式)
	 * <li>3.从source拷贝所有String类型的如果跟target属性名一致,按照{@link DateUtil#DEFAULT_STR_STYLE}格式解析到target中
	 * </ul>
	 *
	 * @param source 源
	 * @param target 目标
	 */
	public static void copyPropertiesEnhance(Object source, Object target) {

		checkNotNull(source, target);

		List<String> ignore = Lists.newArrayList();
		Field[] declaredFields = source.getClass().getDeclaredFields();
		for (Field filed : declaredFields) {
			Class<?> type = filed.getType();
			String name = filed.getName();
			if (!type.isAssignableFrom(Date.class)) {
				boolean baseType = DataUtil.isBaseType(type);
				if (!baseType) {
					ignore.add(name);
				}
			}
		}
		synchronized (Byte.class) {
			copyDateFiledEnhance(source, target, ignore.toArray(new String[0]));
		}
	}

	/**
	 * 从源对象拷贝所有基础类型的字段到目标对象,详情见{@link DataUtil#isBaseType(Class)}函数
	 *
	 * @param source 源
	 * @param target 目标
	 */
	public static void copyProperties(Object source, Object target) {
		checkNotNull(source, target);
		List<String> ignore = Lists.newArrayList();
		Field[] declaredFields = source.getClass().getDeclaredFields();
		for (Field filed : declaredFields) {
			boolean baseType = DataUtil.isBaseType(filed.getType());
			if (!baseType) {
				ignore.add(filed.getName());
			}
		}
		synchronized (Byte.class) {
			BeanUtils.copyProperties(source, target, ignore.toArray(new String[0]));
		}
	}

	/**
	 * 拷贝{@code sources}中的每个对象到一个新集合中，{@code targetClazz}为新集合的泛型，{@param ignoreFields}为忽略属性
	 * 底层采用{@link BeanUtils#copyProperties(Object, Object, String...)}有可能发生无限递归，
	 * 如果希望采用自定义的copy{@link BeanUtilsEnhance#copyList(List, Class, CopyListFilter)}
	 *
	 * @param sources      源
	 * @param targetClazz  目标集合的类型
	 * @param ignoreFields 忽略字段，可变参数
	 * @param              <T> 目标的类型
	 * @param              <Z> 源的类型
	 * @return 最终返回目标类型的List {@code  List<T>}
	 */
	public static <T, Z> List<T> copyList(List<Z> sources, Class<T> targetClazz, final String... ignoreFields) {
		return copyList(sources, targetClazz, (source, target) -> {
            BeanUtils.copyProperties(source, target, ignoreFields);
            return target;
        });
	}

	/**
	 * 拷贝sources中的每个对象到一个新集合中，targetClazz为新集合的泛型
	 *
	 * @param sources        源
	 * @param targetClazz    目标集合的类型
	 * @param copyListFilter 自定义拷贝函数
	 * @param                <T> 目标类型
	 * @param                <S> 源类型
	 * @return 源类型的List
	 */
	public static <T, S> List<T> copyList(List<S> sources, Class<T> targetClazz, CopyListFilter<S, T> copyListFilter) {
		List<T> result = Lists.newArrayList();
		if (CollectionUtils.isEmpty(sources)) {
			return result;
		}
		for (S s : sources) {
			T t = null;
			try {
				t = targetClazz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			result.add(copyListFilter.copy(s, t));
		}
		return result;
	}



	private static void copyDateFiled(Object source, Object target, boolean copyElse, String... ignoreFields) {

		checkNotNull(source, target);

		Map<String, Object> dataFields = Maps.newHashMap();
		Map<String, Object> stringFields = Maps.newHashMap();
		Field[] declaredFields = source.getClass().getDeclaredFields();
		for (Field filed : declaredFields) {
			Class<?> type = filed.getType();
			if (DataUtil.checkFieldIsIgnore(ignoreFields, filed.getName())) {
				continue;
			}
			if (type.isAssignableFrom(Date.class)) {
				putvalue(source, dataFields, filed);
			} else if (type == String.class) {
				putvalue(source, stringFields, filed);
			}
		}

		List<String> ignoreFieldList = null;
		if (copyElse) {
			ignoreFieldList = Lists.newArrayList();
		}
		declaredFields = target.getClass().getDeclaredFields();
		for (Field filed : declaredFields) {
			String name = filed.getName();
			Object value = dataFields.get(name);
			Object strValue = null;
			boolean dateValueIsNull;
			if (dateValueIsNull = value == null) {
				strValue = stringFields.get(name);
			}

			Class<?> type = filed.getType();
			if (!dateValueIsNull && type == String.class) {
				try {
					if (copyElse) {
						ignoreFieldList.add(name);
					}
					DataUtil.invokeSetByObject(target, filed,
							DateUtil.format((Date) value, DateUtil.DEFAULT_STR_STYLE));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (strValue != null && type.isAssignableFrom(Date.class)) {
				try {
					if (copyElse) {
						ignoreFieldList.add(name);
					}
					DataUtil.invokeSetByObject(target, filed, DateUtil.parse(strValue.toString()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (copyElse) {
			if (ignoreFields != null && ignoreFields.length > 0) {
				Collections.addAll(ignoreFieldList, ignoreFields);
			}
			synchronized (Byte.class) {
				String[] strings = ignoreFieldList.toArray(new String[0]);
				BeanUtils.copyProperties(source, target, strings);
			}
		}

	}

	private static void putvalue(Object source, Map<String, Object> dataFields, Field filed) {
		try {
			dataFields.put(filed.getName(), DataUtil.invokeGetByObject(source, filed));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void checkNotNull(Object source, Object target) {
		Assert.notNull(source, "source must not be null");
		Assert.notNull(target, "target must not be null");
	}

}
