package com.aws.carno.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 车牌抓拍记录表
 * </p>
 *
 * @author HuangYW
 * @since 2023-05-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AwsCarNo implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 车型
     */
    private String code;

    /**
     * 车牌
     */
    private String carNo;

    /**
     * 图片
     */
    private String img;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 车牌颜色
     */
    private String color;

    /**
     * 车道
     */
    private Integer lane;

    @TableField(exist = false)
    private String ip;


}
