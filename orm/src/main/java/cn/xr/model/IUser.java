package cn.xr.model;

/**
 * @author Jihy
 * @since 2019-09-10 14:55
 */
public interface IUser extends Idable, Nameable {

//    public void setId(Long id);

    public void setName(String name);

    public String getLoginName();

    public void setLoginName(String loginName);

    public String getPassword();

    public void setPassword(String password);

    public String accountEnabled();
}
