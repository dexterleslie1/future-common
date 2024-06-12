package com.future.common.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JwtUtilTests {
    @Test
    public void test() throws InvalidKeySpecException, NoSuchAlgorithmException, InterruptedException {
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAN2RYIkshdnM0JwZ 2K6RGXlJ3QrmwDBeOgK0fa8y8wTBM2a48jEQApWiKdLz5Qd6T8JivIJEOIk+SJyQ azXiT5lXoH+zJyUa9Gn4Kmmy4rstvZhutTltvWnISgpYZaEuf5UhMhQZ1x8PMECb QIH76IrTvkKBn3rbGtIhPVIdNqNbAgMBAAECgYBymkFecmMBVskIFO5YzMBqVeQ5 QUVjPzmuMIFZ33aHnQ7jMQJEkk1j7C7J6FEsXv1mQ//ROUws4MRaBvENO/ODHHxr jWQHTXdBTBFTg2BxexEMM+xuE+jgM+INCBqhbKGC2t7shjvLDpciq6n2pmIYON86 CjA3yGO2ybW5bNG/cQJBAPpBcsIIqrdcRUfYW1hXvkPnnCyH+ub5y9F+YmPGxrA3 CGXNMbq3A/Z4nauMtSyniTteguCunbBCZgTTUNIS7U8CQQDip1qQPon3GNrhRLEr xAegPpomWaKiUm0BfMKg1yZxRYxCygqOERFGpcYbmvOMkESmeGgeppEg6SI51UQc L941AkAIF2uleHBEo3gd/ZHehl/BhFCZZAApzbPYXzTBMyEje3QLDppoBhjbtESs 0kzdV8FpKQIkT6ELnOn9h/OaB0CjAkEAmAukWTda3gQycQPfxnhOlVTpm+htjW61 6VWEStmJ1FrPaM6Yng0dBcOXTlV4JEdzhiknz8f1e5ppd1p1wmLqMQJBAN3mTvNM /iWXD/HSSkizAqM3tDBXULZwsr7mQppSd6Lz7XvBtrtwLRGK4PcmIq+0l4SeT3CO MJ8yh9yPsqEo5M0=";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDdkWCJLIXZzNCcGdiukRl5Sd0K 5sAwXjoCtH2vMvMEwTNmuPIxEAKVoinS8+UHek/CYryCRDiJPkickGs14k+ZV6B/ syclGvRp+CppsuK7Lb2YbrU5bb1pyEoKWGWhLn+VITIUGdcfDzBAm0CB++iK075C gZ962xrSIT1SHTajWwIDAQAB";

        // 测试claim
        Long userId = 10L;
        String username = "dexter1";
        String token = JwtUtil.signWithPrivateKey(privateKey, o -> {
            o.withClaim("userId", userId)
                    .withClaim("username", username)
                    .withClaim("menuList", Arrays.asList("menu1", "menu2"));
        });
        DecodedJWT decodedJWT = JwtUtil.verifyWithPublicKey(publicKey, token);
        Assert.assertEquals(userId, decodedJWT.getClaim("userId").asLong());
        Assert.assertEquals(username, decodedJWT.getClaim("username").asString());
        Assert.assertEquals(2, decodedJWT.getClaim("menuList").asArray(String.class).length);
        Assert.assertEquals("menu1", decodedJWT.getClaim("menuList").asArray(String.class)[0]);
        Assert.assertEquals("menu2", decodedJWT.getClaim("menuList").asArray(String.class)[1]);

        // 测试过期
        token = JwtUtil.signWithPrivateKey(privateKey, o -> {
            o.withClaim("userId", userId)
                    .withClaim("username", username)
                    .withClaim("menuList", Arrays.asList("menu1", "menu2"));
            Date timeNow = new Date();
            Date expiresAt = DateUtils.addSeconds(timeNow, 10);
            o.withExpiresAt(expiresAt);
        });
        decodedJWT = JwtUtil.verifyWithPublicKey(publicKey, token);
        Assert.assertEquals(userId, decodedJWT.getClaim("userId").asLong());
        Assert.assertEquals(username, decodedJWT.getClaim("username").asString());
        Assert.assertEquals(2, decodedJWT.getClaim("menuList").asArray(String.class).length);
        Assert.assertEquals("menu1", decodedJWT.getClaim("menuList").asArray(String.class)[0]);
        Assert.assertEquals("menu2", decodedJWT.getClaim("menuList").asArray(String.class)[1]);

        token = JwtUtil.signWithPrivateKey(privateKey, o -> {
            o.withClaim("userId", userId)
                    .withClaim("username", username)
                    .withClaim("menuList", Arrays.asList("menu1", "menu2"));
            Date timeNow = new Date();
            Date expiresAt = DateUtils.addSeconds(timeNow, 2);
            o.withExpiresAt(expiresAt);
        });
        TimeUnit.SECONDS.sleep(3);
        try {
            JwtUtil.verifyWithPublicKey(publicKey, token);
            Assert.fail("预期异常没有抛出");
        } catch (TokenExpiredException ignored) {

        }
    }
}
