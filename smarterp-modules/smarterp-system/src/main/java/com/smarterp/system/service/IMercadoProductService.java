package com.smarterp.system.service;

/**
 * 产品Service接口
 *
 * @author smarterp
 * @date 2023-04-26
 */
public interface IMercadoProductService {
    /**
     * 定时更新token
     */
    public void updateMercadoToken();

    /**
     * 定时更新套餐剩余有效时间
     */
    public void updateComboValidDate();

    String getAccessToken(String merchantId);
}
