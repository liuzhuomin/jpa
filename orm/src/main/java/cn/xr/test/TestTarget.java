package cn.xr.test;

import cn.xr.annotaions.LogModel;
import cn.xr.model.base.JPAEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

/**
 * @author liuliuliu
 * @version 1.0
 * 2019/10/9 15:32
 */
@Entity
@Data
@LogModel
public class TestTarget extends JPAEntity<Long> {

    @OneToOne(fetch = FetchType.LAZY)
    private Test test;

}
