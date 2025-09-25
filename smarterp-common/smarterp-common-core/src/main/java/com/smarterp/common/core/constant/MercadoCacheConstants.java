package com.smarterp.common.core.constant;

/**
 * 美客多缓存常量信息
 *
 * @author smarterp
 */
public class MercadoCacheConstants {

    /**
     * 存储店铺accessToken信息
     * key包括shop_info表主键ID，merchantId，userId
     */
    public final static String ACCESS_TOKEN_SHOP = "ACCESS_TOKEN_SHOP:";

    /**
     * 存储店铺名称信息
     * key包括merchantId，userId
     */
    public final static String MERCADO_SHOP_INFO_NAME = "MERCADO_SHOP_INFO:NAME:";

    /**
     * 存储站点信息
     * key：站点ID
     * value {"site_id":"MLM","logistic_type":"remote"}
     */
    public final static String MERCADO_SHOP_INFO_SITE = "MERCADO_SHOP_INFO:SITE";


}
