package com.future.common.net;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.junit.Assert;
import org.junit.Test;

public class IpUtilsTests {
    @Test
    public void test() {
        //region 测试ipv4
        for (int i = 0; i < 10000; i++) {
            String ip = IpUtils.randomIpv4();
            Assert.assertTrue(InetAddressValidator.getInstance().isValidInet4Address(ip));
            Assert.assertTrue(InetAddressValidator.getInstance().isValid(ip));
        }
        //endregion

        //region 测试ipv6
        for (int i = 0; i < 10000; i++) {
            String ip = IpUtils.randomIpv6();
            Assert.assertTrue(InetAddressValidator.getInstance().isValidInet6Address(ip));
            Assert.assertTrue(InetAddressValidator.getInstance().isValid(ip));
        }
        //endregion
    }
}
