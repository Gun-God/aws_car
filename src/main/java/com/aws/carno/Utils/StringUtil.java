package com.aws.carno.Utils;




import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具类
 */
public class StringUtil {
    // 静态变量存储最大值
    private static final AtomicInteger atomicNum = new AtomicInteger();
    private static String nowDate;


    /**
     * 字符串左对齐
     *
     * @param src
     * @param len
     * @param ch
     * @return
     */
    public static String padLeft(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
        for (int i = src.length(); i < len; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }

    /**
     * 字符串右对齐
     *
     * @param src
     * @param len
     * @param ch
     * @return
     */
    public static String padRight(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
        for (int i = 0; i < diff; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }


    // 生成唯一流水号，规则6位时间+3位随机数+5位自增长序号
    //这里加锁不是保证唯一，而是保证有序
    public static synchronized String genNo() {
        SimpleDateFormat sm = new SimpleDateFormat("yyMMdd");
        if (!Objects.equals(nowDate, sm.format(new Date()))) {
            nowDate = sm.format(new Date());
            atomicNum.set(0);
        }
        //生成3位随机数
        String randomNumeric = randomNumeric(3);
        //线程安全的原子操作，所以此方法无需同步
        int newNum = atomicNum.incrementAndGet();
        //数字长度为5位，长度不够数字前面补0
        String newStrNum = String.format("%05d", newNum);
        System.err.println("流水号：" + nowDate + randomNumeric + newStrNum);

        return nowDate + randomNumeric + newStrNum;
    }


    public static String randomNumeric(int len) {
        double v = Math.random() * 9 + 1;
        double pow = Math.pow(10, len - 1);
        int rs = (int) (v * pow);
        return String.valueOf(rs);
    }

    /**
     *利用正则表达式从字符串中获取ip
     */
    public static String getIpInLong(String ipLong)
    {
//        String regex="^(?:[0-9]\\.){3}[0-9]{1,3}$";
        String regex="\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";

        Pattern pattern=Pattern.compile(regex);

        Matcher matcher=pattern.matcher(ipLong);
        String ipstr="";
        if(matcher.find())
        {
            ipstr=matcher.group();
        }
        //System.err.println(ipstr);
        return ipstr;
    }


    public static void main(String[] args) throws ParseException {
        // 创建 2 个线程的线程池
//        ExecutorService threadPool = Executors.newFixedThreadPool(8);
//        for (int i = 0; i < 100000; i++) {
//            //  System.out.println("任务被执行,线程:" + Thread.currentThread().getName());
//            threadPool.execute(StringUtil::genNo);
//        }
//        while (true) {
//            if (!threadPool.isTerminated()) {
//                System.err.println("运行完毕！");
//                return;
//            }
//        }
        int year=1997;
        int month=12;
        int day=7;
        int hour=6;
        int min=10;
        int sec=5;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-M-d H:m:s");
        Date passTime = dateFormat.parse(year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec);

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String no = sf.format(passTime);
//        String no=year+""+month+""+day;
        System.err.println(no);


    }
}