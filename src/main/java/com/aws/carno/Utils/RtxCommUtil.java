package com.aws.carno.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.aws.carno.core.HikCarNoCore;
import com.aws.carno.core.StartCore;
import com.aws.carno.core.UnvCarNoCore;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import org.apache.commons.lang3.ArrayUtils;

import static com.aws.carno.Utils.LedUtil.hexStr2Str;
import static org.apache.ibatis.ognl.DynamicSubscript.first;

/**
 * @author :hyw
 * @version 1.0
 * @date : 2023/06/28 15:49
 * @description
 */
public class RtxCommUtil implements SerialPortEventListener {

    InputStream inputStream; // 从串口来的输入流
    OutputStream outputStream;// 向串口输出的流
    SerialPort serialPort; // 串口的引用
    CommPortIdentifier portId;
    public int type;
    public BlockingQueue<byte[]> msgQueue = new LinkedBlockingQueue<>();
    public byte[] newBufffer = new byte[1024];
    String code;
    int factory;
    LinkedList<Byte> data_list = new LinkedList<Byte>();
    byte[] cache =new byte[1024];
    int idx=0;
    int maxsize=0;//一个有效数据的最大值
    int length=0;
    int again_flag=0;//0 or 1
    byte[] again_Array = new byte[1024];
    int again_length=0;
    int no_maxsize_length=-1;


    public RtxCommUtil(CommPortIdentifier temp, String portName, int bits, int type, String code, int factory) {
        this.type = type;
        this.code=code;
        this.factory=factory;
        try {
            portId = temp;
            // 打开串口,延迟为2毫秒
            serialPort = (SerialPort) portId.open(portName, 2000);
        } catch (PortInUseException e) {
            e.printStackTrace();
        }
        // 设置当前串口的输入输出流
        try {
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 给当前串口添加一个监听器
        try {
            serialPort.addEventListener(this);
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }
        // 设置监听器生效，即：当有数据时通知
        serialPort.notifyOnDataAvailable(true);

        // 设置串口的一些读写参数
        try {
            // 比特率、数据位、停止位、奇偶校验位
            serialPort.setSerialPortParams(bits,
                    SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        }
    }


    public void process_writeBuffer(byte[] buffer){
        if (buffer[0]==-1){
            if (buffer[2]==1){
                msgQueue.add(buffer);
                //生成唯一流水号
                //String preNo = StringUtil.genNo();
                //将称台返回数据临时存储
                //StartCore.hashMap.put(Arrays.hashCode(buffer),preNo);
            }
            else
                newBufffer=buffer;
        }else {
            if (newBufffer!=null){
                byte[] nb= ArrayUtils.addAll(newBufffer,buffer);
                //生成唯一流水号
                //  String preNo = StringUtil.genNo();
                //将称台返回数据临时存储
                // StartCore.hashMap.put(Arrays.hashCode(nb),preNo);
                msgQueue.add(nb);
                newBufffer=null;
            }

        }


    }

    public void process_writeBuffer2(byte[] buffer,int numsBytes){
        //接收一定长度的数据
        //判断是否有效
        if(buffer[0]==-1&&buffer[2]==1)
        {
            maxsize=buffer[4];

            if(maxsize==0 && numsBytes <=4 && numsBytes>=3) //-1 55 1 0 or -1 55 1
            {
                if(numsBytes==3)
                    no_maxsize_length=3;//说明未知长度
                else if(numsBytes==4)
                    no_maxsize_length=4;

                idx=0;
                //maxsize=numsBytes;
                System.arraycopy(buffer,0,cache,idx,numsBytes);
                idx+=numsBytes;
                again_flag=0;
                again_Array=new byte[1024];
                again_length=0;
                return;

            }
            no_maxsize_length=-1;
            if(numsBytes>=maxsize)
            {//长度达标直接发送
                msgQueue.add(buffer);

                if(numsBytes>maxsize&&buffer[maxsize]==-1)
                {
                    //再去读一次数据
                    again_flag=1;
                    //将maxsize+1,numsBytes-1这个索引值区间内的
                    System.arraycopy(buffer,maxsize,again_Array,0,numsBytes-maxsize);
                    again_length=numsBytes-maxsize;
//                    again_Array
                }

                if(numsBytes==maxsize)
                {
                    again_flag=0;
                    again_Array=new byte[1024];
                    again_length=0;
                }

                maxsize=0;
                idx=0;
                cache=new byte[1024];

            }
            else{
                //长度不达标缓存起来
                // ArrayUtils.addAll(cache,buffer);
//                System.arraycopy(buffer,0,cache,idx,numsBytes);
//                idx+=numsBytes;
                idx=0;
//                cache=buffer;
                System.arraycopy(buffer,0,cache,idx,numsBytes);
                idx+=numsBytes;
                again_flag=0;
                again_Array=new byte[1024];
                again_length=0;
            }

        }
        else{
//            if(cache!=null)
            if(idx!=0)
            {
                if(no_maxsize_length>0)
                {
                    if(no_maxsize_length==3)
                    {
                        //说明buffer[1]是maxsize
                        maxsize=buffer[1];
                    }
                    else if(no_maxsize_length==4)
                    {
                        //说明buffer[0]是maxsize
                        maxsize=buffer[0];
                    }
                    //既然获取到了maxsize 那么可以清零这些变量了
                    no_maxsize_length=-1;
                }

                if(idx+numsBytes>=maxsize)
                {//如果缓存收够了
                    System.arraycopy(buffer,0,cache,idx,numsBytes);
                    //！！！！！！！！
                    idx+=numsBytes;
                    msgQueue.add(cache);

//                    if( (idx+numsBytes) >maxsize && cache[maxsize]==-1)
                    if( idx >maxsize && cache[maxsize]==-1)
                    {
//                        int again_length=idx-maxsize;
                        //再去读一次数据
                        again_flag=1;
                        //将maxsize+1,numsBytes-1这个索引值区间内的
                        System.arraycopy(cache,maxsize,again_Array,0,idx-maxsize);
//                        again_length=idx-maxsize-1;
                        again_length=idx-maxsize;
                    }
                    if(idx==maxsize)
                    {
                        again_flag=0;
                        again_Array=new byte[1024];
                        again_length=0;
                    }

                    maxsize=0;
                    idx=0;
                    cache=new byte[1024];
                }
                else{//如果缓存没收够
                    System.arraycopy(buffer,0,cache,idx,numsBytes);
                    idx+=numsBytes;
                    //针对again
                    again_flag=0;
                    again_Array=new byte[1024];
                    again_length=0;

                }
            }

        }

    }

    //
    public void process_readBuffer(byte[] buffer,int len)
    {

        int data_pack_size=0;
        int invalid_index=0;

        if(!data_list.isEmpty())
        {
            invalid_index=1;
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
                        msgQueue.add(new_buffers);
                        System.err.println("放入队列成功");
                        for(int k=0;k<maxsize;k++)
                        {
                            System.err.print(new_buffers[k]);
                        }
                        System.err.println();
                    }
                    data_list.clear();
                    length=0;
                    maxsize=0;
                }
            }
        }

    }

    //TODO 处理串口的函数
    @Override
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:// 当有可用数据时读取数据,并且给串口返回数据

                byte[] readBuffer = new byte[1024];
                try {
                    int numBytes = -1;
                    while (inputStream.available() > 0) {

                        numBytes = inputStream.read(readBuffer);

                        if (numBytes > 0) {

                       /*     for(int i=0;i<numBytes;i++)
                            {
                                System.out.println(readBuffer[i]+" ");
                            }

                            System.out.println("");*/


                            if (type == 1){
                                //idx=0;
                                process_writeBuffer2(readBuffer,numBytes);
                                int curl_flag=0;
                                while(again_flag==1)
                                {
                                    process_writeBuffer2(again_Array,again_length);
                                    curl_flag++;
                                    if(again_flag==0 || curl_flag==4)
                                    {
                                        again_Array=new byte[1024];
                                        again_length=0;
                                        again_flag=0;
                                        break;
                                    }

                                }
                                // process_readBuffer(readBuffer, numBytes);

                                //生成唯一流水号
                                //String preNo = StringUtil.genNo();
                                //将称台返回数据临时存储
                                //StartCore.hashMap.put(Arrays.hashCode(readBuffer),preNo);
//                                if (factory == 1) {
//                                    UnvCarNoCore unv = StartCore.UnvMaps.get(code);
//                                    //接收到称台数据 调用宇视摄像头异步抓拍;
//                                    unv.CaptureSyncAction(preNo);
//                                } else if (factory == 2) {
//                                    HikCarNoCore hik = StartCore.HikMaps.get(code);
//                                    //接收到称台数据 调用海康摄像头异步抓拍;
//                                   // hik.startListen(preNo);
//                                }
                                // msgQueue.add(readBuffer);
                            }
                            readBuffer = new byte[1024];//
                            // 重新构造缓冲对象，否则有可能会影响接下来接收的数据
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    public void send(byte [] bytes) {
        try {

            LocalDateTime now = LocalDateTime.now();

            // 提取年、月、日、时、分、秒
            int year = now.getYear() % 100; // 获取年份并取后两位
            int month = now.getMonthValue();
            int day = now.getDayOfMonth();
            int hour = now.getHour();
            int minute = now.getMinute();
            int second = now.getSecond();
            System.out.println("称台发送时间："+year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second);
           /* for(int i=0;i<bytes.length;i++)
            {
                System.out.print(bytes[i]+" ");
            }
            System.out.println();*/

            outputStream.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ClosePort() {
        if (serialPort != null) {
            serialPort.close();
            try {
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        int[] a={1,2,3,4,5,6,7,8,9,10};
        int[] b={20,12};
        int[] c=ArrayUtils.addAll(a,b);
        for (int d:c) {
            System.err.println(d);

        }
    }
}
