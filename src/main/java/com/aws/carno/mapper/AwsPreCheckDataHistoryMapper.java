package com.aws.carno.mapper;

import com.aws.carno.domain.AwsPreCheckData;
import com.aws.carno.domain.AwsPreCheckDataHistory;
import com.aws.carno.domain.AwsScan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 预检信息记录历史表 Mapper 接口
 * </p>
 *
 * @author HuangYW
 * @since 2023-05-15
 */
@Mapper
public interface AwsPreCheckDataHistoryMapper extends BaseMapper<AwsPreCheckDataHistory> {

    @Insert("<script>" +
            "INSERT\tINTO\taws.publicrole.aws_pre_check_data_history\n" +
            "\t(pre_no,car_no,weight,limit_amt,axis_num,speed,lane,device_id," +
            "create_time,pass_time,img,url,is_show,pre_amt,org_code,car_type_id,color)\n"+
            "\tVALUES(#{preNo},#{carNo},#{weight},#{limitAmt},#{axisNum},#{speed},#{lane},#{deviceId}," +
            "#{createTime},#{passTime},#{img},#{url},#{isShow},#{preAmt},#{orgCode},#{carTypeId},#{color})\n" +
            "</script>")
    void insertOtherModel(AwsPreCheckData a);

}
