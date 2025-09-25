package com.smarterp.system.api;

import com.smarterp.common.core.constant.ServiceNameConstants;
import com.smarterp.common.core.domain.R;
import com.smarterp.system.api.factory.RemoteSystemFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(contextId = "remoteSystemService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSystemFallbackFactory.class)
public interface RemoteSystemService {

    /**
     * 更新所有店铺的token自动更新
     *
     */
    @PostMapping(value = "/feignApi/updateMercadoToken")
    public R<?> updateMercadoToken();

    /**
     * 更新所有部门绑定套餐的有效天数
     *
     */
    @PostMapping(value = "/feignApi/updateComboValidDate")
    public R<?> updateComboValidDate();
}
