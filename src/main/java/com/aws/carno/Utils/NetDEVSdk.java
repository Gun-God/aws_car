package com.aws.carno.Utils;

import com.sun.jna.Pointer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tW6871
 */
public class NetDEVSdk {
    public static Pointer m_lpDevHandle = Pointer.NULL;                        /**@brief 用户ID */
    public static Pointer m_lpPlayHandle = Pointer.NULL;                       /**@brief 实况流句柄 */
    public static Pointer m_lpPicHandle = Pointer.NULL;                        /**@brief 照片流句柄 */
    public static Pointer m_lpPlayWndHandle = Pointer.NULL;                    /**@brief 实况流窗口句柄 */
    public static Pointer m_lpPicWndHandle = Pointer.NULL;                     /**@brief 照片流窗口句柄 */
    public static boolean bVideoStreamStart = false;                           /**@brief 实况流播放标记 */
    public static boolean bPicStreamStart = false;                             /**@brief 照片流启流标记 */
    public static boolean bPicPlay = false;                                    /**@brief 照片流播放标记 */
    public static boolean bSetDiscoveryCB = false;                             /**@brief 设备发现回调设置标记 */
}
