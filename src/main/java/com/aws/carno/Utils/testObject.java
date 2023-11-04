package com.aws.carno.Utils;

import com.aws.carno.core.StartCore;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;


public class testObject {

    public static int type=1;
    public static BlockingQueue<byte[]> msgQueue = new LinkedBlockingQueue<>();
    String code;
    int factory;
    static ArrayList<Byte> data_list = new ArrayList<Byte>();
    static int maxsize=0;
    static int length=0;


    public static byte[] hexStrToByteArray(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        int new_length;
        int yushu=str.length()%3;
        if(yushu!=0)
        new_length=str.length()/3+1;
        else
        new_length=str.length()/3;

        byte[] byteArray = new byte[new_length];
        for (int i = 0; i < byteArray.length; i++) {
            String subStr = str.substring(3 * i, 3 * i+2 );
            byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }

    public static void process_readBuffer(byte[] buffer,int len)
    {

        long time_start=System.currentTimeMillis();
        System.err.println("time start"+time_start);

        int data_pack_size=0;
        int invalid_index=0;

        if(!data_list.isEmpty())
        {
            invalid_index=1;
        }

        if(buffer.length!=len)
        {
            return;
        }
        for(int i=0;i<len;i++)
        {
            //说明是新的数据包
            if(buffer[i]==-1)
            {
                data_pack_size=len-i;
                invalid_index=1;
                //清空列表和length
                maxsize=18;
                length = 0;
                data_list.clear();
                data_list.add(buffer[i]);
                length++;
                i++;
            }
            if(invalid_index==1)
            {
                //说明数据包有效
                //只需要有效数据位的数据，剩下的数据就不存了
                if(length<maxsize)
                {

                    data_list.add(buffer[i]);
                    length++;
                    if(length==3)
                    {
                        //检查固定值是不是1
                        if(data_list.get(2)!=1)
                        {
                            invalid_index=0;
                            maxsize=0;
                            length=0;
                            data_list.clear();
                            data_pack_size=0;
                        }

                    }
                    if(length==18)//轴数，则扩大maxsize
                    {
                        int axis=data_list.get(17)/10;
                        maxsize=18+9*axis;
                    }
                }
                 if(length==maxsize)
                {//最后一位
                    invalid_index=0;
                    byte[] new_buffers=new byte[maxsize];
                    for(int j=0;j<maxsize;j++)
                    {
                        new_buffers[j]=data_list.get(j);
                        //缺少添加队列
                    }
                    if (type == 1){
                        //生成唯一流水号
                        String preNo = StringUtil.genNo();
                        //将称台返回数据临时存储
                        StartCore.hashMap.put(Arrays.hashCode(new_buffers),preNo);
//                                if (factory == 1) {
//                                    UnvCarNoCore unv = StartCore.UnvMaps.get(code);
//                                    //接收到称台数据 调用宇视摄像头异步抓拍;
//                                    unv.CaptureSyncAction(preNo);
//                                } else if (factory == 2) {
//                                    HikCarNoCore hik = StartCore.HikMaps.get(code);
//                                    //接收到称台数据 调用海康摄像头异步抓拍;
//                                   // hik.startListen(preNo);
//                                }
                        msgQueue.add(new_buffers);
                    }
                    data_list.clear();
                    length=0;
                    maxsize=0;

                }

            }
        }
        long time_end=System.currentTimeMillis();
        System.err.println("time end:"+time_end);
    }


    public static void main(String[] args)
    {

        byte[] b1=hexStrToByteArray("FF 22 01 00 65 37 AA 3F 07 00 00 23 10 26 CC 26 17 00");
        byte[] b2=hexStrToByteArray("FF 22 01 00 65 37 AA 3F 07 00 00 23 10 26 11 30 01 3C 00 14 60 00 10 40 00 09 50 00 05 50 00 12 50 00 11 30 00 14 60 00 10 40 00 09 50 00 05 50 00 12 50 00 11 30 00 20 18 00 13 07 00 50 39 00 13 24 00 13 10 1C 32 32 FF 44 01 00 65 33 AB 3F 07 00 00 23 10 26 11 30 01 3C 00 33 50 00 05 20 00 06 10 00 06 80 00 06 80 00 08 40 00 33 50 00 05 20 00 06 10 00 06 80 00 06 80 00 08 40 00 20 26 00 13 09 00 50 35 00 13 24 00 13 12 1C 33 31 32 31 32 33 33 31 32 31 32 33 33 31 32 31 32 33 43 44 4B 50 30 30 59 0A 37 00 01 83 72");
//        byte[] b3=hexStrToByteArray("50 00 11 30 00 20 18 00 13 07 00 50 39 00 13 24 00 13 10 1C CC");
//        byte[] b4=hexStrToByteArray("32 32 01 FF 02 01 12 23 10 26 11 30 07 59 0C 00 00 00 79 79 00 CC");
//        byte[] b=hexStrToByteArray("FF 04 02 00 12 23 10 26 11 30 08 59 0D 00 01 00 98 90");
        Thread myThread = new Thread(new Runnable(){
            @Override
            public void run() {
                // 在 run() 方法中编写线程要执行的任务。

                for (int i = 1; i <= 4; i++)
                {
                    byte[] over={};
                    int len =0;
                    switch (i){
                        case 1:
                            over=b2;
                            len=173;
                            break;
//                        case 2:
//                            over=b2;
//                            len=36;
//                            break;
//                        case 3:
//                            over=b3;
//                            len=20;
//                            break;
//                        case 4:
//                            over=b4;
//                            len=21;
//                            break;

                    }
                    process_readBuffer(over,len);

                }

            }
        });
        myThread.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getByteArrayData();

//        process_readBuffer(b,5);

//        for(int i=0;i<b.length;i++)
//        {
//            System.err.println(b[i]);
//        }
//        System.err.println(b.length);
    }

    public static void getByteArrayData() {
        try {
            System.out.println("--------------任务处理线程运行了--------------");
            while (true) {
                // 如果堵塞队列中存在数据就将其输出
                if (msgQueue.size() > 0) {
                byte[] data = msgQueue.take();
                    String hex = new BigInteger(1,data).toString(16);
//                    System.out.println(hex);
                    StringBuffer hex1 = new StringBuffer();
                    for (int i=0;i<hex.length();i+=2){
                        if(i+2<=hex.length()){
                            hex1.append(hex.substring(i,i+2));
                            hex1.append(" ");
                        }


                    }


                    System.out.println(hex1);




                }
            }

        }catch (Exception e){}

    }



}
