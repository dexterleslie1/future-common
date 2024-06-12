package com.future.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.function.Consumer;

/**
 * jwt工具类
 */
public class JwtUtil {
    /**
     * 使用私钥生成jwt token
     *
     * @param privateKey
     * @param callback 调用者通过回传的JWTCreator.Builder自定义claim、expiresAt等
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static String signWithPrivateKey(String privateKey, Consumer<JWTCreator.Builder> callback) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (StringUtils.isBlank(privateKey)) {
            throw new IllegalArgumentException("没有指定私钥");
        }

        byte[] privateKeyBytes = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.decode(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPrivateKey privateKeyObject = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        Algorithm algorithm = Algorithm.RSA512(null, privateKeyObject);

        JWTCreator.Builder builder = JWT.create();
        callback.accept(builder);
        return builder.sign(algorithm);
    }

    /**
     * 使用公钥验证jwt token
     *
     * @param publicKey
     * @param token
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static DecodedJWT verifyWithPublicKey(String publicKey, String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyBytes = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.decode(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey publicKeyObject = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        Algorithm algorithm = Algorithm.RSA512(publicKeyObject, null);
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}
