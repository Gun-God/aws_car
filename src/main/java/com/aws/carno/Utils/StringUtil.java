package com.aws.carno.Utils;




import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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


    public static void main(String[] args) {
        // 创建 2 个线程的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 100000; i++) {
            //  System.out.println("任务被执行,线程:" + Thread.currentThread().getName());
            threadPool.execute(StringUtil::genNo);
        }
        while (true) {
            if (!threadPool.isTerminated()) {
                System.err.println("运行完毕！");
                return;
            }
        }

    }
}