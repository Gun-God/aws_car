package com.aws.carno.Utils;

import com.aws.carno.domain.AwsPreCheckData;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

import static com.aws.carno.Utils.LedUtil.hexStr2Str;

/**
 * @author :hyw
 * @version 1.0
 * @date : 2023/06/20 11:38
 * @description 称台串口数据解析工具类
 */
public class RTXDataParse {

    private static int crcTable[] = {0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50A5, 0x60C6, 0x70E7,
            0x8108, 0x9129, 0xA14A, 0xB16B, 0xC18C, 0xD1AD, 0xE1CE, 0xF1EF,
            0x1231, 0x0210, 0x3273, 0x2252, 0x52B5, 0x4294, 0x72F7, 0x62D6,
            0x9339, 0x8318, 0xB37B, 0xA35A, 0xD3BD, 0xC39C, 0xF3FF, 0xE3DE,
            0x2462, 0x3443, 0x0420, 0x1401, 0x64E6, 0x74C7, 0x44A4, 0x5485,
            0xA56A, 0xB54B, 0x8528, 0x9509, 0xE5EE, 0xF5CF, 0xC5AC, 0xD58D,
            0x3653, 0x2672, 0x1611, 0x0630, 0x76D7, 0x66F6, 0x5695, 0x46B4,
            0xB75B, 0xA77A, 0x9719, 0x8738, 0xF7DF, 0xE7FE, 0xD79D, 0xC7BC,
            0x48C4, 0x58E5, 0x6886, 0x78A7, 0x0840, 0x1861, 0x2802, 0x3823,
            0xC9CC, 0xD9ED, 0xE98E, 0xF9AF, 0x8948, 0x9969, 0xA90A, 0xB92B,
            0x5AF5, 0x4AD4, 0x7AB7, 0x6A96, 0x1A71, 0x0A50, 0x3A33, 0x2A12,
            0xDBFD, 0xCBDC, 0xFBBF, 0xEB9E, 0x9B79, 0x8B58, 0xBB3B, 0xAB1A,
            0x6CA6, 0x7C87, 0x4CE4, 0x5CC5, 0x2C22, 0x3C03, 0x0C60, 0x1C41,
            0xEDAE, 0xFD8F, 0xCDEC, 0xDDCD, 0xAD2A, 0xBD0B, 0x8D68, 0x9D49,
            0x7E97, 0x6EB6, 0x5ED5, 0x4EF4, 0x3E13, 0x2E32, 0x1E51, 0x0E70,
            0xFF9F, 0xEFBE, 0xDFDD, 0xCFFC, 0xBF1B, 0xAF3A, 0x9F59, 0x8F78,
            0x9188, 0x81A9, 0xB1CA, 0xA1EB, 0xD10C, 0xC12D, 0xF14E, 0xE16F,
            0x1080, 0x00A1, 0x30C2, 0x20E3, 0x5004, 0x4025, 0x7046, 0x6067,
            0x83B9, 0x9398, 0xA3FB, 0xB3DA, 0xC33D, 0xD31C, 0xE37F, 0xF35E,
            0x02B1, 0x1290, 0x22F3, 0x32D2, 0x4235, 0x5214, 0x6277, 0x7256,
            0xB5EA, 0xA5CB, 0x95A8, 0x8589, 0xF56E, 0xE54F, 0xD52C, 0xC50D,
            0x34E2, 0x24C3, 0x14A0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405,
            0xA7DB, 0xB7FA, 0x8799, 0x97B8, 0xE75F, 0xF77E, 0xC71D, 0xD73C,
            0x26D3, 0x36F2, 0x0691, 0x16B0, 0x6657, 0x7676, 0x4615, 0x5634,
            0xD94C, 0xC96D, 0xF90E, 0xE92F, 0x99C8, 0x89E9, 0xB98A, 0xA9AB,
            0x5844, 0x4865, 0x7806, 0x6827, 0x18C0, 0x08E1, 0x3882, 0x28A3,
            0xCB7D, 0xDB5C, 0xEB3F, 0xFB1E, 0x8BF9, 0x9BD8, 0xABBB, 0xBB9A,
            0x4A75, 0x5A54, 0x6A37, 0x7A16, 0x0AF1, 0x1AD0, 0x2AB3, 0x3A92,
            0xFD2E, 0xED0F, 0xDD6C, 0xCD4D, 0xBDAA, 0xAD8B, 0x9DE8, 0x8DC9,
            0x7C26, 0x6C07, 0x5C64, 0x4C45, 0x3CA2, 0x2C83, 0x1CE0, 0x0CC1,
            0xEF1F, 0xFF3E, 0xCF5D, 0xDF7C, 0xAF9B, 0xBFBA, 0x8FD9, 0x9FF8,
            0x6E17, 0x7E36, 0x4E55, 0x5E74, 0x2E93, 0x3EB2, 0x0ED1, 0x1EF0
    };


    //16进制转byte数组
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

    //2进制转换16进制字符串
    private static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }




    public static byte[] setTimeTextGen(String charset) throws Exception {
        //GB18030
        StringBuilder buffer = new StringBuilder();
        //帧头
        buffer.append("FF");
        buffer.append(" ");
        //保留字
        buffer.append("00");
        buffer.append(" ");
        //主机命令
        buffer.append("03");
        buffer.append(" ");
        //帧长
        buffer.append("00 0E");
        buffer.append(" ");
        //年月日时分秒
        // 获取当前日期和时间
        LocalDateTime now = LocalDateTime.now();

        // 提取年、月、日、时、分、秒
        int year = now.getYear() % 100; // 获取年份并取后两位
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        System.out.println("称台校时时间："+year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second);
//        int hour = 0;
//        int minute=0;
//        int second = 0;
//        int year = 23; // 获取年份并取后两位
//        int month = 11;
//        int day = 20;
//        int hour = 2;
//        int minute = 40;
//        int second = 28;
        byte[] ye=decimalStringToBcd(Integer.toString(year));
        buffer.append(bytesToHexString(ye));
        buffer.append(" ");
        byte[] mo=decimalStringToBcd(Integer.toString(month));
        buffer.append(bytesToHexString(mo));
        buffer.append(" ");
        byte[] da=decimalStringToBcd(Integer.toString(day));
        buffer.append(bytesToHexString(da));
        buffer.append(" ");
        byte[] ho=decimalStringToBcd(Integer.toString(hour));
        buffer.append(bytesToHexString(ho));
        buffer.append(" ");
        byte[] mi=decimalStringToBcd(Integer.toString(minute));
        buffer.append(bytesToHexString(mi));
        buffer.append(" ");
        byte[] se=decimalStringToBcd(Integer.toString(second));
        buffer.append(bytesToHexString(se));


//        buffer.append("2D");
//        buffer.append(" ");

        //生成三位CRC校验码
        int[] data_crc=crcCreate(RTXDataParse.hexStrToByteArray(buffer.toString()));
        buffer.append(" ");
        if(data_crc[0]<10 && data_crc[0]>=0)
        {
            buffer.append("0");
        }

        String hex_1=Integer.toHexString(data_crc[0]);
        buffer.append(hex_1);
        buffer.append(" ");
        if(data_crc[1]<10 && data_crc[1]>=0)
        {
            buffer.append("0");
        }
        String hex_2=Integer.toHexString(data_crc[1]);
        buffer.append(hex_2);
        buffer.append(" ");
        if(data_crc[2]<10 && data_crc[2]>=0)
        {
            buffer.append("0");
        }
        String hex_3=Integer.toHexString(data_crc[2]);
        buffer.append(hex_3);

        //帧尾
        System.err.println("16进制：" + buffer);
//        String m3 = hexStr2Str(buffer.toString(), charset);
//        System.err.println("字符串：" + m3);
        return RTXDataParse.hexStrToByteArray(buffer.toString());
    }



    // 生成校验码
    public static int[] crcCreate(byte[] data) {
        int crc = calculateCRC16(data);
        byte[] bcd = new byte[5];
        int[] data_crc=new int[3];
         nm2Bcd(crc,bcd,5);
        int len = data.length;

        if (len == 0) {
            return null;
        }

        data_crc[0] = bcd[0];
        data_crc[1] = ((((bcd[1] & 0xff ) << 4)&0xff) | bcd[2]);
        data_crc[2] = ((((bcd[3] & 0xff ) << 4)&0xff) | bcd[4]);
        return data_crc;
    }


    public static void nm2Bcd(int num, byte[] buf, int len) {
        if (len == 0) {
            return;
        }

        // Convert to decimal
        for (int i = 0; i < len; i++) {
            buf[len - 1 - i] = (byte) (num % 10);
            num = num / 10;
        }
    }

    // 计算 crc16 的值
    public static int calculateCRC16(byte[] bytes) {
        int crc = 0;
        for (byte b : bytes) {
            int da = (crc >> 8) & 0xFF;
            crc=((crc & 0xFF)<<8);
            crc ^= crcTable[(da ^ b) & 0xFF];
        }
        return crc;
}


    public static int getLane(byte b) {
        int by = oneBcdToDecimalInteger(b);
        return ((by / 10) % 10)+1;

//        if(by<10)
//        {
//            lane=1;
//        }
//        else{
//            lane=by/10;
//            lane=lane+1;
//        }
//
//        switch (by) {
//            case 11:
//                lane = 2;
//                break;
//            case 22:
//                lane = 3;
//                break;
//            case 33:
//                lane = 4;
//                break;
//            case 44:
//                lane = 5;
//                break;
//            case 55:
//                lane = 6;
//                break;
//            case 66:
//                lane = 7;
//                break;
//            case 77:
//                lane = 8;
//                break;
//            default:
//                break;
//        }



    }

    /**
     * 从byte字节中解析车辆数据信息
     *
     * @param byteArray
     * @return
     */
    public static AwsPreCheckData byteArrayToObjData(byte[] byteArray) {

        AwsPreCheckData pre = new AwsPreCheckData();
        //获取车道数据
        int lane = getLane(byteArray[1]);
        pre.setLane(lane);//车道算法需要修改
        //获取车速
        int speed = byteArray[5];
        pre.setSpeed((double) speed);
        //获取时间
        int year = oneBcdToDecimalInteger(byteArray[11]);
        int month = oneBcdToDecimalInteger(byteArray[12]);
        int day = oneBcdToDecimalInteger(byteArray[13]);
        int hour = oneBcdToDecimalInteger(byteArray[14]);
        int min = oneBcdToDecimalInteger(byteArray[15]);
        int sec = oneBcdToDecimalInteger(byteArray[16]);

//        for(int i=0;i<b;i++)

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
//        int carType = byteArray[17] % 10;
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
        pre.setWeight((double)total);

        BigDecimal b1 = BigDecimal.valueOf(((double) total / 1000));
        double preAmt = b1.setScale(2, RoundingMode.HALF_UP).doubleValue();
        pre.setPreAmt(preAmt);
        return pre;

    }

    public static void main(String[] args) throws Exception {
//        byte[] byteArray = hexStrToByteArray("ff 55 01 00 41 41 e3 46 01 00 00 23 10 19 17 28 38 16 00 02 90 00 04 30 00 02 90 00 04 30 00 28 34 16 31 32 43 31 32 33 33 44 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 af 49 64 00 04 34 05 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ");
//        int len = byteArray[4];
//
////         System.err.println(oneBcdToDecimalInteger(byteArray[1]));
//        //  System.err.println((byteArray[1] & 0XF0)>> 4+(byteArray[1] & 0XF0));
//        System.err.println("车道：" + getLane((byteArray[1])));
//
//        //System.err.println(byteArray[4]);
//        System.err.println("帧长：" + len);
//
//        int speed = byteArray[5];
//        System.err.println("车速：" + speed);
//        int year = oneBcdToDecimalInteger(byteArray[11]);
//        int month = oneBcdToDecimalInteger(byteArray[12]);
//        int day = oneBcdToDecimalInteger(byteArray[13]);
//        int hour = oneBcdToDecimalInteger(byteArray[14]);
//        int min = oneBcdToDecimalInteger(byteArray[15]);
//        int sec = oneBcdToDecimalInteger(byteArray[15]);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-M-d H:m:s");
//        Date date = dateFormat.parse(year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec);
//        System.err.println(date);
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.err.println(df.format(date));
////        System.err.println(year+"年");
////        System.err.println(month+"月");
////        System.err.println(day+"日");
////        System.err.println(hour+"小时");
////        System.err.println(min+"分钟");
////        System.err.println(sec+"秒");
//
//        // System.err.println("轴数：" + axis);
//
//        int axis = byteArray[17] / 10;
//        System.err.println("轴数：" + axis);
//        int carType = byteArray[17] % 10;
//        System.err.println("车型：" + carType);
//        int l = 18;
//        int left = 1;
//        int right = 1;
//        int total = 0;
//        for (int i = 1; i <= axis * 2; i++) {
//            byte[] bytes = Arrays.copyOfRange(byteArray, l, l + 3);
//            int aa = bcdToDecimalInteger(bytes);
//            total += aa;
//            if (i <= axis) {
//                System.err.println("轴" + left + "左轮" + aa + "kg");
//                left++;
//            } else {
//                System.err.println("轴" + right + "右轮" + aa + "kg");
//                right++;
//            }
//            l += 3;
//        }
//        System.err.println("总重：" + total + "kg");

//        int a=5;
//        System.err.println(((a/10)%10)+1);

        byte[] bytes= setTimeTextGen("UTF-8");


//        byte[] bytes = Arrays.copyOfRange(byteArray, 18, 21);
//        byte[] bytes1 = Arrays.copyOfRange(byteArray, 21, 24);
//        String aa = bcdToDecimalString(bytes);
//        String bb = bcdToDecimalString(bytes1);
//        System.err.println(byteArray);
//        System.err.println(byteToInt(byteArray, 6));
//        System.err.println(Integer.parseInt(aa) + "--" + bb);


    }


}
