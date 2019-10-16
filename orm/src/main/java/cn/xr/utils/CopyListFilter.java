package cn.xr.utils;

/**
     * 拷贝的过滤器
     * @param <T>   目标类型
     * @param <S>    源类型
     */
    public interface CopyListFilter<S, T> {
        /**
         * 从源拷贝到目标
         * @param source    源对象
         * @param target    目标对象
         * @return  返回拷贝成功后的目标对象
         */
       T copy(S source, T target);
    }
