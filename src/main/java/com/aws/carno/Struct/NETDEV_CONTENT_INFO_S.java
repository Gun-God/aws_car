package com.aws.carno.Struct;

import com.aws.carno.Enum.NetDEVEnum;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * @struct tagNETDEVContentInfo
 * @brief 内容信息 Content
 * @attention
 */
public class NETDEV_CONTENT_INFO_S extends Structure {
    public int udwContentType;                                                 /* OSD内容类型,参见枚举NETDEV_OSD_CONTENT_TYPE_E OSD content type. For enumeration, see NETDEV_OSD_CONTENT_TYPE_E*/
    public byte[] szOSDText = new byte[NetDEVEnum.NETDEV_OSD_TEXT_MAX_LEN];    /* OSD文本信息 OSD text*/
    
    public NETDEV_CONTENT_INFO_S() {
        super();
        read();
    }
    
    public NETDEV_CONTENT_INFO_S(Pointer pointer) {
        super(pointer);
        read();
    }
    
    public static class ByValue extends NETDEV_CONTENT_INFO_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_CONTENT_INFO_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"udwContentType", "szOSDText"});
    }
}