package com.smarterp.system.bus;

import com.alibaba.fastjson2.JSON;
import com.smarterp.common.core.utils.mercado.MercadoSecureUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.system.constant.RabbitMQConstant;
import com.smarterp.system.domain.MercadoShopInfo;
import com.smarterp.system.domain.MercadoShopInfoSite;
import com.smarterp.system.mapper.MercadoShopInfoSiteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;


@Component
public class PushShopAccessTokenToCacheListener {

    private static final Logger logger = LoggerFactory.getLogger(PushShopAccessTokenToCacheListener.class);

    @Resource
    private MercadoShopInfoSiteMapper mercadoShopInfoSiteMapper;

    @Resource
    private RedisService redisService;

    /**
     * 将accessToken写入到缓存中
     *
     * @param mercadoShopInfo
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.PUSH_SHOP_ACCESS_TOKEN_TO_CACHE_QUEUE),
            key = RabbitMQConstant.RoutingKey.PUSH_SHOP_ACCESS_TOKEN_TO_CACHE))
    public void pushShopAccessTokenToCacheListener(MercadoShopInfo mercadoShopInfo) {
        try {
            if (mercadoShopInfo != null) {
                logger.info("队列中的店铺 {}" , JSON.toJSONString(mercadoShopInfo));
                String accessToken = MercadoSecureUtil.decryptMercado(String.valueOf(mercadoShopInfo.getId()), mercadoShopInfo.getAccessToken());
                // shop表主键id作为key
                redisService.setCacheObject("ACCESS_TOKEN_SHOP:" + mercadoShopInfo.getId(), accessToken);
                // shop表的merchantId作为key
                redisService.setCacheObject("ACCESS_TOKEN_SHOP:" + mercadoShopInfo.getMerchantId(), accessToken);
                // 根据merchantId获取各站点id
                List<MercadoShopInfoSite> mercadoShopInfoSites = mercadoShopInfoSiteMapper.selectMercadoShopInfoSiteByMerchantId(mercadoShopInfo.getMerchantId());
                if (!mercadoShopInfoSites.isEmpty()) {
                    for (MercadoShopInfoSite mercadoShopInfoSite : mercadoShopInfoSites) {
                        // 站点id作为key
                        redisService.setCacheObject("ACCESS_TOKEN_SHOP:" + mercadoShopInfoSite.getUserId(), accessToken);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("采集链接异常 {}" , e.getMessage());
        }
    }

}
