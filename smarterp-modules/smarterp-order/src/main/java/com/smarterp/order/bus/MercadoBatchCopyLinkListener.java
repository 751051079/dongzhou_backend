package com.smarterp.order.bus;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoProduct;
import com.smarterp.order.domain.MercadoProductAttributes;
import com.smarterp.order.domain.MercadoProductCombination;
import com.smarterp.order.domain.MercadoProductItem;
import com.smarterp.order.domain.dto.BatchCopyLinkToShop;
import com.smarterp.order.mapper.MercadoProductAttributesMapper;
import com.smarterp.order.mapper.MercadoProductCombinationMapper;
import com.smarterp.order.mapper.MercadoProductItemMapper;
import com.smarterp.order.mapper.MercadoProductMapper;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;


@Component
public class MercadoBatchCopyLinkListener {

    private static final Logger logger = LoggerFactory.getLogger(MercadoBatchCopyLinkListener.class);

    @Resource
    private MercadoProductMapper mercadoProductMapper;

    @Resource
    private MercadoProductItemMapper mercadoProductItemMapper;

    @Resource
    private MercadoProductCombinationMapper mercadoProductCombinationMapper;

    @Resource
    private MercadoProductAttributesMapper mercadoProductAttributesMapper;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    /**
     * 复制链接到别的店铺
     *
     * @param batchCopyLinkToShop
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.CLOUD_PULL_MERCADO_BATCH_COPY_LINK_TO_SHOP_QUEUE),
            key = RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_BATCH_COPY_LINK_TO_SHOP))
    public void batchCopyLinkToShop(BatchCopyLinkToShop batchCopyLinkToShop) {
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("CLOUD_PULL_MERCADO_BATCH_COPY_LINK_TO_SHOP:" + batchCopyLinkToShop.getShopId() + ":" + batchCopyLinkToShop.getProductId());
        boolean b = lock.tryLock();
        try {
            if (b) {
                logger.info("复制链接收到的消息 {}", JSON.toJSONString(batchCopyLinkToShop));
                String productId = batchCopyLinkToShop.getProductId();
                logger.info("全局产品id {}", productId);
                MercadoProduct model = mercadoProductMapper.selectMercadoProductById(Long.parseLong(productId));
                if (model != null) {
                    MercadoProduct insertMercadoProduct = new MercadoProduct();
                    insertMercadoProduct.setId(IdUtil.getSnowflake(1, 1).nextId());
                    insertMercadoProduct.setUserId(model.getUserId());
                    insertMercadoProduct.setDeptId(model.getDeptId());
                    insertMercadoProduct.setMerchantId(batchCopyLinkToShop.getShopId());
                    insertMercadoProduct.setMercadoProductUrl(model.getMercadoProductUrl());
                    insertMercadoProduct.setProductTitle(model.getProductTitle());
                    insertMercadoProduct.setProductDescription("全局产品简述");
                    insertMercadoProduct.setSalePrice(model.getSalePrice());
                    insertMercadoProduct.setCbtItemId(model.getCbtItemId());
                    insertMercadoProduct.setSkuPre(model.getSkuPre());
                    insertMercadoProduct.setCbtCategory(model.getCbtCategory());
                    insertMercadoProduct.setUpcCode(batchCopyLinkToShop.getUpcCode());
                    insertMercadoProduct.setShipType(model.getShipType());
                    insertMercadoProduct.setReleaseStatus("NoRelease");
                    insertMercadoProduct.setPictures(model.getPictures());
                    insertMercadoProduct.setAvailableQuantity(999);
                    insertMercadoProduct.setCreateTime(new Date());
                    insertMercadoProduct.setCreateBy(model.getCreateBy());
                    insertMercadoProduct.setUpdateTime(new Date());
                    insertMercadoProduct.setUpdateBy(model.getUpdateBy());
                    insertMercadoProduct.setDeleted(false);
                    insertMercadoProduct.setDomainId(model.getDomainId());
                    insertMercadoProduct.setGenderId(model.getGenderId());
                    insertMercadoProduct.setShipInfo(model.getShipInfo());
                    Integer result = mercadoProductMapper.insertMercadoProduct(insertMercadoProduct);
                    if (result > 0) {
                        // 查询站点信息
                        MercadoProductItem siteExample = new MercadoProductItem();
                        siteExample.setProductId(Long.parseLong(productId));
                        List<MercadoProductItem> mercadoProductItems = mercadoProductItemMapper.selectMercadoProductItemList(siteExample);
                        if (!mercadoProductItems.isEmpty()) {
                            List<MercadoProductItem> insertItemList = new ArrayList<>();
                            for (MercadoProductItem item : mercadoProductItems) {
                                MercadoProductItem mercadoProductItem = new MercadoProductItem();
                                mercadoProductItem.setId(IdUtil.getSnowflake(1, 1).nextId());
                                mercadoProductItem.setProductId(insertMercadoProduct.getId());
                                mercadoProductItem.setSiteId(item.getSiteId());
                                mercadoProductItem.setLogisticType(item.getLogisticType());
                                mercadoProductItem.setSiteSalePrice(item.getSiteSalePrice());
                                mercadoProductItem.setSiteProductDescription(item.getSiteProductDescription());
                                mercadoProductItem.setSiteProductTitle(item.getSiteProductTitle());
                                mercadoProductItem.setSiteCategory(item.getSiteCategory());
                                mercadoProductItem.setSiteReleaseStatus("NoRelease");
                                mercadoProductItem.setSiteDescriptionStatus("NoRelease");
                                mercadoProductItem.setMerchantId(insertMercadoProduct.getMerchantId());
                                mercadoProductItem.setCreateTime(new Date());
                                mercadoProductItem.setCreateBy(insertMercadoProduct.getCreateBy());
                                mercadoProductItem.setUpdateTime(new Date());
                                mercadoProductItem.setUpdateBy(insertMercadoProduct.getUpdateBy());
                                mercadoProductItem.setDeleted(false);
                                insertItemList.add(mercadoProductItem);
                            }
                            mercadoProductItemMapper.batchProductItemList(insertItemList);
                        }

                        // 查询变体信息
                        MercadoProductCombination combinationExample = new MercadoProductCombination();
                        combinationExample.setProductId(Long.parseLong(productId));
                        List<MercadoProductCombination> mercadoProductCombinations = mercadoProductCombinationMapper.selectMercadoProductCombinationList(combinationExample);
                        if (!mercadoProductCombinations.isEmpty()) {
                            for (MercadoProductCombination mercadoProductCombination : mercadoProductCombinations) {
                                mercadoProductCombination.setId(IdUtil.getSnowflake(1, 1).nextId());
                                mercadoProductCombination.setProductId(insertMercadoProduct.getId());
                                mercadoProductCombination.setCreateTime(new Date());
                                mercadoProductCombination.setCreateBy(insertMercadoProduct.getCreateBy());
                                mercadoProductCombination.setUpdateTime(new Date());
                                mercadoProductCombination.setUpdateBy(insertMercadoProduct.getUpdateBy());
                                mercadoProductCombination.setDeleted(false);
                            }
                            mercadoProductCombinationMapper.batchMercadoProductCombination(mercadoProductCombinations);
                        }

                        // 查询属性信息
                        MercadoProductAttributes AttributesExample = new MercadoProductAttributes();
                        AttributesExample.setProductId(Long.parseLong(productId));
                        List<MercadoProductAttributes> mercadoProductAttributes = mercadoProductAttributesMapper.selectMercadoProductAttributesList(AttributesExample);
                        if (!mercadoProductAttributes.isEmpty()) {
                            for (MercadoProductAttributes mercadoProductAttribute : mercadoProductAttributes) {
                                mercadoProductAttribute.setId(IdUtil.getSnowflake(1, 1).nextId());
                                mercadoProductAttribute.setProductId(insertMercadoProduct.getId());
                                mercadoProductAttribute.setMerchantId(insertMercadoProduct.getMerchantId());
                                mercadoProductAttribute.setCreateTime(new Date());
                                mercadoProductAttribute.setCreateBy(insertMercadoProduct.getCreateBy());
                                mercadoProductAttribute.setUpdateTime(new Date());
                                mercadoProductAttribute.setUpdateBy(insertMercadoProduct.getUpdateBy());
                                mercadoProductAttribute.setDeleted(false);
                            }
                            mercadoProductAttributesMapper.batchMercadoProductAttributes(mercadoProductAttributes);
                        }
                    }
                }
            } else {
                logger.error("复制链接到别的店铺没有拿到锁 shopId {} productId {}", batchCopyLinkToShop.getShopId(), batchCopyLinkToShop.getProductId());
            }
        } catch (Exception e) {
            logger.error("复制链接到别的店铺异常 {} shopId {} productId {}", e, batchCopyLinkToShop.getShopId(), batchCopyLinkToShop.getProductId());
        } finally {
            long end = System.currentTimeMillis();
            logger.error("复制链接到别的店铺耗费时间 {} shopId {} productId {}", (end - startTime), batchCopyLinkToShop.getShopId(), batchCopyLinkToShop.getProductId());
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("复制链接到别的店铺放锁异常 {} shopId {} productId {}", e, batchCopyLinkToShop.getShopId(), batchCopyLinkToShop.getProductId());
            }
        }
    }

}
