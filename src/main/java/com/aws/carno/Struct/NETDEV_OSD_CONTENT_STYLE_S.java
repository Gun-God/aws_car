package com.aws.carno.Struct;

import com.aws.carno.Enum.NetDEVEnum;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * @struct tagNETDEVOsdContentStyle
 * @brief 通道OSD内容样式 Display Style of channel OSD
 * @attention
 */
public class NETDEV_OSD_CONTENT_STYLE_S extends Structure {
    public int udwFontStyle;                                              /* 字体形式，参见枚举NETDEV_OSD_FONT_STYLE_E。  Font style. For enumeration, seeNETDEV_OSD_FONT_STYLE_E*/
    public int udwFontSize;                                               /* 字体大小，参见枚举NETDEV_OSD_FONT_SIZE_E。  Font Size. For enumeration, seeNETDEV_OSD_FONT_SIZE_E*/
    public int udwColor;                                                  /* 颜色 Color*/
    public int udwDateFormat;                                             /* 日期格式，参见枚举NETDEV_OSD_DATE_FORMAT_E。  Date Format. For enumeration, seeNETDEV_OSD_DATE_FORMAT_E */
    public int udwTimeFormat;                                             /* 时间格式，参见枚举NETDEV_OSD_TIME_FORMAT_E。  Date Format. For enumeration, seeNETDEV_OSD_DATE_FORMAT_E */
    public int audwFontAlignList[] = new int[NetDEVEnum.NETDEV_LEN_8];    /* 区域内字体对齐，固定8个区域，IPC支持,参见枚举NETDEV_OSD_ALIGN_E。  Font align in area, 8 areasfixed, IPcamera supported. For enumeration, seeNETDEV_OSD_ALIGN_E */
    public int udwMargin;                                                 /* 边缘空的字符数，IPC支持，参见枚举NETDEV_OSD_MIN_MARGIN_E。  Number of character with margin, IP camera supported. For enumeration, seeNETDEV_OSD_MIN_MARGIN_E */
    
    public NETDEV_OSD_CONTENT_STYLE_S() {
        super();
        read();
    }
    
    public NETDEV_OSD_CONTENT_STYLE_S(Pointer pointer) {
        super(pointer);
        read();
    }
    
    public static class ByValue extends NETDEV_OSD_CONTENT_STYLE_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_OSD_CONTENT_STYLE_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"udwFontStyle", "udwFontSize", "udwColor", "udwDateFormat", "udwTimeFormat", "audwFontAlignList", "udwMargin"});
    }
}