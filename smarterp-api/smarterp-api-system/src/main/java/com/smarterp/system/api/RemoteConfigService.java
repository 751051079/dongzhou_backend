package com.smarterp.system.api;


import com.smarterp.common.core.constant.ServiceNameConstants;

import com.smarterp.common.core.web.domain.AjaxResult;

import com.smarterp.system.api.factory.RemoteConfigFallbackFactory;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务
 * 
 * @author smarterp
 */
@FeignClient(contextId = "remoteConfigService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteConfigFallbackFactory.class)
public interface RemoteConfigService
{
    /**
     * 根据参数键名查询参数值
     */
    @GetMapping("/config/configKey/{configKey}")
    public AjaxResult getConfigKey(@PathVariable("configKey") String configKey);
}
