/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aws.carno.bean;

import com.sun.jna.Pointer;

/**
 * 多用户信息
 */
public class StuDevInfo {
    public String strDevIP;
    public String strDevAdmin;
    public String strDevPassWord;
    public Pointer lpDevHandle;
    public Pointer lpPicHandle;
    public int ulUserId;
    public boolean bLogin;
    public boolean bStartStream;
    public int ulPicCount;
}