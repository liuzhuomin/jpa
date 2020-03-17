package cn.xr.model;

import cn.xr.model.base.JPAEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 申请记录实体
 *
 * @author liuliuliu
 * @since 2020/3/16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "user_contacts_apply",indexes = {
        @Index(columnList="sendUserId"),
        @Index(columnList="receiveUserId") })
@Entity
public class UserContactsApplyEntity extends JPAEntity<Long> {

    @Column(columnDefinition = " bigint(20) Comment '发起用户id'", nullable = false)
    private Long sendUserId;

    @Column(columnDefinition = " bigint(20) Comment '接收用户id'", nullable = false)
    private Long receiveUserId;

    @Column(columnDefinition = " varchar(255) Comment '申请中、已通过 、已拒绝 、已过期'", nullable = false)
    private ApplyType state;

    @Column(columnDefinition = "varchar(20) Comment '申请来源'", nullable = false)
    private UserApplySourceFrom userApplySourceFrom;

    @Column(columnDefinition = " TEXT Comment '申请的内容相当于打招呼语'")
    private String content;

    @Column(columnDefinition = " bigint(255) Comment '好友分组实体的id'")
    private Long groupId;

    @Column(columnDefinition = " varchar(255) Comment '开放自己的卡片id集合的json'")
    private String publicCardIds;

}
