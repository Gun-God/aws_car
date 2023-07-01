package com.aws.carno.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

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

    public RtxCommUtil(CommPortIdentifier temp, String portName, int bits, int type) {
        this.type = type;
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
                            if (type == 1)
                                msgQueue.add(readBuffer);
                            readBuffer = new byte[1024];// 重新构造缓冲对象，否则有可能会影响接下来接收的数据
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
}
