package cn.xr.model.base;


import cn.xr.model.Idable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * JPA 实体基类.
 * @param <ID> id的类型
 * @author liuliuliu
 */
@MappedSuperclass
@SuppressWarnings({"serial","unused"})
public abstract class JPAEntity<ID extends Serializable> implements Idable<ID>, Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected ID id;

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

	public boolean getIsNew() {
		return id == null;
	}

	public JPAEntity() {}

	public JPAEntity(ID id){
		this.id=id;
	}
}
