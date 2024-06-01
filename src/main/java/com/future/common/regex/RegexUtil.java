package com.future.common.regex;

import com.future.common.phone.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    private final static Pattern PatternInteger = Pattern.compile("[0-9]+");

    /**
     * 判断号码格式是否正确
     * @param phone
     */
    public static void isPhone(String phone) {
        PhoneUtil.isPhone(phone);
    }

    /**
     * 判断号码是否移动类型号码
     * @param phone
     */
    public static void isMobilePhone(String phone) {
        PhoneUtil.isMobile(phone);
    }

    /**
     * 
     * @param email
     */
    public static void isEmail(String email) {
        Assert.isTrue(!StringUtils.isBlank(email), "没有指定email参数");
        boolean b = EmailValidator.getInstance().isValid(email);
        if(!b) {
            throw new IllegalArgumentException("email：" + email + "非邮箱格式");
        }
    }

    /**
     * 判断输入参数是否为整形
     *
     * @param text
     * @return
     */
    public static boolean isInteger(String text) {
        Assert.isTrue(!StringUtils.isBlank(text), "没有指定text参数");
        Matcher mth = PatternInteger.matcher(text);
        return mth.matches();
    }
}
