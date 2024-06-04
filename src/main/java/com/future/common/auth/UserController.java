package com.future.common.auth;


import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/user")
@Slf4j
public class UserController {
    @Autowired
    UserService userService;

    /**
     * 获取个人信息
     *
     * @return
     */
    @GetMapping("getInfo")
    public ObjectResponse<UserDto> getInfo() throws BusinessException {
        return ResponseUtils.successObject(this.userService.get(0L));
    }
}
