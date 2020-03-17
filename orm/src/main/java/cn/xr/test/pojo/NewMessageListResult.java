/**
 * Copyright 2020 bejson.com
 */
package cn.xr.test.pojo;

import cn.xr.model.base.JPAEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity(name = "new_message_list_result")
@Table(name = "new_message_list_result")
@Data
public class NewMessageListResult extends JPAEntity<Long> {
    private String channel;
    private String num;

    @OneToMany(targetEntity = NewMessageList.class, mappedBy = "newMessageListResult", cascade = {CascadeType.ALL})
    private List<NewMessageList> list;

    @OneToOne
    private NewMessageUsualResult newMessageUsualResult;


}