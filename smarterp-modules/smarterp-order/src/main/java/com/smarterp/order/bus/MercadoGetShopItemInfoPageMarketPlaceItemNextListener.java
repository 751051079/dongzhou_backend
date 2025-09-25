package com.smarterp.order.bus;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.core.utils.mercado.MercadoNewHttpUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoShopItem;
import com.smarterp.order.mapper.MercadoShopItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class MercadoGetShopItemInfoPageMarketPlaceItemNextListener {

    private static final Logger logger = LoggerFactory.getLogger(MercadoGetShopItemInfoPageMarketPlaceItemNextListener.class);

    @Resource
    private RedisService redisService;

    @Resource
    private MercadoShopItemMapper mercadoShopItemMapper;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    /**
     * 查询站点产品ID详情
     *
     * @param mercadoShopItem
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.GET_MERCADO_SHOP_ITEM_INFO_PAGE_MARKETPLACE_ITEM_NEXT_QUEUE),
            key = RabbitMQConstant.RoutingKey.GET_MERCADO_SHOP_ITEM_INFO_PAGE_MARKETPLACE_ITEM_NEXT))
    public void mercadoGetShopItemInfoPageListener(MercadoShopItem mercadoShopItem) {
        // 开始时间
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("GET_MERCADO_SHOP_ITEM_INFO_PAGE_MARKETPLACE_ITEM_NEXT:" + mercadoShopItem.getId());
        boolean b = lock.tryLock();
        try {
            if (b) {
                // 从缓存中获取accessToken
                String accessToken = redisService.getCacheObject("ACCESS_TOKEN_SHOP:" + mercadoShopItem.getSellerId());
                logger.info("从缓存中获取的accessToken {}", accessToken);
                if (accessToken != null) {
                    // 根据accessToken获取分页信息
                    JSONObject jsonObject = MercadoNewHttpUtil.getSiteProductInfo(mercadoShopItem.getId(), accessToken);
                    if (jsonObject != null) {
                        if (!"under_review".equals(jsonObject.getString("status"))) {
                            MercadoShopItem model = new MercadoShopItem();
                            model.setId(mercadoShopItem.getId());
                            model.setSellerId(mercadoShopItem.getSellerId());
                            model.setTitle(jsonObject.getString("title"));
                            model.setSiteId(mercadoShopItem.getSiteId());
                            model.setLogisticType(jsonObject.getString("user_logistic_type"));
                            model.setPermalink(jsonObject.getString("permalink"));
                            model.setSoldQuantity(jsonObject.getInteger("sold_quantity"));
                            model.setPrice(jsonObject.getString("price"));
                            model.setDateCreated(jsonObject.getDate("date_created"));
                            model.setLastUpdated(jsonObject.getDate("last_updated"));
                            model.setStatus(jsonObject.getString("status"));
                            try {
                                // 更新图片标识
                                if (mercadoShopItem.getUpdatePictures()) {
                                    String cbtItemId = jsonObject.getString("cbt_item_id");
                                    if (cbtItemId != null) {
                                        // 全局产品ID
                                        JSONObject productInfo = MercadoNewHttpUtil.getGlobalProductInfo(cbtItemId, accessToken);
                                        if (productInfo != null) {
                                            JSONArray pictures = productInfo.getJSONArray("pictures");
                                            if (pictures != null && pictures.size() > 0) {
                                                JSONObject object = pictures.getJSONObject(0);
                                                model.setThumbnail(object.getString("url"));
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                logger.info("获取产品首图异常 {}", e);
                            }
                            mercadoShopItemMapper.insertMercadoShopItem(model);
                        } else {
                            mercadoShopItemMapper.deleteMercadoShopItemById(mercadoShopItem.getId());
                        }
                    }
                }
            } else {
                logger.error("查询站点产品ID详情未获取到锁 站点产品ID {}", mercadoShopItem.getId());
            }
        } catch (Exception e) {
            logger.error("查询站点产品ID详情异常 {} 站点产品ID", e, mercadoShopItem.getId());
        } finally {
            long end = System.currentTimeMillis();
            logger.error("查询站点产品ID详情耗费时间 {} 站点产品ID {}", (end - startTime), mercadoShopItem.getId());
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("查询站点产品ID详情释放锁异常 {} 站点产品ID {}", e, mercadoShopItem.getId());
            }
        }
    }
}
