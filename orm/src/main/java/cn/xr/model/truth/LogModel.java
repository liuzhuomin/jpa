package cn.xr.model.truth;

import cn.xr.model.base.NameEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import java.util.Date;

/**
 * @author liuliuliu
 * @version 1.0
 * 2019/10/9 14:21
 */
@Data
@Entity(name="log_model")
@SuppressWarnings("unused")
public class LogModel extends NameEntity {

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime=new Date(System.currentTimeMillis());

    /**
     * 数据
     */
    private String data;

    /**
     * 关联的id
     */
    private String  relevanceId;

    public LogModel() {
    }

    public LogModel(String relevanceId,String name,String description, String data) {
        super(name,description);
        this.relevanceId=relevanceId;
        this.data = data;
    }

    public LogModel(String data) {
        this.data = data;
    }
}
