package com.smarterp.common.core.exception.user;

import com.smarterp.common.core.exception.base.BaseException;

/**
 * 用户信息异常类
 * 
 * @author smarterp
 */
public class UserException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args)
    {
        super("user", code, args, null);
    }
}
