package com.aws.carno.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class testObject {
    public static void main(String[] args) throws ParseException {
        NET_DVR_TIME_V30 snapTime = new NET_DVR_TIME_V30();
        snapTime.wYear = (short)18;
        snapTime.byMonth = 12;
        snapTime.byDay = 31;
        snapTime.byHour = 23;
        snapTime.byMinute = 59;
        snapTime.bySecond = 59;

        int year = snapTime.wYear + 2000; // 注意：NET_DVR_TIME_V30中的年份是从2000年开始的
        int month = snapTime.byMonth - 1; // 注意：月份是从0开始的
        int day = snapTime.byDay;
        int hour = snapTime.byHour;
        int min = snapTime.byMinute;
        int sec = snapTime.bySecond;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-M-d H:m:s");
        Date passTime = dateFormat.parse(year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec);
        System.err.println(passTime);
        System.err.println(passTime.getTime());
    }
}

class NET_DVR_TIME_V30 {
    public short wYear;
    public byte byMonth;
    public byte byDay;
    public byte byHour;
    public byte byMinute;
    public byte bySecond;
    public byte byRes;
    public short wMilliSec;
    public byte[] byRes1 = new byte[2];
}
