package com.smarterp.order.schedule;

import com.alibaba.fastjson2.JSON;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoProduct;
import com.smarterp.order.mapper.MercadoProductMapper;
import com.smarterp.order.service.IMercadoSyncRemoteOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * @author juntao.li
 * @ClassName SyncRemoteOrder
 * @description 同步自发货链接
 * @date 2023/7/7 15:15
 * @Version 1.0
 */
@Component
public class SyncRemoteOrderLyh {

    private static final Logger logger = LoggerFactory.getLogger(SyncRemoteOrderLyh.class);



    @Resource
    private IMercadoSyncRemoteOrderService mercadoSyncRemoteOrderService;

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private MercadoProductMapper mercadoProductMapper;



    // @Scheduled(cron = "0/2 * * * * ?")
    public void pullProductLyh() {
        logger.error("定时任务启动=============pullProductLyh");
        //while (true) {
            Long productId = mercadoSyncRemoteOrderService.pullProductLyh();
            if (productId != null) {

                // 将需要发布的产品id放到消息队列中
                amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                        RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_PRODUCT_INFO, productId);
                //更改产品状态为发布中
                // 更新全局产品id和状态
                MercadoProduct exampleResult = new MercadoProduct();
                exampleResult.setId(productId);
                exampleResult.setReleaseStatus("ReleaseIng");
                exampleResult.setUpdateTime(new Date());
                Integer result = mercadoProductMapper.updateMercadoProduct(exampleResult);
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

        //}
    }
}
