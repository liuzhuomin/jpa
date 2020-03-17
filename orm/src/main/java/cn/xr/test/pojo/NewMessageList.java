/**
 * Copyright 2020 bejson.com
 */
package cn.xr.test.pojo;

import cn.xr.model.base.JPAEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name = "new_message_list")
@Table(name = "new_message_list")
public class NewMessageList extends JPAEntity<Long> {
    private String title;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
    private String src;
    private String category;
    private String pic;
    private String content;
    private String url;
    private String weburl;
    @ManyToOne(targetEntity = NewMessageListResult.class)
    NewMessageListResult newMessageListResult;


}