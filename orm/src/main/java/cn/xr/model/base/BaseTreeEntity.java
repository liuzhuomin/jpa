/**
 *
 */
package cn.xr.model.base;

import cn.xr.model.Treeable;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonManagedReference;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础树结构类
 * @author liuliuliu
 */
@MappedSuperclass
@SuppressWarnings({"serial", "unused"})
public abstract class BaseTreeEntity<E extends BaseTreeEntity<E>> extends BaseEntity implements Treeable<E> {

    @ManyToOne(fetch = FetchType.LAZY)
    private E parent;

    @OneToMany(mappedBy = "parent", cascade = {CascadeType.ALL})
    private List<E> children = new ArrayList<E>();

    private Boolean hasChild;

    @XmlTransient
    @JsonBackReference
    public E getParent() {
        return parent;
    }

    public void setParent(E parent) {
        this.parent = parent;
    }

    @JsonManagedReference
    public List<E> getChildren() {
        return children;
    }

    public void setChildren(List<E> children) {
        this.children = children;
    }

    public Boolean getHasChild() {
        return hasChild == null ? !getChildren().isEmpty() : hasChild;
    }

    public void setHasChild(Boolean hasChild) {
        this.hasChild = hasChild;
    }
}
