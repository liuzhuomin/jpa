package cn.xr.redis;

import lombok.Data;

/**
 * 包含距离和地理
 * @author liuliuliu
 */
@SuppressWarnings("AlibabaPojoMustUsePrimitiveField")
@Data
public class GeoDTO<V> {
    private V name;
    private double distance;

    public GeoDTO(V name, double distance) {
        this.name = name;
        this.distance = distance;
    }
}