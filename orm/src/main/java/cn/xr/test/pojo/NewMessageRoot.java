/**
 * Copyright 2020 bejson.com
 */
package cn.xr.test.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * Auto-generated: 2020-02-27 16:25:43
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Entity(name = "new_message_root")
@Table(name = "new_message_root")
@Data
public class NewMessageRoot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    private String code;
    private boolean charge;
    private String msg;
    @OneToOne
    private NewMessageUsualResult newMessageUsualResult;
}