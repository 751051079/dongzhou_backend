package com.smarterp.order.bus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoShopGlobal;
import com.smarterp.order.domain.dto.UpdateShopItemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;


@Component
public class MercadoGetShopItemInfoPageListener {

    private static final Logger logger = LoggerFactory.getLogger(MercadoGetShopItemInfoPageListener.class);

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private RedisService redisService;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    /**
     * 获取店铺的所有全局产品ID
     *
     * @param updateShopItemInfo
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.GET_MERCADO_SHOP_ITEM_INFO_PAGE_QUEUE),
            key = RabbitMQConstant.RoutingKey.GET_MERCADO_SHOP_ITEM_INFO_PAGE))
    public void mercadoGetShopItemInfoPageListener(UpdateShopItemInfo updateShopItemInfo) {
        // 开始时间
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("GET_MERCADO_SHOP_ITEM_INFO_PAGE:" + updateShopItemInfo.getMerchantId());
        boolean b = lock.tryLock();
        String scrollId = null;
        JSONArray results = null;
        try {
            if (b) {
                // 从缓存中获取accessToken
                String accessToken = redisService.getCacheObject("ACCESS_TOKEN_SHOP:" + updateShopItemInfo.getMerchantId());
                logger.info("从缓存中获取的accessToken {}", accessToken);
                if (accessToken != null) {
                    // 根据accessToken获取分页信息
                    JSONObject jsonObject = MercadoHttpUtil.getShopGlobalProductId(updateShopItemInfo.getMerchantId(), accessToken, updateShopItemInfo.getScrollId());
                    if (jsonObject != null) {
                        scrollId = jsonObject.getString("scroll_id");
                        results = jsonObject.getJSONArray("results");
                        if (scrollId != null && results != null & results.size() > 0) {
                            // TODO 处理获取的全局产品ID集合 results
                            logger.info("获取的全局产品信息 {}", JSON.toJSONString(results));
                            try {
                                for (int index = 0; index < results.size(); index++) {
                                    String globalId = results.getString(index);
                                    String merchantId = updateShopItemInfo.getMerchantId();
                                    MercadoShopGlobal mercadoShopGlobal = new MercadoShopGlobal();
                                    mercadoShopGlobal.setId(merchantId + "_" + globalId);
                                    mercadoShopGlobal.setGlobalId(globalId);
                                    mercadoShopGlobal.setMerchantId(merchantId);
                                    amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                                            RabbitMQConstant.RoutingKey.GET_MERCADO_SHOP_ITEM_INFO_PAGE_MARKETPLACE_ITEM, mercadoShopGlobal);
                                }
                            } catch (Exception e) {
                                logger.info("处理获取的全局产品ID集合异常 {}", e);
                            }
                        }
                    }
                }
            } else {
                logger.error("获取店铺的所有全局产品ID没有拿到锁 merchantId {}", updateShopItemInfo.getMerchantId());
            }
        } catch (Exception e) {
            logger.error("获取店铺的所有全局产品ID异常 {} merchantId {}", e, updateShopItemInfo.getMerchantId());
        } finally {
            long end = System.currentTimeMillis();
            logger.error("获取店铺的所有全局产品ID耗费时间 {} merchantId {}", (end - startTime), updateShopItemInfo.getMerchantId());
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("获取店铺的所有全局产品ID释放锁异常 {} merchantId {}", e, updateShopItemInfo.getMerchantId());
            }
            if (scrollId != null && results != null & results.size() > 0) {
                // 再次将数据防到队列中
                updateShopItemInfo.setScrollId(scrollId);
                amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                        RabbitMQConstant.RoutingKey.GET_MERCADO_SHOP_ITEM_INFO_PAGE, updateShopItemInfo);
            }
        }
    }
}
