package cn.xr.model.base;

import cn.xr.model.IUser;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 用户.
 */
@Entity
@Table(name = "tu_user")
@XmlRootElement
public class User extends JPAEntity implements IUser {

    public void setId(Long id) {
    }

    public void setName(String name) {

    }

    public String getLoginName() {
        return null;
    }

    public void setLoginName(String loginName) {

    }

    public String getPassword() {
        return null;
    }

    public void setPassword(String password) {

    }

    public String accountEnabled() {
        return null;
    }

    public String getName() {
        return null;
    }

}
