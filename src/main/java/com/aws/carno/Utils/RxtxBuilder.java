package com.aws.carno.Utils;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import gnu.io.CommPortIdentifier;
/**
 * @author :hyw
 * @version 1.0
 * @date : 2023/06/28 16:16
 * @description
 */

public class RxtxBuilder {
    public static RtxCommUtil init(String name, int bits,int type) {
        Enumeration portList = CommPortIdentifier.getPortIdentifiers(); // 得到当前连接上的端口
        while (portList.hasMoreElements()) {
            CommPortIdentifier temp = (CommPortIdentifier) portList.nextElement();
            if (temp.getPortType() == CommPortIdentifier.PORT_SERIAL) {// 判断如果端口类型是串口
                if (name.equals(temp.getName())) { // 判断如果端口已经启动就连接
                    System.out.println("设备类型：--->" + temp.getPortType());
                    System.out.println("设备名称：---->" + temp.getName());
                    System.err.println("端口---" + temp.getName() + "启动监听");
                    return new RtxCommUtil(temp,name,bits, type);
                }
            }
        }
        return null;
    }


}
