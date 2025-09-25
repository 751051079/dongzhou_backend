package com.smarterp.auth.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.auth.domain.vo.SysDeptVO;
import com.smarterp.common.core.constant.CacheConstants;
import com.smarterp.common.core.constant.Constants;
import com.smarterp.common.core.constant.SecurityConstants;
import com.smarterp.common.core.constant.UserConstants;
import com.smarterp.common.core.domain.R;
import com.smarterp.common.core.enums.UserStatus;
import com.smarterp.common.core.exception.ServiceException;
import com.smarterp.common.core.text.Convert;
import com.smarterp.common.core.utils.StringUtils;
import com.smarterp.common.core.utils.ip.IpUtils;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.common.security.utils.SecurityUtils;
import com.smarterp.system.api.RemoteUserService;
import com.smarterp.system.api.domain.SysUser;
import com.smarterp.system.api.model.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 登录校验方法
 *
 * @author smarterp
 */
@Component
public class SysLoginService {

    private static final Logger logger = LoggerFactory.getLogger(SysLoginService.class);

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private SysPasswordService passwordService;

    @Resource
    private SysRecordLogService recordLogService;

    @Resource
    private RedisService redisService;

    /**
     * 登录
     */
    public LoginUser login(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password)) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户/密码必须填写");
            throw new ServiceException("用户/密码必须填写");
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户密码不在指定范围");
            throw new ServiceException("用户密码不在指定范围");
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户名不在指定范围");
            throw new ServiceException("用户名不在指定范围");
        }
        // IP黑名单校验
        String blackStr = Convert.toStr(redisService.getCacheObject(CacheConstants.SYS_LOGIN_BLACKIPLIST));
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr())) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "很遗憾，访问IP已被列入系统黑名单");
            throw new ServiceException("很遗憾，访问IP已被列入系统黑名单");
        }
        // 查询用户信息
        R<LoginUser> userResult = remoteUserService.getUserInfo(username, SecurityConstants.INNER);

        if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData())) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "登录用户不存在");
            throw new ServiceException("登录用户：" + username + " 不存在");
        }

        if (R.FAIL == userResult.getCode()) {
            throw new ServiceException(userResult.getMsg());
        }

        LoginUser userInfo = userResult.getData();
        SysUser user = userResult.getData().getSysUser();
        if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "对不起，您的账号已被删除");
            throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
        }
        if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户已停用，请联系管理员");
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }
        // 校验用户所属部门是否有绑定套餐，或者绑定的套餐是否在有效期之内
        logger.info("获取的用户信息 {}", JSON.toJSONString(user));
        if (user.getDept() == null) {
            throw new ServiceException("对不起，您的账号未设置归属部门，请联系管理员");
        }

        // 根据部门id查询套餐有效剩余天数
        String key = redisService.getJSONStringByKey("COMBO_DEPT_" + user.getDeptId());
        logger.info("从缓存中获取的有效天数信息 {}", key);
        if (key != null) {
            JSONObject jsonObject = JSON.parseObject(key);
            Integer comboEfficientDays = jsonObject.getInteger("comboEfficientDays");
            if (comboEfficientDays < 0) {
                throw new ServiceException("对不起，您的账号所属公司购买的套餐已失效，请联系管理员");
            }
        } else {
            throw new ServiceException("对不起，您的账号所属公司未购买VIP套餐，请联系管理员");
        }

        passwordService.validate(user, password);
        recordLogService.recordLogininfor(username, Constants.LOGIN_SUCCESS, "登录成功");
        return userInfo;
    }

    public void logout(String loginName) {
        recordLogService.recordLogininfor(loginName, Constants.LOGOUT, "退出成功");
    }

    /**
     * 注册
     */
    public void register(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password)) {
            throw new ServiceException("用户/密码必须填写");
        }
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw new ServiceException("账户长度必须在2到20个字符之间");
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("密码长度必须在5到20个字符之间");
        }

        // 注册用户信息
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);
        sysUser.setNickName(username);
        sysUser.setPassword(SecurityUtils.encryptPassword(password));
        R<?> registerResult = remoteUserService.registerUserInfo(sysUser, SecurityConstants.INNER);

        if (R.FAIL == registerResult.getCode()) {
            throw new ServiceException(registerResult.getMsg());
        }
        recordLogService.recordLogininfor(username, Constants.REGISTER, "注册成功");
    }
}
