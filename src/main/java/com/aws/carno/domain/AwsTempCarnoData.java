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
public class AwsTempCarnoData implements Serializable {


    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 车牌
     */
    private String carNo;

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

    /**
     * 检测站
     */
    private String orgCode;

    /**
     * 车牌颜色（1蓝色2黄色）
     */
    private Integer color;

    @Override
    public String toString() {
        return "AwsTempCarnoData{" +
                "id=" + id +
                ", carNo='" + carNo + '\'' +
                ", lane=" + lane +
                ", deviceId='" + deviceId + '\'' +
                ", createTime=" + createTime +
                ", passTime=" + passTime +
                ", img='" + img + '\'' +
                ", orgCode='" + orgCode + '\'' +
                ", color=" + color +
                '}';
    }
}
