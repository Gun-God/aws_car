package com.aws.carno.core;

import com.aws.carno.Utils.RTXDataParse;
import com.aws.carno.domain.AwsCarType;
import com.aws.carno.domain.AwsCarTypeIdRelation;
import com.aws.carno.domain.AwsPreCheckData;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ScoketWeightCore {

    private String ip;
    private int port;
    private Socket socket;

    public ScoketWeightCore(String ip, int port) {
        this.ip = ip;
        this.port = port;
        if (connect())
            getBytes();


    }

    boolean connect() {
        try {
            socket = new Socket(ip, port);
            if (socket == null) {
                System.err.println("连接失败");
                return false;
            }

            System.err.println("连接成功");

            // String preNo = StartCore.hashMap.get(hashCode);


        } catch (Exception e) {

        }
        return true;
    }

    void getBytes() {
        try {
            InputStream inputStream = socket.getInputStream(); // 获取客户端输入流
            OutputStream outputStream = socket.getOutputStream(); // 获取客户端输出流
            byte[] bytes = new byte[1024];
            int len = inputStream.read(bytes);
            int length = inputStream.read(bytes); // 读取数据
            String message = new String(bytes, 0, length);
           // System.out.println("Received message: " + message);
            //将称台字节数据解析到实体类
            AwsPreCheckData preCheckData = RTXDataParse.byteArrayToObjData(bytes);
            //TODO 打印实体类数据(称重信息,重点看流水号是否绑定成功)
            System.out.println("=========================================================");
            System.out.println("称重台检测内容：");
            System.err.println(preCheckData);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void close() throws IOException {
        if (socket != null)
            socket.close();
    }

}
