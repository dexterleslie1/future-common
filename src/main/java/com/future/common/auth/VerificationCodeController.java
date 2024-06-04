package com.future.common.auth;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// todo swagger
@RestController
@RequestMapping(value = "/api/v1/verificationCode")
@Slf4j
public class VerificationCodeController {
    @Autowired
    VerificationCodeService verificationCodeService;

    /**
     * 获取验证码
     *
     * @return
     */
    @GetMapping(value = "get")
    public ObjectResponse<Integer> get(
            @RequestParam(value = "phone", defaultValue = "") String phone) {
        int ttlInSeconds = this.verificationCodeService.get(phone);
        return ResponseUtils.successObject(ttlInSeconds);
    }
}
