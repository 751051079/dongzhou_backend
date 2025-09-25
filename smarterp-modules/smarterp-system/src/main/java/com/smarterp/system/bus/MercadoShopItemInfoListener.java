package com.smarterp.system.bus;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.core.utils.mercado.MercadoSecureUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.system.constant.RabbitMQConstant;
import com.smarterp.system.domain.MercadoShopInfo;
import com.smarterp.system.domain.MercadoShopInfoSite;
import com.smarterp.system.mapper.MercadoShopInfoSiteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;


@Component
public class MercadoShopItemInfoListener {

    private static final Logger logger = LoggerFactory.getLogger(MercadoShopItemInfoListener.class);

    @Resource
    private MercadoShopInfoSiteMapper mercadoShopInfoSiteMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private AmqpTemplate amqpTemplate;

    /**
     * 处理店铺站点信息
     *
     * @param mercadoShopInfo
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.GET_MERCADO_SHOP_ITEM_INFO_QUEUE),
            key = RabbitMQConstant.RoutingKey.GET_MERCADO_SHOP_ITEM_INFO))
    public void mercadoShopItemInfoListener(MercadoShopInfo mercadoShopInfo) {
        try {
            String accessToken = MercadoSecureUtil.decryptMercado(mercadoShopInfo.getId().toString(), mercadoShopInfo.getAccessToken());
            JSONArray jsonArray = MercadoHttpUtil.getShopSiteInfo(accessToken);
            // 将店铺名称存入到redis缓存中
            redisService.setCacheObject("MERCADO_SHOP_INFO:NAME:" + mercadoShopInfo.getMerchantId(), mercadoShopInfo.getMercadoShopName());

            // 将accessToken存入到redis缓存中
            redisService.setCacheObject("ACCESS_TOKEN_SHOP:" + mercadoShopInfo.getId(), accessToken);
            redisService.setCacheObject("ACCESS_TOKEN_SHOP:" + mercadoShopInfo.getMerchantId(), accessToken);

            if (jsonArray != null && jsonArray.size() > 0) {
                for (int index = 0; index < jsonArray.size(); index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    String user_id = jsonObject.getString("user_id");
                    MercadoShopInfoSite mercadoShopInfoSite = new MercadoShopInfoSite();
                    mercadoShopInfoSite.setId(Long.parseLong(user_id));
                    mercadoShopInfoSite.setDeptId(mercadoShopInfo.getDeptId());
                    mercadoShopInfoSite.setMerchantId(mercadoShopInfo.getMerchantId());
                    mercadoShopInfoSite.setUserId(user_id);
                    mercadoShopInfoSite.setSiteId(jsonObject.getString("site_id"));
                    mercadoShopInfoSite.setLogisticType(jsonObject.getString("logistic_type"));
                    mercadoShopInfoSite.setCreateBy(mercadoShopInfo.getCreateBy());
                    mercadoShopInfoSite.setCreateTime(new Date());
                    mercadoShopInfoSite.setUpdateBy(mercadoShopInfo.getUpdateBy());
                    mercadoShopInfoSite.setUpdateTime(new Date());
                    mercadoShopInfoSite.setQuota(jsonObject.getString("quota"));
                    mercadoShopInfoSite.setTotalItems(jsonObject.getString("total_items"));
                    mercadoShopInfoSiteMapper.insertMercadoShopInfoSite(mercadoShopInfoSite);

                    // 将店铺站点信息存入到缓存中
                    JSONObject object = new JSONObject();
                    object.put("site_id", jsonObject.getString("site_id"));
                    object.put("logistic_type", jsonObject.getString("logistic_type"));
                    redisService.setCacheObject("MERCADO_SHOP_INFO:SITE:" + user_id, JSON.toJSONString(object));
                    // 将店铺名称存入到redis缓存中
                    redisService.setCacheObject("MERCADO_SHOP_INFO:NAME:" + user_id, mercadoShopInfo.getMercadoShopName());
                    // 将accessToken存入到redis缓存中
                    redisService.setCacheObject("ACCESS_TOKEN_SHOP:" + user_id, accessToken);
                }
            }
        } catch (Exception e) {
            logger.error("采集链接异常 {}", e.getMessage());
        }
    }

}
