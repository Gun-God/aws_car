package com.aws.carno.Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;


public class testObject {

    public static BlockingQueue<CameraData> msgQueue1 = new LinkedBlockingQueue<>();


    public static void main(String[] args) throws InterruptedException {



        // 打印摄像头数据
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    processing_Data();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        fun();

    }

    public static void fun() throws InterruptedException {
        for(int i=0;i<100;i++) {
            // 创建一个 CameraData 实例
            CameraData camera1 = new CameraData("Camera 1", 1920, 1080, "192.168"+i+"100");
            msgQueue1.add(camera1);
            sleep(100);
        }
    }


    public static void processing_Data() throws InterruptedException {
        while(1==1)
        {
            if(msgQueue1.size()>0) {
                CameraData cameraData ;
                cameraData= msgQueue1.take();
                System.out.println("Camera Name: " + cameraData.getCameraName());
                System.out.println("Resolution Width: " + cameraData.getResolutionWidth());
                System.out.println("Resolution Height: " + cameraData.getResolutionHeight());
                System.out.println("IP Address: " + cameraData.getIpAddress());
            }
        }
    }

}

class CameraData {
    private String cameraName;
    private int resolutionWidth;
    private int resolutionHeight;
    private String ipAddress;

    public CameraData(String cameraName, int resolutionWidth, int resolutionHeight, String ipAddress) {
        this.cameraName = cameraName;
        this.resolutionWidth = resolutionWidth;
        this.resolutionHeight = resolutionHeight;
        this.ipAddress = ipAddress;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public void setResolutionWidth(int resolutionWidth) {
        this.resolutionWidth = resolutionWidth;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    public void setResolutionHeight(int resolutionHeight) {
        this.resolutionHeight = resolutionHeight;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "CameraData{" +
                "cameraName='" + cameraName + '\'' +
                ", resolutionWidth=" + resolutionWidth +
                ", resolutionHeight=" + resolutionHeight +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}