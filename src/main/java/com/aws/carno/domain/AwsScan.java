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
 * 设备信息表
 * </p>
 *
 * @author HuangYW
 * @since 2023-05-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AwsScan implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 设备编号
     */
    private String code;

    /**
     * 设备名称
     */
    private String portName;

    /**
     * 车道
     */
    private Integer lane;

    /**
     * 端口
     */
    private Integer port;

    private String udpIp;

    private String videoIp;

    /**
     * 设备类型（1精检称台，2预检称台，3摄像头）
     */
    private Integer type;

    /**
     * 状态(1正常2维修)
     */
    private Integer state;

    /**
     * 操作员
     */
    private String operName;

    /**
     * 检测站
     */
    private String orgCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 设备唯一标识
     */
    private String deviceId;

    /**
     * 账号
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 视频端口
     */
    private String videoPort;

    @TableField(exist = false)
    private String orgName;

    /**
     * 关联设备编号
     */
    private String reCode;

    /**
     * 摄像头厂家（1宇视，2海康）
     */
    private Integer factory;


    /**
     * 对外展示车道
     * */
    private Integer showLane;

    /*
    * 车道实际名称
    * **/
    private String laneName;

    /**
     * 摄像头方向(1前向 2后向)
     * **/
    private Integer direction;




}
