/**
 * Copyright 2020 bejson.com
 */
package cn.xr.test.pojo;

import cn.xr.model.base.JPAEntity;
import lombok.Data;

import javax.persistence.*;

/**
 * Auto-generated: 2020-02-27 16:25:43
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Entity(name = "new_message_usual_result")
@Table(name = "new_message_usual_result")
@Data
public class NewMessageUsualResult  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO )
    protected Long id;
    private String status;
    private String msg;
    @OneToOne
    private NewMessageListResult usualResult;
}