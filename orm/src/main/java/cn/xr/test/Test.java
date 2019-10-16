package cn.xr.test;

import cn.xr.annotaions.LogModel;
import cn.xr.model.base.JPAEntity;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import java.util.Objects;

/**
 * @author liuliuliu
 * @version 1.0
 * 2019/8/28 17:08
 */
@Entity
@LogModel
@Data
public class Test extends JPAEntity<Long> {

    private String ceshi;

    @OneToOne(fetch = FetchType.LAZY)
    private TestTarget testTarget;

    public Test() {
    }

    public Test(String ceshi) {
        super();
        this.ceshi = ceshi;
    }

    public String getCeshi() {
        return ceshi;
    }

    public void setCeshi(String ceshi) {
        this.ceshi = ceshi;
    }

    @Override
    public String toString() {
        return ObjectUtils.identityToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Test test = (Test) o;
        return Objects.equals(ceshi, test.ceshi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ceshi);
    }
}
