package com.smarterp.system.api.factory;

import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.system.api.RemoteConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务降级处理
 * 
 * @author smarterp
 */
@Component
public class RemoteConfigFallbackFactory implements FallbackFactory<RemoteConfigService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteConfigFallbackFactory.class);

    @Override
    public RemoteConfigService create(Throwable throwable)
    {
        log.error("参数配置服务调用失败:{}", throwable.getMessage());
        return new RemoteConfigService()
        {
            @Override
            public AjaxResult getConfigKey(@PathVariable String configKey)
            {
                return AjaxResult.error("获取参数配置失败:" + throwable.getMessage());
            }
        };
    }
}
