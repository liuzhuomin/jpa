package cn.xr.model;

/**
 * 用于在好友申请展示来源用
 * 0-数字交换
 * <p>
 * 1-附近的人（智能雷达、附近的人）
 * <p>
 * 2-名片分享（点击分享至聊天页面的名片分享加好友）
 * <p>
 * 3-扫描二维码、
 * <p>
 * 4-搜索（通过精准搜索进入名片详情页）
 * <p>
 * 5-热门内容（通过内容进入详情页递名片）
 * <p>
 * 6-群聊（通过聊天群进入详情页加递名片)
 * <p>
 * 7-项目（通过项目进入详情页）
 * <p>
 * 8-商铺（通过商铺首页进入详情页）
 * <p>
 * 9-雷达（雷达加好友）
 * <p>
 * 10-脉圈
 * <p>
 * 11-电话簿
 * <p>
 * 12-活动
 *
 * @author liuliuliu
 * @since 2019/11/30
 */
@SuppressWarnings("unused")
public enum UserApplySourceFrom implements ChatEnumInterface<String, Integer> {
    /**
     * 0-数字交换
     */
    NUMBER_SWITCH(0, "数字交换"),
    /**
     * 1附近的人（智能雷达、附近的人）
     */
    NEAR_MAN(1, "附近的人"),
    /**
     * 2-名片分享（点击分享至聊天页面的名片分享加好友）
     */
    SQUARE(2, "名片分享"),
    /**
     * 3-扫描二维码、
     */
    SCAN_QR_CODE(3, "扫描二维码"),
    /**
     * 4-搜索（通过精准搜索进入名片详情页）
     */
    ACCURATE_SEARCH(4, "搜索"),
    /**
     * 5-热门内容（通过内容进入详情页递名片）
     */
    HOT_CONTENT(5, "热门内容"),
    /**
     * 6-群聊（通过聊天群进入详情页加递名片)
     */
    GROUP_CHAT(6, "群聊"),
    /**
     * 7-项目（通过项目进入详情页）
     */
    PROJECT(7, "项目"),
    /**
     * 8-商铺（通过商铺首页进入详情页）
     */
    SHOP(8, "商铺"),
    /**
     * 9-雷达（雷达加好友）
     */
    RADAR(9, "雷达"),
    CIRCLE(10, "脉圈"),
    CONTACTS(11, "电话簿"),
    ACTIVITY(12, "活动");

    private Integer viewValue;
    private String value;

    UserApplySourceFrom(Integer viewValue, String value) {
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
