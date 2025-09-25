package com.smarterp.order.schedule;

import com.alibaba.fastjson2.JSON;
import com.smarterp.order.service.IMercadoSyncRemoteOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SyncRemoteOrderBack {

    private static final Logger logger = LoggerFactory.getLogger(SyncRemoteOrderBack.class);

    @Resource
    private RedisLockRegistry redisLockRegistry;

    @Resource
    private IMercadoSyncRemoteOrderService mercadoSyncRemoteOrderService;

    // @Scheduled(fixedDelay = 30 * 1000)
    public void syncRemoteOrderBack() {
        logger.error("定时任务启动=============syncRemoteOrderBack");
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("syncRemoteOrderBack");
        boolean b = lock.tryLock();
        try {
            if (b) {
                // 只保留最近30天的数据
                // 每次返回的数量
                Integer limit = 50;
                List<Long> longs = mercadoSyncRemoteOrderService.syncRemoteOrderBack(limit);
                logger.error("查询相应的productIds {}", JSON.toJSONString(longs));
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
            long endTime = System.currentTimeMillis();
            logger.error("执行时间：" + (endTime - startTime));
        }
    }

}
