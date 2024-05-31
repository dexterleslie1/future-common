package com.future.common.phone;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

/**
 * 手机号码随机生成工具
 */
public class RandomPhoneGeneratorUtil {
    private final static String []PhonePrefixs = new String []{"135", "159", "130"};
    private final static Random RandomFactor = new Random();

    /**
     * 随机生成手机号码
     * @return
     */
    public static String getRandom(){
        int prefixLength = PhonePrefixs.length;
        int randomIndex = RandomFactor.nextInt(prefixLength);
        String phonePrefix = PhonePrefixs[randomIndex];
        String randomPhoneSuffix = RandomStringUtils.randomNumeric(8);
        String randomPhone = phonePrefix + randomPhoneSuffix;
        randomPhone = "+86" + randomPhone;
        return randomPhone;
    }
}
