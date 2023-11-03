package com.aws.carno.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AwsTempWeightData implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 预检流水号
     */
    private String preNo;

    /**
     * 设备唯一标识
     */
    private String deviceId;


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
     * 拍照时间
     */
    private Date createTime;

    /**
     * 通过时间
     */
    private Date passTime;

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

    @Override
    public String toString() {
        return "AwsTempWeightData{" +
                "id=" + id +
                ", preNo='" + preNo + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", weight=" + weight +
                ", limitAmt=" + limitAmt +
                ", axisNum=" + axisNum +
                ", speed=" + speed +
                ", lane=" + lane +
                ", createTime=" + createTime +
                ", passTime=" + passTime +
                ", preAmt=" + preAmt +
                ", orgCode='" + orgCode + '\'' +
                ", carTypeId=" + carTypeId +
                '}';
    }
}
