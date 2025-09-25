package com.smarterp.system.schedule;

import com.smarterp.system.constant.RabbitMQConstant;
import com.smarterp.system.constant.SizeChartsConstant;
import com.smarterp.system.domain.MercadoShopInfo;
import com.smarterp.system.domain.MercadoSizeCharts;
import com.smarterp.system.domain.MercadoUpc;
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
public class SyncMercadoSizeCharts {

    private static final Logger logger = LoggerFactory.getLogger(SyncMercadoSizeCharts.class);

    @Resource
    private RedisLockRegistry redisLockRegistry;

    @Resource
    private MercadoShopInfoMapper mercadoShopInfoMapper;

    @Resource
    private AmqpTemplate amqpTemplate;

    /**
     * 定时任务更新尺码表信息
     */
    // @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    public void syncMercadoSizeCharts() {
        Lock lock = redisLockRegistry.obtain("syncMercadoSizeCharts");
        boolean b = lock.tryLock();
        try {
            if (b) {
                List<MercadoShopInfo> mercadoShopInfoList = mercadoShopInfoMapper.selectAllList();
                if (!mercadoShopInfoList.isEmpty()) {
                    List<String> genderIdList = SizeChartsConstant.GENDER_ID_LIST;
                    List<String> domainIdList = SizeChartsConstant.DOMAIN_ID_LIST;
                    for (MercadoShopInfo mercadoShopInfo : mercadoShopInfoList) {
                        for (String genderId : genderIdList) {
                            for (String domainId : domainIdList) {
                                MercadoSizeCharts mercadoSizeCharts = new MercadoSizeCharts();
                                mercadoSizeCharts.setDomainId(domainId);
                                mercadoSizeCharts.setGenderId(genderId);
                                mercadoSizeCharts.setSellerId(mercadoShopInfo.getMerchantId());
                                amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                                        RabbitMQConstant.RoutingKey.SYNC_MERCADO_SIZE_CHARTS_DATA, mercadoSizeCharts);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.info("同步尺码表异常，异常信息：{}", e);
        } finally {
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("释放锁异常");
            }
        }
    }

}
