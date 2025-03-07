package com.future.common.phone;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.commons.lang3.StringUtils;

public class PhoneUtil {
    final static PhoneNumberUtil PhoneNumberUtilInstance = PhoneNumberUtil.getInstance();

    /**
     * 判断号码格式是否正确
     *
     * @param phone
     * @return
     */
    public static boolean isValid(String phone) {
        try {
            isPhone(phone);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    /**
     * 判断号码格式是否正确
     *
     * @param phone
     */
    public static void isPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            throw new IllegalArgumentException("没有指定号码");
        }

        try {
            Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtilInstance.parse(phone, null);
            PhoneNumberUtilInstance.getNumberType(phoneNumber);
        } catch (NumberParseException e) {
            // 号码格式错误
            String message = String.format("号码=%s格式错误，必需为E.164格式：+[国家代号][号码]，例如：+8613512345678", phone);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 判断号码是否移动类型号码
     *
     * @param phone
     * @return
     */
    public static void isMobile(String phone) {
        if (StringUtils.isBlank(phone)) {
            throw new IllegalArgumentException("没有指定号码");
        }

        try {
            Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtilInstance.parse(phone, null);
            PhoneNumberUtil.PhoneNumberType phoneNumberType = PhoneNumberUtilInstance.getNumberType(phoneNumber);
            if (PhoneNumberUtil.PhoneNumberType.MOBILE != phoneNumberType) {
                String message = String.format("号码=%s非手机类型号码", phone);
                throw new IllegalArgumentException(message);
            }
        } catch (NumberParseException e) {
            // 号码格式错误
            String message = String.format("号码=%s格式错误，必需为E.164格式：+[国家代号][手机号码]，例如：+8613512345678", phone);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 获取号码countryCode
     *
     * @param phone
     * @return
     */
    public static int getCountryCode(String phone) {
        if (StringUtils.isBlank(phone)) {
            throw new IllegalArgumentException("没有指定号码");
        }

        try {
            Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtilInstance.parse(phone, null);
            return phoneNumber.getCountryCode();
        } catch (NumberParseException e) {
            // 号码格式错误
            String message = String.format("号码=%s格式错误，必需为E.164格式：+[国家代号][手机号码]，例如：+8613512345678", phone);
            throw new IllegalArgumentException(message);
        }
    }
}
