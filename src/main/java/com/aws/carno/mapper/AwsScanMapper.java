package com.aws.carno.mapper;

import com.aws.carno.domain.AwsScan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 设备信息表 Mapper 接口
 * </p>
 *
 * @author HuangYW
 * @since 2023-05-15
 */
@Mapper
public interface AwsScanMapper extends BaseMapper<AwsScan> {

    @Select("<script>" +
            "SELECT\n" +
            "\tu.*,\n" +
            "\to.NAME org_name \n" +
            "FROM\n" +
            "\taws_scan u LEFT JOIN\n" +
            "\taws_nsp_org o on \tu.org_code = o.code \n" +
            "WHERE\n" +
            "\t u.state !=0" +
            "</script>")
    List<AwsScan> selectAll();

}
