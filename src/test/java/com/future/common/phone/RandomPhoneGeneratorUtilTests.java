package com.future.common.phone;

import org.junit.Assert;
import org.junit.Test;

public class RandomPhoneGeneratorUtilTests {
    @Test
    public void test() {
        String randomPhone = RandomPhoneGeneratorUtil.getRandom();
        PhoneUtil.isPhone(randomPhone);

        boolean valid = PhoneUtil.isValid(randomPhone);
        Assert.assertTrue(valid);

        String phone = "13511111111";
        valid = PhoneUtil.isValid(phone);
        Assert.assertFalse(valid);
    }
}
