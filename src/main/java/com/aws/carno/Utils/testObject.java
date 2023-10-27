package com.aws.carno.Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;


public class testObject {


    public static byte[] hexStrToByteArray(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 3];
        for (int i = 0; i < byteArray.length; i++) {
//            int l=0;
//            if (i%2==0)
//                l=2*i;
//            else
//                l=2*i+1;
            String subStr = str.substring(3 * i, 3 * i + 2);
            byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }

    public static void main(String[] args)
    {
        byte[] b=hexStrToByteArray("FF 04 02 00 12 23 10 26 11 30 08 59 0D 00 01 00 98 90");
        for(int i=0;i<b.length;i++)
        {
            System.err.println(b[i]);
        }
        System.err.println(b.length);
    }



}
