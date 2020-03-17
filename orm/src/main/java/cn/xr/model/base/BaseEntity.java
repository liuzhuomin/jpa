package cn.xr.model.base;

import cn.xr.model.Nameable;
import com.google.common.base.Objects;

import javax.persistence.MappedSuperclass;

/**
 * 实体基类.
 *
 * @author liuliuliu
 */
@MappedSuperclass
@SuppressWarnings({"serial","unused"})
public abstract class BaseEntity extends JPAEntity<Long> implements Nameable {

    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", getId()).add("name2", name).toString();
    }
}
