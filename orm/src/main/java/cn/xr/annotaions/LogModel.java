package cn.xr.annotaions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在类上加此注解会在修改时对这个实体类的操作做记录
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface LogModel {
    /**
     * 默认为true代表记录
     * @return  true/false
     */
    boolean value() default  true;
}
