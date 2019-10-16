package cn.xr.model.base;

import cn.xr.model.Treeable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jihy
 * @since 2019-09-10 15:04
 */
@MappedSuperclass
public  abstract class TreeEntity <E extends TreeEntity<E>> extends NameEntity implements Treeable<E> {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  protected E parent;

  @OneToMany(mappedBy = "parent", cascade = { CascadeType.ALL })
  private List<E> children = new ArrayList<E>();

  @Transient
  private Boolean hasChild;

  public TreeEntity() {
  }

  public TreeEntity(String name) {
    super(name);
  }

  @Override
  public E getParent() {
    return parent;
  }

  @Override
  public void setParent(E parent) {
    this.parent = parent;
  }

  @Override
  public List<E> getChildren() {
    return children;
  }

  @Override
  public void setChildren(List<E> children) {
    this.children = children;
  }

  @Override
  public Boolean getHasChild() {
    return hasChild == null ? !getChildren().isEmpty() : hasChild;
  }

  public void setHasChild(Boolean hasChild) {
    this.hasChild = hasChild;
  }

  public boolean isRoot() {
    return this.getParent() == null;
  }

  public boolean isLeaf() {
    return children.isEmpty();
  }

  @SuppressWarnings("unchecked")
  public E addChild(E... entities) {
    for (E e : entities) {
      e.setParent((E) this);
      getChildren().add(e);
    }
    return (E) this;
  }
}
