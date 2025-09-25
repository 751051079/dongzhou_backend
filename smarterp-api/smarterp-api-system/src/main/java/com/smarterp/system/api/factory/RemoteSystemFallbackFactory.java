package com.smarterp.system.api.factory;

import com.smarterp.common.core.domain.R;
import com.smarterp.system.api.RemoteSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 文件服务降级处理
 * 
 * @author smarterp
 */
@Component
public class RemoteSystemFallbackFactory implements FallbackFactory<RemoteSystemService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteSystemFallbackFactory.class);

    @Override
    public RemoteSystemService create(Throwable throwable)
    {
        log.error("更新美客多token:{}", throwable.getMessage());
        return new RemoteSystemService()
        {
            @Override
            public R<?> updateMercadoToken()
            {
                return R.fail("更新美客多token:" + throwable.getMessage());
            }

            @Override
            public R<?> updateComboValidDate()
            {
                return R.fail("更新部门绑定套餐的有效天数:" + throwable.getMessage());
            }
        };
    }
}
