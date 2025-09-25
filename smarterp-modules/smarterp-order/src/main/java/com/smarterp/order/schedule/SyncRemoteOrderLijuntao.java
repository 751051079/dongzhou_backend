package com.smarterp.order.schedule;

import com.alibaba.fastjson.JSON;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.mapper.MercadoProductMapper;
import com.smarterp.order.service.IMercadoProductService;
import com.smarterp.order.service.IMercadoSyncRemoteOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
public class SyncRemoteOrderLijuntao {

    private static final Logger logger = LoggerFactory.getLogger(SyncRemoteOrderLijuntao.class);

    @Resource
    private RedisLockRegistry redisLockRegistry;

    @Resource
    private IMercadoSyncRemoteOrderService mercadoSyncRemoteOrderService;

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private MercadoProductMapper mercadoProductMapper;

    @Resource
    private IMercadoProductService mercadoProductService;

    // @Scheduled(fixedDelay = 300 * 1000)
    public void syncRemoteOrderLijuntao() {
        logger.error("定时任务启动=============syncRemoteOrderLijuntao");
        Lock lock = redisLockRegistry.obtain("syncRemoteOrderLijuntao");
        boolean b = lock.tryLock();
        try {
            if (b) {
                List<Long> longs = mercadoSyncRemoteOrderService.syncRemoteOrderLijuntao();
                logger.info("同步订单返回的产品id ===========" + JSON.toJSONString(longs));
            }
        } catch (Exception e) {
            logger.info("同步订单异常，异常信息：{}", e);
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

    //@Scheduled(fixedDelay = 30000 * 1000)
    public void pullProductLijuntao() {
        logger.error("定时任务启动=============pullProductLijuntao");
        Lock lock = redisLockRegistry.obtain("pullProductLijuntao");
        boolean b = lock.tryLock();
        try {
            if (b) {
                List<Long> longs = mercadoSyncRemoteOrderService.pullProductLijuntao();
                if (!longs.isEmpty()) {
                    for (Long productId : longs) {
                        // 将需要发布的产品id放到消息队列中
                        amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                                RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_PRODUCT_INFO, productId);
                    }
                }
            }
        } catch (Exception e) {
            logger.info("同步订单异常，异常信息：{}", e);
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

   // @Scheduled(fixedDelay = 60 * 1000)
    public void deleteProductLijuntao() {
        logger.error("定时任务启动=============deleteProductLijuntao");
        Lock lock = redisLockRegistry.obtain("deleteProductLijuntao");
        boolean b = lock.tryLock();
        try {
            if (b) {
                List<Long> longs = mercadoSyncRemoteOrderService.deleteProductLijuntao();
                if (!longs.isEmpty()) {
                    for (Long productId : longs) {
                        mercadoProductService.deleteMercadoProductById(productId);
                    }
                }
            }
        } catch (Exception e) {
            logger.info("同步订单异常，异常信息：{}", e);
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

    //@Scheduled(cron = "0 4 0 * * ?")
   /* public void pullProductIdLyh() {
        logger.error("定时任务启动=============pullProductIdLyh");
        //查询100次  一共10000条数据
        for (int i = 0; i < 1000; i++) {
            int sise = mercadoSyncRemoteOrderService.pullProductIdLyh(i, 100);
            if (sise == 0) {
                return;
            }
        }

    }*/

    //@Scheduled(cron = "0 48 15 * * ?")
/*    public void deleteProByLyh() {
        logger.error("定时任务启动=============deleteProByLyh");
        //查询100次  一共10000条数据
        for (int i = 0; i < 1000; i++) {

            int sise = mercadoSyncRemoteOrderService.deleteProByLyh(i, 100);
            if (sise == 0) {
                return;
            }
        }

    }*/


    // @Scheduled(cron = "0 44 10 27 * ?")
 /*   public void syncRemoteOrderLyh() {
        logger.error("定时任务启动=============syncRemoteOrderLyh");
//        Lock lock = redisLockRegistry.obtain("syncRemoteOrderLyh");
//        boolean b = lock.tryLock();
        for (int i = 0; i < 10000; i++) {
            int size = mercadoSyncRemoteOrderService.syncRemoteOrderLyh(i, 10);
            if (size == 0) {
                return;
            }
        }

//        try {
//            if (b) {
//                List<Long> longs = mercadoSyncRemoteOrderService.syncRemoteOrderLyh();
//                logger.info("同步订单返回的产品id ===========" + JSON.toJSONString(longs));
//            }
//        } catch (Exception e) {
//            logger.info("同步订单异常，异常信息：{}", e);
//        } finally {
//            if (b) {
//                lock.unlock();
//            }
//        }
    }*/

    //@Scheduled(cron = "0 24 0 27 * ?")
   /* public void pullProductLyh() {
        logger.error("定时任务启动=============pullProductLyh");
//        Lock lock = redisLockRegistry.obtain("pullProductLyh");
//        boolean b = lock.tryLock();
//        try {
//            if (b) {

        while (true) {
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
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                return;
            }

        }

//        } catch (Exception e) {
//            logger.info("同步订单异常，异常信息：{}", e);
//        } finally {
//            if (b) {
//                lock.unlock();
//            }
//        }
    }*/


}
