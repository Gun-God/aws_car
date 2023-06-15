package com.aws.carno.Struct;

import com.aws.carno.Enum.NetDEVEnum;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * @struct tagNETDEVOsdContent
 * @brief 通道OSD所有内容 All contents of channel OSD
 * @attention
 */
public class NETDEV_OSD_CONTENT_S extends Structure {
    public int udwNum;                                                                                             /* OSD区域数量 Number of OSD area */
    public NETDEV_OSD_CONTENT_INFO_S[] astContentList = new NETDEV_OSD_CONTENT_INFO_S[NetDEVEnum.NETDEV_LEN_32];   /* OSD区域内容信息列表 Content list of OSD area */
    
    public NETDEV_OSD_CONTENT_S() {
        super();
        read();
    }
    
    public NETDEV_OSD_CONTENT_S(Pointer pointer) {
        super(pointer);
        read();
    }
    
    public static class ByValue extends NETDEV_OSD_CONTENT_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_OSD_CONTENT_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"udwNum", "astContentList"});
    }
}