package com.smarterp.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.order.domain.MercadoProduct;
import com.smarterp.order.domain.MercadoProductAttributes;
import com.smarterp.order.domain.MercadoProductCombination;
import com.smarterp.order.domain.MercadoProductItem;
import com.smarterp.order.mapper.*;
import com.smarterp.order.service.IMercadoSyncRemoteOrderService;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class MercadoSyncRemoteOrderServiceImpl implements IMercadoSyncRemoteOrderService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoSyncRemoteOrderServiceImpl.class);

    @Resource
    private MercadoCallBackMapper mercadoCallBackMapper;

    @Resource
    private MercadoProductMapper mercadoProductMapper;

    @Resource
    private MercadoProductItemMapper mercadoProductItemMapper;

    @Resource
    private MercadoProductCombinationMapper mercadoProductCombinationMapper;

    @Resource
    private MercadoProductAttributesMapper mercadoProductAttributesMapper;

    public final static Integer SYNC_REMOTE_ORDER_COUNT = 50;


    @Override
    public List<Long> syncRemoteOrderLijuntao() {
        List<Long> result = new ArrayList<>();
        // 查询出需要处理的订单id
        List<Long> productIds = mercadoCallBackMapper.getProductIds(SYNC_REMOTE_ORDER_COUNT);
        logger.error("查询的产品id" + JSON.toJSONString(productIds));
        try {
            if (!productIds.isEmpty()) {
                // TODO 插入产品逻辑
                // 店铺信息
                List<String> merchantIds = Arrays.asList("1661343542309687296", "1668239842682736640", "1668241494085079040");
                // 根据产品信息id查询产品信息
                List<MercadoProduct> mercadoProductList = new ArrayList<>();
                // 产品站点信息
                Map<Long, MercadoProductItem> siteMap = new HashMap<>();
                // 变体信息
                Map<Long, List<MercadoProductCombination>> combinationMap = new HashMap<>();
                // 属性信息
                Map<Long, List<MercadoProductAttributes>> attributesMap = new HashMap<>();
                for (Long productId : productIds) {
                    MercadoProduct mercadoProduct = mercadoProductMapper.selectMercadoProductById(productId);
                    if (mercadoProduct != null) {
                        mercadoProductList.add(mercadoProduct);

                        // 查询站点信息
                        MercadoProductItem siteExample = new MercadoProductItem();
                        siteExample.setProductId(productId);
                        siteExample.setSiteId("MLM");
                        siteExample.setLogisticType("remote");
                        List<MercadoProductItem> mercadoProductItems = mercadoProductItemMapper.selectMercadoProductItemList(siteExample);
                        if (!mercadoProductItems.isEmpty()) {
                            siteMap.put(productId, mercadoProductItems.get(0));
                        }

                        // 查询变体信息
                        MercadoProductCombination combinationExample = new MercadoProductCombination();
                        combinationExample.setProductId(productId);
                        List<MercadoProductCombination> mercadoProductCombinations = mercadoProductCombinationMapper.selectMercadoProductCombinationList(combinationExample);
                        if (!mercadoProductCombinations.isEmpty()) {
                            combinationMap.put(productId, mercadoProductCombinations);
                        }

                        // 查询属性信息
                        MercadoProductAttributes AttributesExample = new MercadoProductAttributes();
                        AttributesExample.setProductId(productId);
                        List<MercadoProductAttributes> mercadoProductAttributes = mercadoProductAttributesMapper.selectMercadoProductAttributesList(AttributesExample);
                        if (!mercadoProductAttributes.isEmpty()) {
                            attributesMap.put(productId, mercadoProductAttributes);
                        }
                    }
                }
                logger.error("查询出来的产品信息：{}", JSON.toJSONString(mercadoProductList));
                Long deptId = 103L;
                Long userId = 104L;
                for (String merchantId : merchantIds) {
                    for (MercadoProduct mercadoProductCopy : mercadoProductList) {
                        try {
                            if (siteMap.get(mercadoProductCopy.getId()) != null) {

                                // 插入产品信息
                                MercadoProduct mercadoProduct = new MercadoProduct();
                                mercadoProduct.setId(IdUtil.getSnowflake(1, 1).nextId());
                                mercadoProduct.setUserId(userId);
                                mercadoProduct.setDeptId(deptId);
                                mercadoProduct.setMerchantId(merchantId);
                                mercadoProduct.setMercadoProductUrl(mercadoProductCopy.getMercadoProductUrl());
                                mercadoProduct.setProductTitle(mercadoProductCopy.getProductTitle());
                                mercadoProduct.setProductDescription("全局产品简述");
                                mercadoProduct.setSalePrice(mercadoProductCopy.getSalePrice().add(new BigDecimal("3")));
                                mercadoProduct.setCbtItemId(mercadoProductCopy.getCbtItemId());
                                mercadoProduct.setSkuPre(mercadoProductCopy.getSkuPre());
                                mercadoProduct.setCbtCategory(mercadoProductCopy.getCbtCategory());
                                mercadoProduct.setUpcCode(mercadoProductCopy.getUpcCode());
                                mercadoProduct.setShipType(mercadoProductCopy.getShipType());
                                mercadoProduct.setReleaseStatus("NoRelease");
                                mercadoProduct.setPictures(mercadoProductCopy.getPictures());
                                mercadoProduct.setAvailableQuantity(999);
                                mercadoProduct.setCreateTime(new Date());
                                mercadoProduct.setCreateBy(String.valueOf(userId));
                                mercadoProduct.setUpdateTime(new Date());
                                mercadoProduct.setUpdateBy(String.valueOf(userId));
                                mercadoProduct.setDeleted(false);
                                mercadoProductMapper.insertMercadoProduct(mercadoProduct);

                                result.add(mercadoProduct.getId());

                                // 插入站点信息
                                MercadoProductItem mercadoProductItemCopy = siteMap.get(mercadoProductCopy.getId());
                                MercadoProductItem mercadoProductItem = new MercadoProductItem();
                                mercadoProductItem.setId(IdUtil.getSnowflake(1, 1).nextId());
                                mercadoProductItem.setProductId(mercadoProduct.getId());
                                mercadoProductItem.setSiteId("MLM");
                                mercadoProductItem.setLogisticType("remote");
                                mercadoProductItem.setSiteSalePrice(mercadoProductItemCopy.getSiteSalePrice().add(new BigDecimal("3")));
                                mercadoProductItem.setSiteProductDescription(mercadoProductItemCopy.getSiteProductDescription());
                                mercadoProductItem.setSiteProductTitle(mercadoProductItemCopy.getSiteProductTitle());
                                mercadoProductItem.setSiteCategory(mercadoProductItemCopy.getSiteCategory());
                                mercadoProductItem.setSiteReleaseStatus("NoRelease");
                                mercadoProductItem.setSiteDescriptionStatus("NoRelease");
                                mercadoProductItem.setMerchantId(merchantId);
                                mercadoProductItem.setCreateTime(new Date());
                                mercadoProductItem.setCreateBy(String.valueOf(userId));
                                mercadoProductItem.setUpdateTime(new Date());
                                mercadoProductItem.setUpdateBy(String.valueOf(userId));
                                mercadoProductItem.setDeleted(false);
                                mercadoProductItemMapper.insertMercadoProductItem(mercadoProductItem);

                                // 插入变体信息
                                if (combinationMap.get(mercadoProductCopy.getId()) != null) {
                                    List<MercadoProductCombination> mercadoProductCombinations = combinationMap.get(mercadoProductCopy.getId());
                                    for (MercadoProductCombination mercadoProductCombination : mercadoProductCombinations) {
                                        mercadoProductCombination.setId(IdUtil.getSnowflake(1, 1).nextId());
                                        mercadoProductCombination.setProductId(mercadoProduct.getId());
                                        mercadoProductCombination.setCreateTime(new Date());
                                        mercadoProductCombination.setCreateBy(String.valueOf(userId));
                                        mercadoProductCombination.setUpdateTime(new Date());
                                        mercadoProductCombination.setUpdateBy(String.valueOf(userId));
                                        mercadoProductCombination.setDeleted(false);
                                    }
                                    mercadoProductCombinationMapper.batchMercadoProductCombination(mercadoProductCombinations);
                                }

                                // 插入属性信息
                                if (attributesMap.get(mercadoProductCopy.getId()) != null) {
                                    List<MercadoProductAttributes> mercadoProductAttributesList = attributesMap.get(mercadoProductCopy.getId());
                                    for (MercadoProductAttributes mercadoProductAttributes : mercadoProductAttributesList) {
                                        mercadoProductAttributes.setId(IdUtil.getSnowflake(1, 1).nextId());
                                        mercadoProductAttributes.setProductId(mercadoProduct.getId());
                                        mercadoProductAttributes.setMerchantId(merchantId);
                                        mercadoProductAttributes.setCreateTime(new Date());
                                        mercadoProductAttributes.setCreateBy(String.valueOf(userId));
                                        mercadoProductAttributes.setUpdateTime(new Date());
                                        mercadoProductAttributes.setUpdateBy(String.valueOf(userId));
                                        mercadoProductAttributes.setDeleted(false);
                                    }
                                    mercadoProductAttributesMapper.batchMercadoProductAttributes(mercadoProductAttributesList);
                                }
                            }
                        } catch (Exception e) {
                            logger.error("同步订单数据失败,产品id：{}", mercadoProductCopy.getId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("同步订单数据失败：{}", e);
        }
        if (!productIds.isEmpty()) {
            // 将产品id 的状态更新为flag = 1
            mercadoCallBackMapper.updateStatusByProductIds(productIds);
        }
        return result;
    }

    @Override
    public List<Long> pullProductLijuntao() {
        return mercadoCallBackMapper.pullProductLijuntao();
    }

    @Override
    public List<Long> deleteProductLijuntao() {
        return mercadoCallBackMapper.deleteProductLijuntao();
    }

   /* @Override
    public int syncRemoteOrderLyh(int page, int size) {
        List<Long> result = new ArrayList<>();
        // 查询出需要处理的订单id
        List<Long> productIds = mercadoCallBackMapper.getProductIdsByLyh(page * size, size);
        logger.error("查询的产品id" + JSON.toJSONString(productIds));
        try {
            if (!productIds.isEmpty()) {
                // TODO 插入产品逻辑
                // 店铺信息
                List<String> merchantIds = Arrays.asList("1662135882976727040");
                // 根据产品信息id查询产品信息
                List<MercadoProduct> mercadoProductList = new ArrayList<>();
                // 产品站点信息
                Map<Long, MercadoProductItem> siteMap = new HashMap<>();
                // 变体信息
                Map<Long, List<MercadoProductCombination>> combinationMap = new HashMap<>();
                // 属性信息
                Map<Long, List<MercadoProductAttributes>> attributesMap = new HashMap<>();
                for (Long productId : productIds) {
                    MercadoProduct mercadoProduct = mercadoProductMapper.selectMercadoProductById(productId);
                    if (mercadoProduct != null) {
                        mercadoProductList.add(mercadoProduct);

                        // 查询站点信息
                        MercadoProductItem siteExample = new MercadoProductItem();
                        siteExample.setProductId(productId);
                        siteExample.setSiteId("MLM");
                        siteExample.setLogisticType("remote");
                        List<MercadoProductItem> mercadoProductItems = mercadoProductItemMapper.selectMercadoProductItemList(siteExample);
                        if (!mercadoProductItems.isEmpty()) {
                            siteMap.put(productId, mercadoProductItems.get(0));
                        }

                        // 查询变体信息
                        MercadoProductCombination combinationExample = new MercadoProductCombination();
                        combinationExample.setProductId(productId);
                        List<MercadoProductCombination> mercadoProductCombinations = mercadoProductCombinationMapper.selectMercadoProductCombinationList(combinationExample);
                        if (!mercadoProductCombinations.isEmpty()) {
                            combinationMap.put(productId, mercadoProductCombinations);
                        }

                        // 查询属性信息
                        MercadoProductAttributes AttributesExample = new MercadoProductAttributes();
                        AttributesExample.setProductId(productId);
                        List<MercadoProductAttributes> mercadoProductAttributes = mercadoProductAttributesMapper.selectMercadoProductAttributesList(AttributesExample);
                        if (!mercadoProductAttributes.isEmpty()) {
                            attributesMap.put(productId, mercadoProductAttributes);
                        }
                    }
                }
                logger.error("查询出来的产品信息：{}", JSON.toJSONString(mercadoProductList));
                Long deptId = 200L;
                Long userId = 102L;
                for (String merchantId : merchantIds) {
                    for (MercadoProduct mercadoProductCopy : mercadoProductList) {
                        try {
                            if (siteMap.get(mercadoProductCopy.getId()) != null) {
                                MercadoProductItem mercadoProductItemCopy = siteMap.get(mercadoProductCopy.getId());

                                //根据标题判断是否有重复产品
                                long count = mercadoProductMapper.selectProductLyhByTitle102(mercadoProductItemCopy.getSiteProductTitle());
                                if (count > 0) {
                                    continue;
                                }
                                //判断产品状态是否删除
                                JSONObject productInfo = MercadoNewHttpUtil.getProductInfo(mercadoProductItemCopy.getSiteItemId());
                                if (productInfo == null
                                        || !productInfo.get("status").equals("active")) {
                                    continue;
                                }


                                // 插入产品信息
                                MercadoProduct mercadoProduct = new MercadoProduct();
                                mercadoProduct.setId(IdUtil.getSnowflake(1, 1).nextId());
                                mercadoProduct.setUserId(userId);
                                mercadoProduct.setDeptId(deptId);
                                mercadoProduct.setMerchantId(merchantId);
                                mercadoProduct.setMercadoProductUrl(mercadoProductCopy.getMercadoProductUrl());
                                mercadoProduct.setProductTitle(mercadoProductCopy.getProductTitle());
                                mercadoProduct.setProductDescription("全局产品简述");
                                mercadoProduct.setSalePrice(mercadoProductCopy.getSalePrice().add(new BigDecimal("3")));
                                mercadoProduct.setCbtItemId(mercadoProductCopy.getCbtItemId());
                                mercadoProduct.setSkuPre(mercadoProductCopy.getSkuPre());
                                mercadoProduct.setCbtCategory(mercadoProductCopy.getCbtCategory());
                                mercadoProduct.setUpcCode(mercadoProductCopy.getUpcCode());
                                mercadoProduct.setShipType(mercadoProductCopy.getShipType());
                                mercadoProduct.setReleaseStatus("NoRelease");
                                mercadoProduct.setPictures(mercadoProductCopy.getPictures());
                                mercadoProduct.setAvailableQuantity(999);
                                mercadoProduct.setCreateTime(new Date());
                                mercadoProduct.setCreateBy(String.valueOf(userId));
                                mercadoProduct.setUpdateTime(new Date());
                                mercadoProduct.setUpdateBy(String.valueOf(userId));
                                mercadoProduct.setDeleted(false);
                                mercadoProductMapper.insertMercadoProduct(mercadoProduct);

                                result.add(mercadoProduct.getId());

                                // 插入站点信息

                                MercadoProductItem mercadoProductItem = new MercadoProductItem();
                                mercadoProductItem.setId(IdUtil.getSnowflake(1, 1).nextId());
                                mercadoProductItem.setProductId(mercadoProduct.getId());
                                mercadoProductItem.setSiteId("MLM");
                                mercadoProductItem.setLogisticType("remote");
                                if (mercadoProductItemCopy.getSiteSalePrice().add(new BigDecimal("3")).compareTo(
                                        mercadoProductCopy.getSalePrice()) > 0) {
                                    mercadoProductItem.setSiteSalePrice(mercadoProductItemCopy.getSiteSalePrice().add(new BigDecimal("3")));
                                } else {
                                    mercadoProductItem.setSiteSalePrice(mercadoProductCopy.getSalePrice());
                                }
                                //去除说明中的链接

                                String siteDes = mercadoProductItemCopy.getSiteProductDescription();
                                // 使用正则表达式匹配网址模式

                                siteDes = siteDes.replace("www.DeepL.com/Translator", "");
                                siteDes = siteDes.replace("http://www.DeepL.com/Translator", "");
                                siteDes = siteDes.replace("https://www.DeepL.com/Translator", "");
                                siteDes = siteDes.replace("https://", "");
                                siteDes = siteDes.replace("http://", "");
                                siteDes = siteDes.replace("www.", "");
                                siteDes = siteDes.replace(".com", "");

                                mercadoProductItem.setSiteProductDescription(siteDes);
                                mercadoProductItem.setSiteProductTitle(mercadoProductItemCopy.getSiteProductTitle());
                                mercadoProductItem.setSiteCategory(mercadoProductItemCopy.getSiteCategory());
                                mercadoProductItem.setSiteReleaseStatus("NoRelease");
                                mercadoProductItem.setSiteDescriptionStatus("NoRelease");
                                mercadoProductItem.setMerchantId(merchantId);
                                mercadoProductItem.setCreateTime(new Date());
                                mercadoProductItem.setCreateBy(String.valueOf(userId));
                                mercadoProductItem.setUpdateTime(new Date());
                                mercadoProductItem.setUpdateBy(String.valueOf(userId));
                                mercadoProductItem.setDeleted(false);
                                mercadoProductItemMapper.insertMercadoProductItem(mercadoProductItem);

                                // 插入变体信息
                                if (combinationMap.get(mercadoProductCopy.getId()) != null) {
                                    List<MercadoProductCombination> mercadoProductCombinations = combinationMap.get(mercadoProductCopy.getId());
                                    for (MercadoProductCombination mercadoProductCombination : mercadoProductCombinations) {
                                        mercadoProductCombination.setId(IdUtil.getSnowflake(1, 1).nextId());
                                        mercadoProductCombination.setProductId(mercadoProduct.getId());
                                        mercadoProductCombination.setCreateTime(new Date());
                                        mercadoProductCombination.setCreateBy(String.valueOf(userId));
                                        mercadoProductCombination.setUpdateTime(new Date());
                                        mercadoProductCombination.setUpdateBy(String.valueOf(userId));
                                        mercadoProductCombination.setDeleted(false);
                                    }
                                    mercadoProductCombinationMapper.batchMercadoProductCombination(mercadoProductCombinations);
                                }

                                // 插入属性信息
                                if (attributesMap.get(mercadoProductCopy.getId()) != null) {
                                    List<MercadoProductAttributes> mercadoProductAttributesList = attributesMap.get(mercadoProductCopy.getId());
                                    for (MercadoProductAttributes mercadoProductAttributes : mercadoProductAttributesList) {
                                        mercadoProductAttributes.setId(IdUtil.getSnowflake(1, 1).nextId());
                                        mercadoProductAttributes.setProductId(mercadoProduct.getId());
                                        mercadoProductAttributes.setMerchantId(merchantId);
                                        mercadoProductAttributes.setCreateTime(new Date());
                                        mercadoProductAttributes.setCreateBy(String.valueOf(userId));
                                        mercadoProductAttributes.setUpdateTime(new Date());
                                        mercadoProductAttributes.setUpdateBy(String.valueOf(userId));
                                        mercadoProductAttributes.setDeleted(false);
                                    }
                                    mercadoProductAttributesMapper.batchMercadoProductAttributes(mercadoProductAttributesList);
                                }
                            }
                        } catch (Exception e) {
                            logger.error("同步订单数据失败,产品id：{},:{}", mercadoProductCopy.getId(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("同步订单数据失败：{}", e);
        }
        if (!productIds.isEmpty()) {
            // 将产品id 的状态更新为flag = 1
            mercadoCallBackMapper.updateStatusByProductIdsByLyh(productIds);
        }
        return productIds.size();
    }*/

    @Override
    public Long pullProductLyh() {
        return mercadoCallBackMapper.pullProductLyh();
    }

    @Override
    public int pullProductIdLyh(int page, int size) {
        List<MercadoProduct> prs = mercadoProductMapper.pullProductIdLyh(page * size, size);
        for (MercadoProduct pr : prs) {
            long count = mercadoProductMapper.selectProductLyhById(pr.getId());
            if (count <= 0) {
                mercadoProductMapper.addProductLyh(pr);
            }

        }
        return prs.size();
    }

    @Override
    public int deleteProByLyh(int page, int size) {
        List<MercadoProduct> prs = mercadoProductMapper.pullProductByLyh(page * size, size);
        for (MercadoProduct pr : prs) {
            List<Long> count = mercadoProductMapper.selectProductLyhByTitle(pr.getProductTitle());
            if (count.size() > 1) {
                count.remove(pr.getId());
                mercadoProductMapper.deleteProductLyhById(count);
            }
            long count102 = mercadoProductMapper.selectProductLyhByTitle102(pr.getProductTitle());
            if (count102 > 0) {
                List<Long> list = new ArrayList<>();
                list.add(pr.getId());
                mercadoProductMapper.deleteProductLyhById(list);
            }

        }
        return prs.size();
    }

    @Override
    public List<Long> syncRemoteOrderBack(Integer limit) {
        List<Long> result = new ArrayList<>();
        try {
            result = mercadoCallBackMapper.selectProductIdsByLimit(limit);
            if (result.size() > 0) {
                // 备份product数据
                Integer productNums = mercadoCallBackMapper.backUpProductData(result);
                // 备份product_item数据
                Integer productItemNums = mercadoCallBackMapper.backUpProductItemData(result);
                // 备份product_attributes数据
                Integer productAttributesNums = mercadoCallBackMapper.backUpProductAttributesData(result);
                // 备份product_combination数据
                Integer productCombinationNums = mercadoCallBackMapper.backUpProductCombinationData(result);

                // 删除产品数据
                mercadoProductMapper.deleteMercadoProductByProductIds(result);
                // 删除站点数据
                mercadoProductItemMapper.deleteMercadoProductItemByProductIds(result);
                // 删除属性数据
                mercadoProductAttributesMapper.deleteMercadoProductAttributesByProductIds(result);
                // 删除变体数据
                mercadoProductCombinationMapper.deleteMercadoProductCombinationByProductIds(result);
            }
        } catch (Exception e) {
            logger.error("同步数据报错 {}", e.getMessage());
        }
        return result;
    }
}
