package cn.xr.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * chat的枚举接口父类，为了mybatis的类型处理
 *
 * @author liuliuliu
 * @since 2019/12/11
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public interface ChatEnumInterface<E, V> {
    /**
     * 获取显示value
     *
     * @return 显示的value
     */
    @JsonValue
    V getViewValue();
    /**
     * 设置显示value
     *
     * @param v 任意类型
     */
    void setViewValue(V v);

    /**
     * 获取枚举默认的值
     *
     * @return 枚举默认代表的值
     */
    E getValue();

    /**
     * 设置枚举默认的值
     *
     * @param value 需要设置的值
     */
    void setValue(E value);

}
