package com.aws.carno.mapper;

import com.aws.carno.domain.AwsCarNo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 车牌抓拍记录表 Mapper 接口
 * </p>
 *
 * @author HuangYW
 * @since 2023-05-15
 */
@Mapper
public interface AwsCarNoMapper extends BaseMapper<AwsCarNo> {

}
