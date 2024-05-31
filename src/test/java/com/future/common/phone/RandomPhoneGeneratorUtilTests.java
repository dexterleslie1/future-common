package com.future.common.phone;

import org.junit.Test;

public class RandomPhoneGeneratorUtilTests {
    @Test
    public void test() {
        String randomPhone = RandomPhoneGeneratorUtil.getRandom();
        PhoneUtil.isPhone(randomPhone);
    }
}
