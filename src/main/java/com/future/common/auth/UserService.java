package com.future.common.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.future.common.bean.ModelMapperUtil;
import com.future.common.exception.BusinessException;
import com.future.common.phone.PhoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.UUID;


@Slf4j
public class UserService extends ServiceImpl<UserMapper, User> {
    @Autowired
    VerificationCodeService verificationCodeService;

    /**
     * 用户注册
     * @param phone
     * @param nickname
     * @param password
     * @param verificationCode
     * @throws BusinessException
     */
    public void register(String phone, String nickname, String password, String verificationCode) throws BusinessException {
        Assert.isTrue(!StringUtils.isBlank(phone), "请指定手机号码");
        Assert.isTrue(!StringUtils.isBlank(nickname), "请指定昵称");
        Assert.isTrue(!StringUtils.isBlank(password), "请指定登录密码");

        PhoneUtil.isPhone(phone);

        // 判断手机号码是否已经存在
        // todo 并发锁定
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user = this.getOne(queryWrapper, true);
        if (user != null) {
            if (log.isDebugEnabled()) {
                log.debug("手机号码 {} 已经存在", phone);
            }

            throw new BusinessException("手机号码 " + phone + " 已经存在");
        }

        // 校验验证码
        this.verificationCodeService.verify(phone, verificationCode);

        user = new User();
        user.setPhone(phone);
        user.setNickname(nickname);
        user.setPassword(password);
        user.setCreateTime(new Date());
        this.save(user);
    }

    /**
     * 用户登录
     * @param phone
     * @param password
     * @return
     * @throws BusinessException
     */
    public LoginSuccessDto login(String phone, String password) throws BusinessException {
        Assert.isTrue(!StringUtils.isBlank(phone), "请指定手机号码");
        Assert.isTrue(!StringUtils.isBlank(password), "请指定登录密码");

        PhoneUtil.isPhone(phone);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone).eq("password", password);
        User user = this.getOne(queryWrapper, true);

        if (user == null) {
            if (log.isDebugEnabled()) {
                log.debug("尝试使用手机号码 {} 密码 {} 登录但失败", phone, password);
            }

            throw new BusinessException("提供的手机号码或者密码错误！");
        }

        // todo 使用jwt
        LoginSuccessDto loginSuccessDto = new LoginSuccessDto();
        loginSuccessDto.setUserId(user.getId());
        loginSuccessDto.setPhone(user.getPhone());
        String refreshToken = UUID.randomUUID().toString();
        loginSuccessDto.setRefreshToken(refreshToken);
        String accessToken = UUID.randomUUID().toString();
        loginSuccessDto.setAccessToken(accessToken);
        return loginSuccessDto;
    }

    /**
     * 根据id查询个人资料
     * @param id
     * @return
     * @throws BusinessException
     */
    public UserDto get(Long id) throws BusinessException {
        Assert.isTrue(id != null && id > 0, "请指定用户id");
        User user = this.getById(id);
        if (user == null) {
            if (log.isDebugEnabled()) {
                log.debug("查询的用户id {} 不存在", id);
            }

            throw new BusinessException("用户不存在！");
        }

        return ModelMapperUtil.ModelMapperInstance.map(user, UserDto.class);
    }
}
