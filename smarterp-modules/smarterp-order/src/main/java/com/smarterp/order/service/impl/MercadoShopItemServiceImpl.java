package com.smarterp.order.service.impl;

import com.github.pagehelper.PageHelper;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.common.security.utils.SecurityUtils;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoShopItem;
import com.smarterp.order.domain.dto.*;
import com.smarterp.order.mapper.MercadoBatchCollectionLinkMapper;
import com.smarterp.order.mapper.MercadoProductMapper;
import com.smarterp.order.mapper.MercadoShopItemMapper;
import com.smarterp.order.service.IMercadoShopItemService;
import com.smarterp.system.api.model.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 店铺产品Service业务层处理
 *
 * @author smarterp
 * @date 2024-06-09
 */
@Service
public class MercadoShopItemServiceImpl implements IMercadoShopItemService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoShopItemServiceImpl.class);

    @Resource
    private MercadoShopItemMapper mercadoShopItemMapper;

    @Resource
    private MercadoProductMapper mercadoProductMapper;

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private RedisService redisService;

    @Resource
    private MercadoBatchCollectionLinkMapper mercadoBatchCollectionLinkMapper;

    /**
     * 查询店铺产品
     *
     * @param id 店铺产品主键
     * @return 店铺产品
     */
    @Override
    public MercadoShopItem selectMercadoShopItemById(String id) {
        return mercadoShopItemMapper.selectMercadoShopItemById(id);
    }

    /**
     * 查询店铺产品列表
     *
     * @param mercadoShopItem 店铺产品
     * @return 店铺产品
     */
    @Override
    public List<MercadoShopItem> selectMercadoShopItemList(MercadoShopItem mercadoShopItem, Integer pageNum, Integer pageSize) {
        if (mercadoShopItem.getMerchantId() != null) {
            List<String> userIdList = new ArrayList<>();
            // 根据店铺id获取站点信息
            List<MercadoShopInfoSite> mercadoShopInfoSites = mercadoProductMapper.getMercadoInfoSiteByMerchantId(mercadoShopItem.getMerchantId());
            if (!mercadoShopInfoSites.isEmpty()) {
                for (MercadoShopInfoSite mercadoShopInfoSite : mercadoShopInfoSites) {
                    userIdList.add(mercadoShopInfoSite.getUserId());
                }
            }
            if (userIdList.isEmpty()) {
                userIdList.add("666666");
            }
            mercadoShopItem.setUserIdList(userIdList);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<MercadoShopItem> mercadoShopItems = mercadoShopItemMapper.selectMercadoShopItemList(mercadoShopItem);
        if (!mercadoShopItems.isEmpty()) {
            for (MercadoShopItem item : mercadoShopItems) {
                String sellerName = redisService.getCacheObject("MERCADO_SHOP_INFO:NAME:" + item.getSellerId());
                if (sellerName != null) {
                    item.setSellerName(sellerName);
                }
            }
        }
        return mercadoShopItems;
    }

    /**
     * 新增店铺产品
     *
     * @param mercadoShopItem 店铺产品
     * @return 结果
     */
    @Override
    public int insertMercadoShopItem(MercadoShopItem mercadoShopItem) {
        return mercadoShopItemMapper.insertMercadoShopItem(mercadoShopItem);
    }

    /**
     * 修改店铺产品
     *
     * @param mercadoShopItem 店铺产品
     * @return 结果
     */
    @Override
    public int updateMercadoShopItem(MercadoShopItem mercadoShopItem) {
        return mercadoShopItemMapper.updateMercadoShopItem(mercadoShopItem);
    }

    /**
     * 批量删除店铺产品
     *
     * @param ids 需要删除的店铺产品主键
     * @return 结果
     */
    @Override
    public int deleteMercadoShopItemByIds(String[] ids) {
        return mercadoShopItemMapper.deleteMercadoShopItemByIds(ids);
    }

    /**
     * 删除店铺产品信息
     *
     * @param id 店铺产品主键
     * @return 结果
     */
    @Override
    public int deleteMercadoShopItemById(String id) {
        return mercadoShopItemMapper.deleteMercadoShopItemById(id);
    }

    @Override
    public AjaxResult updateShopItemInfo(String merchantId) {
        UpdateShopItemInfo updateShopItemInfo = new UpdateShopItemInfo();
        updateShopItemInfo.setMerchantId(merchantId);
        amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                RabbitMQConstant.RoutingKey.GET_MERCADO_SHOP_ITEM_INFO_PAGE, updateShopItemInfo);
        return AjaxResult.success("获取店铺产品详情成功!");
    }

    @Override
    public AjaxResult updateAllShopItemInfo() {
        List<MercadoShopInfoSite> mercadoShopInfoSites = mercadoProductMapper.getMercadoInfoSiteAll();
        if (!mercadoShopInfoSites.isEmpty()) {
            for (MercadoShopInfoSite mercadoShopInfoSite : mercadoShopInfoSites) {
                amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                        RabbitMQConstant.RoutingKey.GET_MERCADO_SHOP_ITEM_INFO_PAGE, mercadoShopInfoSite);
            }
        }
        return AjaxResult.success("获取所有店铺产品详情成功!");
    }

    @Override
    public AjaxResult batchCollectionToShop(BatchCollectionLinkToShop batchCollectionLinkToShop) {
        if (batchCollectionLinkToShop.getLinkIdList() != null && !batchCollectionLinkToShop.getLinkIdList().isEmpty()) {
            if (batchCollectionLinkToShop.getShopId() != null) {
                List<String> linkIdList = batchCollectionLinkToShop.getLinkIdList();
                // 获取对应数量的UPC编码
                LoginUser currentUser = SecurityUtils.getLoginUser();
                List<MercadoUpcItem> mercadoUpcItems = mercadoBatchCollectionLinkMapper.getUpcCodeByDept(currentUser.getSysUser().getDeptId(), linkIdList.size());
                if (mercadoUpcItems.isEmpty()) {
                    return AjaxResult.error("无可用的UPC编码!");
                }
                if (mercadoUpcItems.size() < linkIdList.size()) {
                    return AjaxResult.error("UPC编码数量不够!此次采集需要" + linkIdList.size() + "个UPC编码,实际只有" + mercadoUpcItems.size() + "个!");
                }
                // 更新UPC编码的总数，已用数量和可用数量 以及更新UPC编码的状态
                Set<Long> upcIds = new HashSet<>();
                List<Long> upcIdList = new ArrayList<>();
                for (MercadoUpcItem mercadoUpcItem : mercadoUpcItems) {
                    upcIds.add(mercadoUpcItem.getUpcId());
                    upcIdList.add(mercadoUpcItem.getId());
                }
                // 更新UPC编码的状态,将状态更改为已使用
                if (!upcIdList.isEmpty()) {
                    Integer count = mercadoBatchCollectionLinkMapper.updateUpcStatus(upcIdList);
                    if (count > 0) {
                        // 更新UPC编码的总数，已用数量和可用数量
                        if (!upcIds.isEmpty()) {
                            for (Long upcId : upcIds) {
                                amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                                        RabbitMQConstant.RoutingKey.UPDATE_UPC_CODE_INFO, upcId);
                            }
                        }
                    }
                }
                // 获取SKU编号
                List<String> skuNoList = getSkuNoList(linkIdList.size(), batchCollectionLinkToShop.getShopId());
                if (skuNoList.isEmpty()) {
                    return AjaxResult.error("获取SKU编码失败!");
                }
                for (int index = 0; index < linkIdList.size(); index++) {
                    String linkId = linkIdList.get(index);
                    BatchCollectionLinkToShop record = new BatchCollectionLinkToShop();
                    record.setLinkId(linkId);
                    record.setShopId(batchCollectionLinkToShop.getShopId());
                    record.setUpcCode(mercadoUpcItems.get(index).getUpcCode());
                    record.setPreSku(skuNoList.get(index));
                    record.setDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
                    record.setUserId(SecurityUtils.getLoginUser().getUserid());
                    amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                            RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_BATCH_COLLECTION_ITEM_TO_SHOP, record);
                }
            }
        }
        return AjaxResult.success("采集链接到店铺成功,稍后请刷新页面!");
    }

    @Override
    public AjaxResult batchEdit(String[] ids) {
        for (int index = 0; index < ids.length; index++) {
            String id = ids[index];
            MercadoShopItem mercadoShopItem = mercadoShopItemMapper.selectMercadoShopItemById(id);
            if (mercadoShopItem != null) {
                // 更新图片
                mercadoShopItem.setUpdatePictures(true);
                amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                        RabbitMQConstant.RoutingKey.GET_MERCADO_SHOP_ITEM_INFO_PAGE_MARKETPLACE_ITEM_NEXT, mercadoShopItem);
            }
        }
        return AjaxResult.success("批量更新产品信息成功,稍后请刷新页面!");
    }

    /**
     * 根据sku个数获取对应数量的编码并且拼接成Sku
     *
     * @param skuCount
     * @return
     */
    private List<String> getSkuNoList(Integer skuCount, String ShopId) {
        List<String> skuNoList = new ArrayList<>();
        try {
            // 使用店铺名称作为sku前缀
            MercadoShopInfo mercadoShopInfo = mercadoProductMapper.getAccessTokenByMerchantId(Long.parseLong(ShopId));
            if (mercadoShopInfo != null) {
                if (mercadoShopInfo.getMercadoShopName() != null) {
                    String skuStartNo = redisService.getJSONStringByKey("SKU:CODE:SHOPID:" + ShopId);
                    if (skuStartNo != null) {
                        Integer endCount = Integer.parseInt(skuStartNo) + skuCount;
                        for (int index = Integer.parseInt(skuStartNo) + 1; index <= endCount; index++) {
                            String skuNo = mercadoShopInfo.getMercadoShopName() + "-" + index;
                            skuNoList.add(skuNo);
                        }
                        redisService.setCacheObject("SKU:CODE:SHOPID:" + ShopId, String.valueOf(endCount));
                    } else {
                        // 编号
                        redisService.setCacheObject("SKU:CODE:SHOPID:" + ShopId, String.valueOf(skuCount));
                        for (int index = 1; index <= skuCount; index++) {
                            String skuNo = mercadoShopInfo.getMercadoShopName() + "-" + index;
                            skuNoList.add(skuNo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取SKU编号失败 {}", e);
        }
        return skuNoList;
    }
}
