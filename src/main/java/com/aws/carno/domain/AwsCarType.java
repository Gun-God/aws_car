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
 * 车辆类型表
 * </p>
 *
 * @author HuangYW
 * @since 2023-05-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AwsCarType implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 车型名称
     */
    private String name;

    /**
     * 轴数
     */
    private Integer axisNum;

    /**
     * 最大限重
     */
    private Double limitAmt;

    /**
     * 标准限重
     */
    private Double checkLimit;

    /**
     * 车长
     */
    private Double length;

    /**
     * 车宽
     */
    private Double width;

    /**
     * 车高
     */
    private Double height;

    /**
     * 状态（0不可用1可用）
     */
    private Integer state;

    /**
     * 操作员
     */
    private String operName;

    /**
     * 时间
     */
    private Date createTime;


}
