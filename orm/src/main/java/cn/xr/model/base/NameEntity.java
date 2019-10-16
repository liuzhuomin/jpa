package cn.xr.model.base;

import cn.xr.model.Nameable;
import com.google.common.base.Objects;
import com.sun.istack.internal.NotNull;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class NameEntity extends JPAEntity<Long> implements Nameable {

    /**
     * 名称.
     */
    @Column(nullable = false, length = 100)
    @NotNull
    private String name;

    /**
     * 描述.
     */
    @Column(length = 2000)
    private String description;

    public NameEntity() {
    }

    public NameEntity(String name) {
        this.name = name;
    }

    public NameEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", getId()).add("name", name).toString();
    }
}
