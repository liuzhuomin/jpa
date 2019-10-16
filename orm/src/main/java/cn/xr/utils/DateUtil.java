package cn.xr.utils;

import com.google.common.collect.Lists;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <B>Title:</B>DateUtil.java</br>
 * <B>Description:</B> 时间工具类 </br>
 * <B>Copyright: </B>Copyright (c) 2019 </br>
 *
 * @author liuliuliu
 * @version 1.0
 *  2019年3月27日
 */
public final class DateUtil {

    private static final ThreadLocal<List<SimpleDateFormat>> LOCAL = new ThreadLocal<List<SimpleDateFormat>>() {

        @Override
        protected List<SimpleDateFormat> initialValue() {
            return Lists.newArrayList(new SimpleDateFormat(DEFAULT_STR_STYLE));
        }
    };

    public static final String DEFAULT_STR_STYLE = "yyyy-MM-dd HH:mm:ss";

    /**
     * {@link Date}转换成{@link String}类型，默认的时间格式为:{@link DateUtil#DEFAULT_STR_STYLE}
     *
     * @param date {@link Date} 时间对象
     * @return {@link DateUtil#DEFAULT_STR_STYLE}格式的时间字符串
     */
    public static String format(Date date) {
        return getLocal().format(date);
    }

    /**
     * {@link Date}转换成指定时间格式@link String}类型
     *
     * @param date 时间对象
     * @param pattern 时间格式
     * @return  指定时间格式的时间字符串
     */
    public static String format(Date date, String pattern) {
        return getLocal(pattern).format(date);
    }

    /**
     * {@link String}时间字符串转换成{@link Date}类型，默认的时间格式为:{@link DateUtil#DEFAULT_STR_STYLE}
     *
     * @param dateStr {@link String} 时间字符串对象
     * @return {@link Date} 时间对象
     * @throws ParseException 时间字符串解析失败，可能会抛出异常
     */
    public static Date parse(String dateStr) throws ParseException {
        return getLocal().parse(dateStr);
    }

    /**
     * 获取当前存储的时间格式，如果传递了参数patterns，则按照patterns去查询。
     * 否则按照默认的{@link DateUtil#DEFAULT_STR_STYLE}去查询。
     * @param patterns  时间格式
     * @return {@link SimpleDateFormat}时间格式转换对象
     */
    private static synchronized SimpleDateFormat getLocal(String... patterns) {
        List<SimpleDateFormat> simpleDateFormats = LOCAL.get();
        String currentPatterns;
        if (patterns != null && patterns.length > 0) {
            currentPatterns = patterns[0];
        } else {
            currentPatterns = DEFAULT_STR_STYLE;
        }
        for (int i = 0; i < simpleDateFormats.size(); i++) {
            SimpleDateFormat simpleDateFormat = simpleDateFormats.get(i);
            if (simpleDateFormat.toPattern().equals(currentPatterns)) {
                return simpleDateFormat;
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(currentPatterns);
        simpleDateFormats.add(simpleDateFormat);
        return simpleDateFormat;
    }
}
