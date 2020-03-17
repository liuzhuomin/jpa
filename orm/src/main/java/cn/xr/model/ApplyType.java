package cn.xr.model;

/**
 * 申请状态
 *
 * @author liuliuliu
 * @since 2019/11/30
 */
public enum ApplyType implements ChatEnumInterface<String, Integer> {

    /**
     * 已通过
     */
    AGREED(0, "已通过"),
    /**
     * 已拒绝
     */
    REFUSE(1, "已拒绝"),

    /**
     * 已过期
     */
    OVERDUE(2, "已过期"),

    /**
     * 申请中
     */
    APPLYING(3, "申请中");

    private Integer viewValue;
    private String value;

    ApplyType(Integer viewValue, String value) {
        this.viewValue = viewValue;
        this.value = value;
    }

    @Override
    public Integer getViewValue() {
        return viewValue;
    }

    @Override
    public void setViewValue(Integer viewValue) {
        this.viewValue = viewValue;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
