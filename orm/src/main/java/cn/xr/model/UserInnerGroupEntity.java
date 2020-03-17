package cn.xr.model;

import cn.xr.model.base.NameEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "user_inner_group", indexes = {@Index(columnList = "userId")})
@Data
public class UserInnerGroupEntity extends NameEntity implements Orderable {

    @Column(columnDefinition = " bigint(20) Comment '分组创建者id，关联users的主键'", nullable = false)
    private Long userId;

    @Column(columnDefinition = " int(20) Comment '分组索引排序从0-n'", nullable = false)
    private long orderIndex;

}
