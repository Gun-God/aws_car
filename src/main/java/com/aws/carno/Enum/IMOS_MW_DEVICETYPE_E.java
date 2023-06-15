/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aws.carno.Enum;

/**
 *
 * @author tW6871
 */
public class IMOS_MW_DEVICETYPE_E
{
    public static final int IMOS_MW_DTYPE_UNKNOWN = 0;          /* Unknown type */
    public static final int IMOS_MW_DTYPE_IPC = 1;              /* IPC range */
    public static final int IMOS_MW_DTYPE_IPC_FISHEYE = 2;      /* 鱼眼IPC  fish eye IPC */
    public static final int IMOS_MW_DTYPE_NVR = 101;            /* NVR range */
    public static final int IMOS_MW_DTYPE_NVR_BACKUP  = 102;    /* NVR备份服务器  NVR back up */
    public static final int IMOS_MW_DTYPE_DC = 201;             /* DC range */
    public static final int IMOS_MW_DIYPE_DC_ADU = 202;         /* ADU range */
    public static final int IMOS_MW_DTYPE_EC = 301;             /* EC range */
    public static final int IMOS_MW_DTYPE_VMS = 501;            /* VMS range */
    public static final int IMOS_MW_DTYPE_INVALID = 0xFFFF;     /* 无效值  Invalid value */
}
