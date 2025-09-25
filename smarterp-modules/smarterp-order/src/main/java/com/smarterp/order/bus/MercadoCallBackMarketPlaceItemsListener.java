package com.smarterp.order.bus;

import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.core.utils.mercado.MercadoNewHttpUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoShopItem;
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
public class MercadoCallBackMarketPlaceItemsListener {

    private static final Logger logger = LoggerFactory.getLogger(MercadoCallBackMarketPlaceItemsListener.class);

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    @Resource
    private RedisService redisService;

    /**
     * 美客多回调之站点产品
     *
     * @param jsonObject
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.MERCADO_CALL_BACK_INFO_MARKETPLACE_ITEMS_QUEUE),
            key = RabbitMQConstant.RoutingKey.MERCADO_CALL_BACK_INFO_MARKETPLACE_ITEMS))
    public void mercadoCallBackMarketPlaceItemsListener(JSONObject jsonObject) {
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("MERCADO_CALL_BACK_INFO_MARKETPLACE_ITEMS:" + jsonObject.getString("resource"));
        boolean b = lock.tryLock();
        try {
            if (b) {
                String resource = jsonObject.getString("resource");
                String user_id = jsonObject.getString("user_id");
                String accessToken = redisService.getCacheObject("ACCESS_TOKEN_SHOP:" + user_id);
                if (accessToken != null) {
                    if (resource != null && user_id != null) {
                        String[] split = resource.split("/");
                        String itemId = split[split.length - 1];
                        JSONObject itemInfo = MercadoNewHttpUtil.getSiteProductInfo(itemId, accessToken);
                        if (itemInfo != null) {
                            String id = itemInfo.getString("id");
                            String site_id = itemInfo.getString("site_id");
                            String seller_id = itemInfo.getString("seller_id");
                            MercadoShopItem mercadoShopItem = new MercadoShopItem();
                            mercadoShopItem.setId(id);
                            mercadoShopItem.setSellerId(seller_id);
                            mercadoShopItem.setSiteId(site_id);
                            mercadoShopItem.setUpdatePictures(true);
                            amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                                    RabbitMQConstant.RoutingKey.GET_MERCADO_SHOP_ITEM_INFO_PAGE_MARKETPLACE_ITEM_NEXT, mercadoShopItem);
                        }
                    }
                }
            } else {
                logger.error("美客多回调之站点产品没有拿到锁 resource {}", jsonObject.toJSONString());
            }
        } catch (Exception e) {
            logger.error("美客多回调之站点产品异常 {} resource {}", e, jsonObject.toJSONString());
        } finally {
            long end = System.currentTimeMillis();
            logger.error("美客多回调之站点产品费时间 {} resource {}", (end - startTime), jsonObject.toJSONString());
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("美客多回调之站点产释放锁异常 {} resource {}", e, jsonObject.toJSONString());
            }
        }
    }

}
