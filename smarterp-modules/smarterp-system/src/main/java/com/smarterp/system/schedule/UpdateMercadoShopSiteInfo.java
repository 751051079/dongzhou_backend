package com.smarterp.system.schedule;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.system.constant.RabbitMQConstant;
import com.smarterp.system.domain.MercadoShopInfo;
import com.smarterp.system.mapper.MercadoShopInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.locks.Lock;


@Component
public class UpdateMercadoShopSiteInfo {

    private static final Logger logger = LoggerFactory.getLogger(UpdateMercadoShopSiteInfo.class);

    @Resource
    private RedisLockRegistry redisLockRegistry;

    @Resource
    private MercadoShopInfoMapper mercadoShopInfoMapper;

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private RedisService redisService;

    /**
     * 更新店铺链接数量，每天更新一次
     */
    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    public void updateMercadoShopSiteInfo() {
        logger.info("启动定时任务更新店铺站点信息");
        Lock lock = redisLockRegistry.obtain("updateMercadoShopSiteInfo");
        boolean b = lock.tryLock();
        try {
            if (b) {
                List<MercadoShopInfo> mercadoShopInfoList = mercadoShopInfoMapper.selectAllList();
                if (!mercadoShopInfoList.isEmpty()) {
                    for (MercadoShopInfo mercadoShopInfo : mercadoShopInfoList) {
                        // 使用消息队列处理店铺站点数据
                        amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                                RabbitMQConstant.RoutingKey.GET_MERCADO_SHOP_ITEM_INFO, mercadoShopInfo);
                    }
                }
            }
        } catch (Exception e) {
            logger.info("定时任务更新店铺站点信息异常 {}", e);
        } finally {
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("释放锁异常");
            }
        }
        logger.info("结束定时任务更新店铺站点信息");
    }

    /**
     * 更新墨西哥比索兑换美元汇率，每天更新一次
     */
    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    public void updateUsdExchangeRate() {
        logger.info("启动定时任务更新墨西哥比索兑换美元汇率");
        Lock lock = redisLockRegistry.obtain("updateUsdExchangeRate");
        boolean b = lock.tryLock();
        try {
            if (b) {
                JSONObject exchangeRates = MercadoHttpUtil.getRealTimeExchangeRates();
                if (exchangeRates != null) {
                    String code = exchangeRates.getString("code");
                    if (code.equals("200")) {
                        JSONArray jsonArray = exchangeRates.getJSONArray("data");
                        if (jsonArray != null && jsonArray.size() > 0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String close = jsonObject.getString("close");
                            if (close != null) {
                                logger.info("查询当天墨西哥比索兑换美元汇率为 {}", close);
                                redisService.setCacheObject("updateUsdExchangeRate", close);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.info("定时任务更新墨西哥比索兑换美元汇率信息异常 {}", e);
        } finally {
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("释放锁异常");
            }
        }
        logger.info("结束定时任务更新墨西哥比索兑换美元汇率");
    }


}
