package com.smarterp.order.bus;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoShopGlobal;
import com.smarterp.order.domain.MercadoShopItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;


@Component
public class MercadoGetShopItemInfoPageMarketPlaceItemListener {

    private static final Logger logger = LoggerFactory.getLogger(MercadoGetShopItemInfoPageMarketPlaceItemListener.class);

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private RedisService redisService;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    // 同步站点ID 例如MLM
    @Value("${mercado.item.siteId}")
    private String siteId;

    /**
     * 根据全局产品ID查询站点产品ID
     *
     * @param mercadoShopGlobal
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.GET_MERCADO_SHOP_ITEM_INFO_PAGE_MARKETPLACE_ITEM_QUEUE),
            key = RabbitMQConstant.RoutingKey.GET_MERCADO_SHOP_ITEM_INFO_PAGE_MARKETPLACE_ITEM))
    public void mercadoGetShopItemInfoPageListener(MercadoShopGlobal mercadoShopGlobal) {
        // 开始时间
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("GET_MERCADO_SHOP_ITEM_INFO_PAGE_MARKETPLACE_ITEM:" + mercadoShopGlobal.getGlobalId());
        boolean b = lock.tryLock();
        try {
            if (b) {
                // 从缓存中获取accessToken
                String accessToken = redisService.getCacheObject("ACCESS_TOKEN_SHOP:" + mercadoShopGlobal.getMerchantId());
                logger.info("从缓存中获取的accessToken {}", accessToken);
                if (accessToken != null) {
                    // 根据accessToken获取分页信息
                    JSONObject jsonObject = MercadoHttpUtil.getGlobalAndSiteRelationships(mercadoShopGlobal.getGlobalId(), accessToken);
                    if (jsonObject != null) {
                        JSONArray marketplace_items = jsonObject.getJSONArray("marketplace_items");
                        if (marketplace_items != null && marketplace_items.size() > 0) {
                            for (int index = 0; index < marketplace_items.size(); index++) {
                                JSONObject object = marketplace_items.getJSONObject(index);
                                if (siteId.equals(object.getString("site_id"))) {
                                    MercadoShopItem mercadoShopItem = new MercadoShopItem();
                                    mercadoShopItem.setId(object.getString("item_id"));
                                    mercadoShopItem.setSellerId(object.getString("user_id"));
                                    mercadoShopItem.setSiteId(object.getString("site_id"));
                                    // 更新图片
                                    mercadoShopItem.setUpdatePictures(true);
                                    amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                                            RabbitMQConstant.RoutingKey.GET_MERCADO_SHOP_ITEM_INFO_PAGE_MARKETPLACE_ITEM_NEXT, mercadoShopItem);
                                }
                            }
                        }
                    }
                }
            } else {
                logger.error("根据全局产品ID查询站点产品ID没有拿到锁 全局产品ID {}", mercadoShopGlobal.getGlobalId());
            }
        } catch (Exception e) {
            logger.error("根据全局产品ID查询站点产品ID异常 {} 全局产品ID {}", e, mercadoShopGlobal.getGlobalId());
        } finally {
            long end = System.currentTimeMillis();
            logger.error("根据全局产品ID查询站点产品ID耗费时间 {} 全局产品ID {}", (end - startTime), mercadoShopGlobal.getGlobalId());
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("获取店铺的所有全局产品ID释放锁异常 {} 全局产品ID {}", e, mercadoShopGlobal.getGlobalId());
            }
        }
    }
}
