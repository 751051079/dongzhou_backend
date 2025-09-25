package com.smarterp.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.core.utils.mercado.MercadoSecureUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.system.constant.RabbitMQConstant;
import com.smarterp.system.domain.MercadoShopInfo;
import com.smarterp.system.domain.vo.SysDeptVO;
import com.smarterp.system.mapper.MercadoComboInfoMapper;
import com.smarterp.system.mapper.MercadoShopInfoMapper;
import com.smarterp.system.service.IMercadoProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 产品Service业务层处理
 *
 * @author smarterp
 * @date 2023-04-26
 */
@Service
public class MercadoProductServiceImpl implements IMercadoProductService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoProductServiceImpl.class);

    @Resource
    private RedisService redisService;

    @Resource
    private MercadoComboInfoMapper mercadoComboInfoMapper;

    @Resource
    private MercadoShopInfoMapper mercadoShopInfoMapper;

    @Resource
    private AmqpTemplate amqpTemplate;

    /**
     * 更新所有店铺的token
     */
    @Override
    public void updateMercadoToken() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
        int page = 1;
        int pageSize = 100;
        while (true) {
            List<MercadoShopInfo> mercadoShopInfos = mercadoShopInfoMapper.getShopList((page - 1) * pageSize, pageSize);
            if (mercadoShopInfos.size() == 0) {
                break;
            }
            for (MercadoShopInfo mercadoShopInfo : mercadoShopInfos) {
                scheduledExecutorService.execute(() -> {
                    try {
                        String s = MercadoSecureUtil.decryptMercado(mercadoShopInfo.getId().toString(), mercadoShopInfo.getRefreshToken());
                        JSONObject refreshToken = MercadoHttpUtil.getRefreshToken(s);
                        if (refreshToken != null) {
                            mercadoShopInfo.setAccessToken(
                                    MercadoSecureUtil.encryptMercado(mercadoShopInfo.getId().toString(), refreshToken.getString("access_token")));
                            mercadoShopInfo.setRefreshToken(
                                    MercadoSecureUtil.encryptMercado(mercadoShopInfo.getId().toString(), refreshToken.getString("refresh_token")));
                            mercadoShopInfo.setUpdateTime(new Date());
                            mercadoShopInfoMapper.updateMercadoToken(mercadoShopInfo);

                            try {
                                // 使用消息队列处理店铺站点数据
                                amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                                        RabbitMQConstant.RoutingKey.PUSH_SHOP_ACCESS_TOKEN_TO_CACHE, mercadoShopInfo);
                            } catch (Exception e) {
                                logger.error("");
                            }

                        } else {
                            logger.error("更新美客多token失败:{}" , mercadoShopInfo.getId());
                        }
                    } catch (Exception e) {
                        logger.error("更新美客多token异常:{},{}" , e, mercadoShopInfo.getId());
                    }
                });
            }
            page++;
        }
        scheduledExecutorService.shutdown();
    }


    /**
     * 更新部门绑定套餐剩余的有效天数
     */
    @Override
    public void updateComboValidDate() {
        List<SysDeptVO> sysDeptVOList = mercadoComboInfoMapper.getAllDeptComboList();
        if (!sysDeptVOList.isEmpty()) {
            for (SysDeptVO sysDeptVO : sysDeptVOList) {
                if (sysDeptVO.getComboEfficientDays() == null) {
                    sysDeptVO.setComboEfficientDays(-1);
                }
                logger.info("更新部门绑定套餐剩余的有效天数 key {} value {}" , "COMBO_DEPT_" + sysDeptVO.getDeptId(), JSON.toJSONString(sysDeptVO));
                redisService.setCacheObject("COMBO_DEPT_" + sysDeptVO.getDeptId(), JSON.toJSONString(sysDeptVO));
            }
        }
    }

    @Override
    public String getAccessToken(String merchantId) {
        MercadoShopInfo mercadoShopInfo = mercadoShopInfoMapper.selectMercadoShopByMerchantId(merchantId);
        return MercadoSecureUtil.decryptMercado(mercadoShopInfo.getId().toString(), mercadoShopInfo.getAccessToken());
    }


}
