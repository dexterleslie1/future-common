package com.future.common.auth.controller;

import com.future.common.auth.dto.LoginSuccessDto;
import com.future.common.auth.service.UserService;
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/future/auth")
@Slf4j
public class RegisterAndLoginController {
    @Autowired
    UserService userService;

    /**
     * 注册
     *
     * @return
     */
    @PostMapping(value = "register")
    public ObjectResponse<String> register(
            @RequestParam(name = "phone", defaultValue = "") String phone,
            @RequestParam(name = "nickname", defaultValue = "") String nickname,
            @RequestParam(name = "password", defaultValue = "") String password,
            @RequestParam(name = "verificationCode", defaultValue = "") String verificationCode) throws BusinessException {
        this.userService.register(phone, nickname, password, verificationCode);
        return ResponseUtils.successObject("注册成功");
    }

    /**
     * 登录
     *
     * @param phone
     * @param password
     * @return
     * @throws BusinessException
     */
    @PostMapping(value = "login")
    public ObjectResponse<LoginSuccessDto> login(
            @RequestParam(name = "phone", defaultValue = "") String phone,
            @RequestParam(name = "password", defaultValue = "") String password) throws Exception {
        return ResponseUtils.successObject(this.userService.login(phone, password));
    }
}
