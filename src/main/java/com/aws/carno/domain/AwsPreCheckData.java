package com.aws.carno.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 预检信息记录表
 * </p>
 *
 * @author HuangYW
 * @since 2023-05-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AwsPreCheckData implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 预检流水号
     */
    private String preNo;

    /**
     * 车牌
     */
    private String carNo;

    /**
     * 车自重
     */
    private Double weight;

    /**
     * 限重
     */
    private Double limitAmt;

    /**
     * 轴数
     */
    private Integer axisNum;

    /**
     * 车速
     */
    private Double speed;

    /**
     * 车道
     */
    private Integer lane;

    /**
     * 设备唯一标识
     */
    private String deviceId;

    /**
     * 拍照时间
     */
    private Date createTime;

    /**
     * 通过时间
     */
    private Date passTime;

    private String img;

    private String url;

    /**
     * 是否展示（0不展示1展示）
     */
    private Integer isShow;

    /**
     * 预检重量
     */
    private Double preAmt;

    /**
     * 检测站
     */
    private String orgCode;

    /**
     * 车型
     */
    private Integer carTypeId;
    /**
     * 车牌颜色（1蓝色2黄色）
     */
    private Integer color;

    @Override
    public String toString() {
        return "AwsPreCheckData{" +
                "id=" + id +
                ", preNo='" + preNo + '\'' +
                ", carNo='" + carNo + '\'' +
                ", weight=" + weight +
                ", limitAmt=" + limitAmt +
                ", axisNum=" + axisNum +
                ", speed=" + speed +
                ", lane=" + lane +
                ", deviceId='" + deviceId + '\'' +
                ", createTime=" + createTime +
                ", passTime=" + passTime +
                ", img='" + img + '\'' +
                ", url='" + url + '\'' +
                ", isShow=" + isShow +
                ", preAmt=" + preAmt +
                ", orgCode='" + orgCode + '\'' +
                ", carTypeId=" + carTypeId +
                ", color=" + color +
                '}';
    }
}
