package com.smarterp.order.bus;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.core.utils.mercado.MercadoSecureUtil;
import com.smarterp.common.core.utils.mercado.vo.CreateSiteVo;
import com.smarterp.common.core.utils.mercado.vo.SiteConfig;
import com.smarterp.order.config.DelayRabbitConfig;
import com.smarterp.order.domain.MercadoProduct;
import com.smarterp.order.domain.MercadoProductErrorLog;
import com.smarterp.order.domain.MercadoProductItem;
import com.smarterp.order.domain.dto.MercadoShopInfo;
import com.smarterp.order.mapper.MercadoProductErrorLogMapper;
import com.smarterp.order.mapper.MercadoProductItemMapper;
import com.smarterp.order.mapper.MercadoProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.Lock;

@Component
public class MercadoCreateSiteListener {

    private static final Logger logger = LoggerFactory.getLogger(MercadoCreateSiteListener.class);

    @Resource
    private MercadoProductMapper mercadoProductMapper;

    @Resource
    private MercadoProductItemMapper mercadoProductItemMapper;

    @Resource
    private MercadoProductErrorLogMapper mercadoProductErrorLogMapper;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    // 接口连接超时时间
    @Value("${mercado.api.connectionTimeout}")
    private int connectionTimeout;

    // 接口读取超时时间
    @Value("${mercado.api.readTimeout}")
    private int readTimeout;

    // 接口写入超时时间
    @Value("${mercado.api.writeTimeout}")
    private int writeTimeout;

    /**
     * 上传站点信息消费者逻辑
     *
     * @param mercadoProductItemId
     */
    @RabbitListener(queues = {DelayRabbitConfig.CLOUD_MERCADO_ORDER_QUEUE})
    public void pullMercadoProduct(Long mercadoProductItemId) {
        // 开始时间
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("RELEASE:ITEM:PRODUCT:" + mercadoProductItemId);
        boolean b = lock.tryLock();
        try {
            if (b) {
                MercadoProductItem mercadoProductItem = mercadoProductItemMapper.selectMercadoProductItemById(mercadoProductItemId);
                if (mercadoProductItem != null) {
                    if ("NoRelease".equals(mercadoProductItem.getSiteReleaseStatus())) {
                        // 首先将站点发布状态和描述发布状态更改为发布中
                        MercadoProductItem updateItemStatus = new MercadoProductItem();
                        updateItemStatus.setId(mercadoProductItem.getId());
                        updateItemStatus.setSiteReleaseStatus("ReleaseIng");
                        updateItemStatus.setSiteDescriptionStatus("ReleaseIng");
                        updateItemStatus.setUpdateTime(new Date());
                        mercadoProductItemMapper.updateMercadoProductItem(updateItemStatus);

                        // 默认先给站点和描述发布失败状态
                        MercadoProductItem mercadoProductItemNew = new MercadoProductItem();
                        mercadoProductItemNew.setId(mercadoProductItem.getId());
                        mercadoProductItemNew.setSiteReleaseStatus("ReleaseFail");
                        mercadoProductItemNew.setSiteDescriptionStatus("ReleaseFail");
                        // 发布站点逻辑
                        try {
                            // 根据产品id查询全局产品信息
                            MercadoProduct mercadoProduct = mercadoProductMapper.selectMercadoProductById(mercadoProductItem.getProductId());
                            if (mercadoProduct != null) {
                                // 获取accessToken
                                String merchantId = mercadoProduct.getMerchantId();
                                MercadoShopInfo mercadoShopInfo = mercadoProductMapper.getAccessTokenByMerchantId(Long.parseLong(merchantId));
                                if (mercadoShopInfo != null) {
                                    String accessToken = MercadoSecureUtil.decryptMercado(String.valueOf(mercadoShopInfo.getId()), mercadoShopInfo.getAccessToken());
                                    String cbtProId = mercadoProduct.getCbtProId();
                                    BigDecimal siteSalePrice = mercadoProductItem.getSiteSalePrice();
                                    String siteId = mercadoProductItem.getSiteId();
                                    String logisticType = mercadoProductItem.getLogisticType();
                                    String siteProductTitle = mercadoProductItem.getSiteProductTitle();
                                    HttpRequest post = HttpUtil.createPost("https://api.mercadolibre.com/marketplace/items/" + cbtProId);
                                    // 设置超时时间
                                    post.setConnectionTimeout(connectionTimeout);
                                    post.setReadTimeout(readTimeout);
                                    Map<String, String> headers = new HashMap<>();
                                    headers.put("Authorization", "Bearer " + accessToken);
                                    headers.put("Content-Type", "application/json");
                                    CreateSiteVo vo = new CreateSiteVo();
                                    List<SiteConfig> configs = new ArrayList<>();
                                    SiteConfig config = new SiteConfig();
                                    config.setSite_id(siteId);
                                    config.setLogistic_type(logisticType);
                                    config.setPrice(siteSalePrice);
                                    config.setTitle(siteProductTitle);
                                    configs.add(config);
                                    vo.setConfig(configs);
                                    logger.info("mercado上传站点产品请求参数 {}", JSON.toJSONString(vo));
                                    String res = post.addHeaders(headers).body(JSONObject.toJSONString(vo)).execute().body();
                                    logger.info("mercado上传站点产品响应信息 {}", JSON.toJSONString(res));
                                    if (res != null) {
                                        JSONArray jsonArray = JSONArray.parseArray(res);
                                        if (jsonArray.size() > 0) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            if (StringUtils.isEmpty(jsonObject.getString("item_id"))) {
                                                logger.error("mercado上传至站点失败 {}", res);
                                                // 插入错误日志
                                                MercadoProductErrorLog mercadoProductErrorLog = new MercadoProductErrorLog();
                                                mercadoProductErrorLog.setId(IdUtil.getSnowflake(1, 1).nextId());
                                                mercadoProductErrorLog.setErrorType("ITEM");
                                                mercadoProductErrorLog.setProductItemId(mercadoProductItemId);
                                                mercadoProductErrorLog.setErrorInfo(res);
                                                mercadoProductErrorLogMapper.insertMercadoProductErrorLog(mercadoProductErrorLog);
                                            } else {
                                                // 修改站点标题
                                                if (StringUtils.isNotBlank(siteProductTitle)) {
                                                    Map<String, String> headersTitle = new HashMap<>();
                                                    headersTitle.put("Authorization", "Bearer " + accessToken);
                                                    HttpRequest request = HttpUtil.createRequest(Method.PUT, "https://api.mercadolibre.com/global/items/" + jsonObject.getString("item_id"));
                                                    // 设置超时时间
                                                    request.setConnectionTimeout(connectionTimeout);
                                                    request.setReadTimeout(readTimeout);
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("title", siteProductTitle);
                                                    logger.info("mercado修改标题请求信息 {}", JSON.toJSONString(map));
                                                    String body = request.addHeaders(headersTitle).body(JSONObject.toJSONString(map)).execute().body();
                                                    logger.info("mercado修改标题响应信息 {}", body);
                                                }

                                                // 上传站点描述
                                                if (StringUtils.isNotBlank(mercadoProductItem.getSiteProductDescription())) {
                                                    JSONObject des = MercadoHttpUtil.createDes(accessToken, mercadoProduct.getCbtProId(), mercadoProductItem.getSiteProductDescription(), siteId, logisticType);
                                                    if (des != null) {
                                                        logger.info("(覆盖)mercado上传描述响应信息 {}", des);
                                                        if (StringUtils.isNotBlank(des.getString("error"))) {
                                                            mercadoProductItemNew.setSiteDescriptionStatus("ReleaseSuccess");
                                                        }
                                                    }
                                                } else {
                                                    mercadoProductItemNew.setSiteDescriptionStatus("ReleaseSuccess");
                                                }

                                                // 更新站点itemId和发送产品状态
                                                mercadoProductItemNew.setSiteItemId(jsonObject.getString("item_id"));
                                                mercadoProductItemNew.setSiteReleaseStatus("ReleaseSuccess");
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.error("mercado上架站点异常 {} 站点ID {}", e, mercadoProductItemId);
                        }

                        mercadoProductItemNew.setUpdateTime(new Date());
                        mercadoProductItemMapper.updateMercadoProductItem(mercadoProductItemNew);
                    }
                }
            }else {
                logger.info("没有拿到锁 站点产品id {}",mercadoProductItemId);
            }
        } catch (Exception e) {
            logger.error("上架站点产品异常 {}", e);
        } finally {
            long end = System.currentTimeMillis();
            logger.info("上架站点产品耗费时间 {} 站点产品ID {}", (end - startTime), mercadoProductItemId);
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("上架站点产品释放锁异常 {}", e);
            }
        }
    }
}
