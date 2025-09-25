package com.smarterp.order.bus;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.core.utils.mercado.MercadoNewHttpUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoBatchCollectionLink;
import com.smarterp.order.mapper.MercadoBatchCollectionLinkMapper;
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
import java.util.Date;
import java.util.concurrent.locks.Lock;


@Component
public class MercadoBatchCollectionLinkListener {

    private static final Logger logger = LoggerFactory.getLogger(MercadoBatchCollectionLinkListener.class);

    @Resource
    private MercadoBatchCollectionLinkMapper mercadoBatchCollectionLinkMapper;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    @Resource
    private RedisService redisService;

    /**
     * 批量采集链接
     *
     * @param record
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.CLOUD_PULL_MERCADO_BATCH_COLLECTION_LINK_QUEUE),
            key = RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_BATCH_COLLECTION_LINK))
    public void batchCollectionLink(MercadoBatchCollectionLink record) {
        // 取一个固定的accessToken
        String accessToken = redisService.getCacheObject("ACCESS_TOKEN_SHOP:1312397040");
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("CLOUD_PULL_MERCADO_BATCH_COLLECTION_LINK:" + record.getItemId() + ":" + record.getDeptId());
        boolean b = lock.tryLock();
        try {
            if (b) {
                // 首先根据itemId和deptId判断数据是否存在，如果存在则不做处理
                MercadoBatchCollectionLink example = new MercadoBatchCollectionLink();
                example.setItemId(record.getItemId());
                example.setDeptId(record.getDeptId());
                Integer count = mercadoBatchCollectionLinkMapper.linkCount(example);
                if (count > 0) {
                    return;
                }
                // 根据id查询图片地址
                JSONObject itemInfo = MercadoNewHttpUtil.getProductInfo(record.getItemId(),accessToken);
                if (itemInfo != null) {
                    String status = itemInfo.getString("status");
                    if ("active".equals(status)) {
                        // 产品链接
                        String permalink = itemInfo.getString("permalink");
                        MercadoBatchCollectionLink mercadoBatchCollectionLink = new MercadoBatchCollectionLink();
                        mercadoBatchCollectionLink.setId(IdUtil.getSnowflake(1, 1).nextId());
                        mercadoBatchCollectionLink.setItemId(record.getItemId());
                        mercadoBatchCollectionLink.setMerchantId(itemInfo.getString("seller_id"));
                        mercadoBatchCollectionLink.setProductUrl(permalink);
                        mercadoBatchCollectionLink.setUserId(record.getUserId());
                        mercadoBatchCollectionLink.setDeptId(record.getDeptId());
                        mercadoBatchCollectionLink.setCreateBy(String.valueOf(record.getUserId()));
                        mercadoBatchCollectionLink.setUpdateBy(String.valueOf(record.getUserId()));
                        // 首图链接
                        JSONArray pictures = itemInfo.getJSONArray("pictures");
                        if (pictures != null && pictures.size() > 0) {
                            JSONObject jsonObject = pictures.getJSONObject(0);
                            String pictureUrl = jsonObject.getString("url");
                            mercadoBatchCollectionLink.setPictureUrl(pictureUrl);
                        }
                        // 创建时间
                        Date date_created = itemInfo.getDate("date_created");
                        // 最后更新时间
                        Date last_updated = itemInfo.getDate("last_updated");
                        mercadoBatchCollectionLink.setCreateTime(date_created);
                        mercadoBatchCollectionLink.setUpdateTime(last_updated);
                        mercadoBatchCollectionLink.setSystemCreateTime(new Date());
                        mercadoBatchCollectionLink.setPrice(itemInfo.getString("price"));
                        mercadoBatchCollectionLink.setCurrencyId(itemInfo.getString("currency_id"));
                        mercadoBatchCollectionLink.setTitle(itemInfo.getString("title"));
                        mercadoBatchCollectionLink.setSiteId(itemInfo.getString("site_id"));
                        mercadoBatchCollectionLink.setCategoryId(itemInfo.getString("category_id"));
                        mercadoBatchCollectionLink.setDomainId(itemInfo.getString("domain_id"));
                        mercadoBatchCollectionLinkMapper.insertMercadoBatchCollectionLink(mercadoBatchCollectionLink);
                    }
                }
            } else {
                logger.error("批量采集链接没拿到锁 产品ID {} 部门ID {}", record.getItemId(), record.getDeptId());
            }
        } catch (Exception e) {
            logger.error("批量采集链接 {} 产品ID {} 部门ID {}", e, record.getItemId(), record.getDeptId());
        } finally {
            long end = System.currentTimeMillis();
            logger.error("批量采集链接耗费时间 {} 产品ID {} 部门ID {}", (end - startTime), record.getItemId(), record.getDeptId());
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("批量采集链接释放锁异常 {} 产品ID {} 部门ID {}", e, record.getItemId(), record.getDeptId());
            }
        }
    }

}
