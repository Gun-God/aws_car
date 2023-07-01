package com.aws.carno.Utils;

import com.aws.carno.domain.AwsPreCheckData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author :hyw
 * @version 1.0
 * @date : 2023/06/20 11:38
 * @description 称台串口数据解析工具类
 */
public class RTXDataParse {


    //16进制转byte数组
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


    /**
     * 将BCD码转换为十进制的字符串。举例：
     * 如果BCD码为00010010，转换后的十进制字符串为"12"
     * 如果BCD码为00000010，转换后的十进制字符串为"2"
     * 如果BCD码为00000001 00100011，转换后的十进制字符串为"123"
     *
     * @param bcd BCD码
     * @return 十进制字符串
     */
    public static Integer bcdToDecimalInteger(byte[] bcd) {
        StringBuilder sb = new StringBuilder();
        // 存放转化后的十进制数字的字符串
        String decStr;

        for (byte b : bcd) {
            // 每个字节的前四位的值右移4位转化为十进制数
            sb.append((b & 0XF0) >> 4);
            sb.append(b & 0X0F);
        }

        // 如果转化后的字符串首字母为0，那么去掉
        if (sb.charAt(0) == '0') {
            decStr = sb.substring(1);
        } else {
            decStr = sb.toString();
        }

        return Integer.parseInt(decStr);
    }

    /**
     * 将BCD码转换为十进制的字符串。举例：
     * 如果BCD码为00010010，转换后的十进制字符串为"12"
     * 如果BCD码为00000010，转换后的十进制字符串为"2"
     * 如果BCD码为00000001 00100011，转换后的十进制字符串为"123"
     *
     * @param bcd BCD码
     * @return 十进制字符串
     */
    public static Integer oneBcdToDecimalInteger(byte bcd) {
        StringBuilder sb = new StringBuilder();
        // 存放转化后的十进制数字的字符串
        String decStr;
        // 每个字节的前四位的值右移4位转化为十进制数
        sb.append((bcd & 0XF0) >> 4);
        sb.append(bcd & 0X0F);


        // 如果转化后的字符串首字母为0，那么去掉
        if (sb.charAt(0) == '0') {
            decStr = sb.substring(1);
        } else {
            decStr = sb.toString();
        }

        return Integer.parseInt(decStr);
    }


    /**
     * 十进制字符串转BCD码。举例：
     * 如果十进制字符串为"2"，转换后的BCD码为00000010
     * 如果十进制字符串为"12"，转换后的BCD码为00010010
     * 如果十进制字符串为"123"，转换后的BCD码为00000001 00100011
     *
     * @param decStr 十进制字符串
     * @return BCD码
     * @throws Exception
     */
    public static byte[] decimalStringToBcd(String decStr) throws Exception {
        // 因为可能修改字符串的内容，所以构造StringBuffer
        StringBuilder sb = new StringBuilder(decStr);
        // 一个字节包含两个4位的BCD码，byte数组中要包含偶数个BCD码
        // 一个十进制字符对应4位BCD码，所以如果十进制字符串的长度是奇数，要在前面补一个0使长度成为偶数
        if ((sb.length() % 2) != 0) {
            sb.insert(0, '0');
        }

        // 两个十进制数字转换为BCD码后占用一个字节，所以存放BCD码的字节数等于十进制字符串长度的一半
        byte[] bcd = new byte[sb.length() / 2];
        for (int i = 0; i < sb.length(); ) {
            if (!Character.isDigit(sb.charAt(i)) || !Character.isDigit(sb.charAt(i + 1))) {
                throw new Exception("传入的十进制字符串包含非数字字符!");
            }
            // 每个字节的构成：用两位十进制数字运算的和填充，高位十进制数字左移4位+低位十进制数字
            bcd[i / 2] = (byte) ((Character.digit(sb.charAt(i), 10) << 4) + Character.digit(sb.charAt(i + 1), 10));
            // 字符串的每两个字符取出来一起处理，所以此处i的自增长要加2，而不是加1
            i += 2;
        }
        return bcd;
    }

    public static int getLane(byte b) {
        int by = oneBcdToDecimalInteger(b);
        int lane = 1;
        switch (by) {
            case 11:
                lane = 2;
                break;
            case 22:
                lane = 3;
                break;
            case 33:
                lane = 4;
                break;
            case 44:
                lane = 5;
                break;
            case 55:
                lane = 6;
                break;
            case 66:
                lane = 7;
                break;
            case 77:
                lane = 8;
                break;
            default:
                break;
        }

        return lane;

    }

    /**
     * 从byte字节中解析车辆数据信息
     * @param byteArray
     * @return
     */
    public static AwsPreCheckData byteArrayToObjData(byte[] byteArray) {
        AwsPreCheckData pre = new AwsPreCheckData();
        //获取车道数据
        int lane = getLane(byteArray[1]);
        pre.setLane(lane);
        //获取车速
        int speed = byteArray[5];
        pre.setSpeed((double) speed);
        //获取时间
        int year = oneBcdToDecimalInteger(byteArray[11]);
        int month = oneBcdToDecimalInteger(byteArray[12]);
        int day = oneBcdToDecimalInteger(byteArray[13]);
        int hour = oneBcdToDecimalInteger(byteArray[14]);
        int min = oneBcdToDecimalInteger(byteArray[15]);
        int sec = oneBcdToDecimalInteger(byteArray[15]);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-M-d H:m:s");
        Date date = null;
        try {
            date = dateFormat.parse(year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec);
            pre.setPassTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //获取轴数
        int axis = byteArray[17] / 10;
        pre.setAxisNum(axis);
        //获取车型
        int carType = byteArray[17];
        pre.setCarTypeId(carType);
        //获取总重量
        int l = 18;
        int total = 0;
        for (int i = 1; i <= axis * 2; i++) {
            byte[] bytes = Arrays.copyOfRange(byteArray, l, l + 3);
            int aa = bcdToDecimalInteger(bytes);
            total += aa;
            l += 3;
        }

        BigDecimal b1 = BigDecimal.valueOf(((double) total / 1000) * 100);
        double preAmt = b1.setScale(2, RoundingMode.HALF_UP).doubleValue();
        pre.setPreAmt(preAmt);
        return pre;

    }

    public static void main(String[] args) throws ParseException {
        byte[] byteArray = hexStrToByteArray("FF 22 01 00 41 46 E8 03 00 00 00 16 08 24 18 18 19 18 00 05 00 00 04 00 00 05 00 00 04 00 00 27 00 0D 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 65 85 ");
        int len = byteArray[4];

//         System.err.println(oneBcdToDecimalInteger(byteArray[1]));
        //  System.err.println((byteArray[1] & 0XF0)>> 4+(byteArray[1] & 0XF0));
        System.err.println("车道：" + getLane((byteArray[1])));

        //System.err.println(byteArray[4]);
        System.err.println("帧长：" + len);

        int speed = byteArray[5];
        System.err.println("车速：" + speed);
        int year = oneBcdToDecimalInteger(byteArray[11]);
        int month = oneBcdToDecimalInteger(byteArray[12]);
        int day = oneBcdToDecimalInteger(byteArray[13]);
        int hour = oneBcdToDecimalInteger(byteArray[14]);
        int min = oneBcdToDecimalInteger(byteArray[15]);
        int sec = oneBcdToDecimalInteger(byteArray[15]);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-M-d H:m:s");
        Date date = dateFormat.parse(year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec);
        System.err.println(date);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.err.println(df.format(date));
//        System.err.println(year+"年");
//        System.err.println(month+"月");
//        System.err.println(day+"日");
//        System.err.println(hour+"小时");
//        System.err.println(min+"分钟");
//        System.err.println(sec+"秒");

        // System.err.println("轴数：" + axis);

        int axis = byteArray[17] / 10;
        System.err.println("轴数：" + axis);
        int carType = byteArray[17] % 10;
        System.err.println("车型：" + carType);
        int l = 18;
        int left = 1;
        int right = 1;
        int total = 0;
        for (int i = 1; i <= axis * 2; i++) {
            byte[] bytes = Arrays.copyOfRange(byteArray, l, l + 3);
            int aa = bcdToDecimalInteger(bytes);
            total += aa;
            if (i <= axis) {
                System.err.println("轴" + left + "左轮" + aa + "kg");
                left++;
            } else {
                System.err.println("轴" + right + "右轮" + aa + "kg");
                right++;
            }
            l += 3;
        }
        System.err.println("总重：" + total + "kg");
//        byte[] bytes = Arrays.copyOfRange(byteArray, 18, 21);
//        byte[] bytes1 = Arrays.copyOfRange(byteArray, 21, 24);
//        String aa = bcdToDecimalString(bytes);
//        String bb = bcdToDecimalString(bytes1);
//        System.err.println(byteArray);
//        System.err.println(byteToInt(byteArray, 6));
//        System.err.println(Integer.parseInt(aa) + "--" + bb);


    }


}
