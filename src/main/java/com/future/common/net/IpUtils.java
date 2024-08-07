package com.future.common.net;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

/**
 * ipv4和ipv6相关的工具类
 */
public class IpUtils {
    /**
     * ipv6每个部分的字符范围，生成ipv6时被用于随机抽取
     */
    private final static String Ipv6RandomStringChosenFrom = "0123456789abcdef";
    /**
     * ipv6每个部分的字符数
     */
    private final static int Ipv6PartLength = 4;
    /**
     * ipv4每个部分的最大值
     */
    private final static int Ipv4MaximumInt = 255;
    private final static Random R = new Random();

    /**
     * 随机生成ipv4地址
     *
     * @return
     */
    public static String randomIpv4() {
        int rand1 = R.nextInt(Ipv4MaximumInt) + 1;
        int rand2 = R.nextInt(Ipv4MaximumInt) + 1;
        int rand3 = R.nextInt(Ipv4MaximumInt) + 1;
        int rand4 = R.nextInt(Ipv4MaximumInt) + 1;
        return rand1 + "." + rand2 + "." + rand3 + "." + rand4;
    }

    /**
     * 随机生成ipv6地址
     *
     * @return
     */
    public static String randomIpv6() {
        String part1 = RandomStringUtils.random(Ipv6PartLength, Ipv6RandomStringChosenFrom);
        String part2 = RandomStringUtils.random(Ipv6PartLength, Ipv6RandomStringChosenFrom);
        String part3 = RandomStringUtils.random(Ipv6PartLength, Ipv6RandomStringChosenFrom);
        String part4 = RandomStringUtils.random(Ipv6PartLength, Ipv6RandomStringChosenFrom);
        String part5 = RandomStringUtils.random(Ipv6PartLength, Ipv6RandomStringChosenFrom);
        String part6 = RandomStringUtils.random(Ipv6PartLength, Ipv6RandomStringChosenFrom);
        String part7 = RandomStringUtils.random(Ipv6PartLength, Ipv6RandomStringChosenFrom);
        String part8 = RandomStringUtils.random(Ipv6PartLength, Ipv6RandomStringChosenFrom);
        return part1 + ":" + part2 + ":" + part3 + ":" + part4 + ":" + part5 + ":" +
                part6 + ":" + part7 + ":" + part7;
    }
}
