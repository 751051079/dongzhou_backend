package com.smarterp.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.smarterp.common.core.exception.ServiceException;
import com.smarterp.common.security.utils.SecurityUtils;
import com.smarterp.order.domain.MercadoProduct;
import com.smarterp.order.domain.MercadoProductAttributes;
import com.smarterp.order.domain.MercadoProductCombination;
import com.smarterp.order.domain.MercadoProductItem;
import com.smarterp.order.domain.dto.*;
import com.smarterp.order.mapper.MercadoProductAttributesMapper;
import com.smarterp.order.mapper.MercadoProductCombinationMapper;
import com.smarterp.order.mapper.MercadoProductItemMapper;
import com.smarterp.order.mapper.MercadoProductMapper;
import com.smarterp.order.service.IMercadoProductTraService;
import com.smarterp.system.api.model.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 产品，店铺和站点信息关联Service业务层处理
 *
 * @author smarterp
 * @date 2023-04-26
 */
@Service
public class MercadoProductTraServiceImpl implements IMercadoProductTraService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoProductTraServiceImpl.class);

    @Resource
    private MercadoProductItemMapper mercadoProductItemMapper;

    @Resource
    private MercadoProductMapper mercadoProductMapper;

    @Resource
    private MercadoProductAttributesMapper mercadoProductAttributesMapper;

    @Resource
    private MercadoProductCombinationMapper mercadoProductCombinationMapper;

    @Override
    @Transactional
    public List<Long> releaseProduct(MercadoInfoDto mercadoInfoDto) {
        // 待发布的产品id
        List<Long> productIds = new ArrayList<>();
        // 根据deptId，全局产品id和商家id查询是否有重复的数据
        LoginUser currentUser = SecurityUtils.getLoginUser();
        // 获取商家id
        List<String> merchantIdList = new ArrayList<>();
        List<MercadoShopInfoDTO> mercadoShopInfoDTOList = mercadoInfoDto.getMercadoShopInfoDTOList();
        if (!mercadoShopInfoDTOList.isEmpty()) {
            for (MercadoShopInfoDTO item : mercadoShopInfoDTOList) {
                merchantIdList.add(item.getMerchantId());
            }
        }
        if (!mercadoShopInfoDTOList.isEmpty()) {
            for (MercadoShopInfoDTO mercadoShopInfoDTO : mercadoShopInfoDTOList) {
                String merchantId = mercadoShopInfoDTO.getMerchantId();
                MercadoProduct mercadoProduct = new MercadoProduct();
                mercadoProduct.setId(IdUtil.getSnowflake(1, 1).nextId());
                mercadoProduct.setUserId(currentUser.getUserid());
                mercadoProduct.setDeptId(currentUser.getSysUser().getDeptId());
                mercadoProduct.setMerchantId(merchantId);
                mercadoProduct.setMercadoProductUrl(mercadoInfoDto.getMercadoProductUrl());
                mercadoProduct.setProductTitle(mercadoInfoDto.getTitle());
                mercadoProduct.setProductDescription("全局产品简述");
                mercadoProduct.setSalePrice(mercadoInfoDto.getPrice());
                mercadoProduct.setCbtItemId(mercadoInfoDto.getId());
                mercadoProduct.setSkuPre(mercadoInfoDto.getSkuPre());
                mercadoProduct.setCbtCategory(mercadoInfoDto.getCategoryId());
                // 获取部门的一个upc
                String upc = this.getOneUpcByDeptId(mercadoProduct.getDeptId());
                mercadoProduct.setUpcCode(upc);
                mercadoProduct.setShipType(mercadoInfoDto.getShipType());
                mercadoProduct.setReleaseStatus("NoRelease");
                if (!mercadoInfoDto.getMercadoInfoPictureDtos().isEmpty()) {
                    mercadoProduct.setPictures(JSON.toJSONString(mercadoInfoDto.getMercadoInfoPictureDtos()));
                }
                mercadoProduct.setAvailableQuantity(999);
                mercadoProduct.setCreateTime(new Date());
                mercadoProduct.setCreateBy(String.valueOf(currentUser.getUserid()));
                mercadoProduct.setUpdateTime(new Date());
                mercadoProduct.setUpdateBy(String.valueOf(currentUser.getUserid()));
                mercadoProduct.setDeleted(false);
                mercadoProduct.setShipInfo(mercadoInfoDto.getShipInfo());
                Integer result = mercadoProductMapper.insertMercadoProduct(mercadoProduct);
                if (result > 0) {
                    productIds.add(mercadoProduct.getId());
                    // 存储跟产品相关的店铺信息
                    List<MercadoSiteInfo> mercadoSiteInfoList = mercadoShopInfoDTO.getMercadoSiteInfoList();
                    if (mercadoSiteInfoList != null && !mercadoSiteInfoList.isEmpty()) {
                        List<MercadoProductItem> itemInsertList = new ArrayList<>();
                        for (MercadoSiteInfo mercadoSiteInfo : mercadoSiteInfoList) {
                            MercadoProductItem mercadoProductItem = new MercadoProductItem();
                            mercadoProductItem.setId(IdUtil.getSnowflake(1, 1).nextId());
                            mercadoProductItem.setProductId(mercadoProduct.getId());
                            if (mercadoSiteInfo.getSiteId().equals("MLMFULL")) {
                                mercadoProductItem.setSiteId("MLM");
                                mercadoProductItem.setLogisticType("fulfillment");
                            } else {
                                mercadoProductItem.setSiteId(mercadoSiteInfo.getSiteId());
                                mercadoProductItem.setLogisticType("remote");
                            }
                            mercadoProductItem.setSiteSalePrice(mercadoSiteInfo.getPrice() == null ? mercadoInfoDto.getPrice() : mercadoSiteInfo.getPrice());
                            mercadoProductItem.setSiteProductDescription(mercadoSiteInfo.getDes());
                            mercadoProductItem.setSiteProductTitle(mercadoSiteInfo.getTitle());
                            mercadoProductItem.setSiteCategory(mercadoProduct.getCbtCategory());
                            mercadoProductItem.setSiteReleaseStatus("NoRelease");
                            mercadoProductItem.setSiteDescriptionStatus("NoRelease");
                            mercadoProductItem.setMerchantId(merchantId);
                            mercadoProductItem.setCreateTime(new Date());
                            mercadoProductItem.setCreateBy(String.valueOf(currentUser.getUserid()));
                            mercadoProductItem.setUpdateTime(new Date());
                            mercadoProductItem.setUpdateBy(String.valueOf(currentUser.getUserid()));
                            mercadoProductItem.setDeleted(false);
                            itemInsertList.add(mercadoProductItem);
                        }
                        mercadoProductItemMapper.batchProductItemList(itemInsertList);
                    }
                    // 属性
                    List<MercadoAttrInfo> mercadoAttrInfos = mercadoInfoDto.getMercadoAttrInfos();
                    if (!mercadoAttrInfos.isEmpty()) {
                        List<MercadoProductAttributes> inserAttributesList = new ArrayList<>();
                        for (MercadoAttrInfo mercadoAttrInfo : mercadoAttrInfos) {
                            MercadoProductAttributes mercadoProductAttributes = new MercadoProductAttributes();
                            mercadoProductAttributes.setId(IdUtil.getSnowflake(1, 1).nextId());
                            mercadoProductAttributes.setProductId(mercadoProduct.getId());
                            mercadoProductAttributes.setMerchantId(merchantId);
                            mercadoProductAttributes.setAttributeId(mercadoAttrInfo.getId());
                            mercadoProductAttributes.setAttributeName(mercadoAttrInfo.getName());
                            mercadoProductAttributes.setAttributeValueId(mercadoAttrInfo.getValueId());
                            mercadoProductAttributes.setAttributeValueName(mercadoAttrInfo.getValueName());
                            mercadoProductAttributes.setValueType(mercadoAttrInfo.getValueType());
                            // attribute表示属性
                            mercadoProductAttributes.setAttributeType("attribute");
                            mercadoProductAttributes.setCreateTime(new Date());
                            mercadoProductAttributes.setCreateBy(String.valueOf(currentUser.getUserid()));
                            mercadoProductAttributes.setUpdateTime(new Date());
                            mercadoProductAttributes.setUpdateBy(String.valueOf(currentUser.getUserid()));
                            mercadoProductAttributes.setDeleted(false);
                            inserAttributesList.add(mercadoProductAttributes);
                        }
                        mercadoProductAttributesMapper.batchMercadoProductAttributes(inserAttributesList);
                    }
                    // 变体
                    List<MercadoVarInfo> mercadoVarInfos = mercadoInfoDto.getMercadoVarInfos();
                    if (mercadoVarInfos != null && !mercadoVarInfos.isEmpty()) {
                        List<MercadoProductCombination> insertCombinationList = new ArrayList<>();
                        for (int index = 0; index < mercadoVarInfos.size(); index++) {
                            MercadoVarInfo mercadoVarInfo = mercadoVarInfos.get(index);
                            MercadoProductCombination mercadoProductCombination = new MercadoProductCombination();
                            mercadoProductCombination.setId(IdUtil.getSnowflake(1, 1).nextId());
                            mercadoProductCombination.setProductId(mercadoProduct.getId());
                            mercadoProductCombination.setCombinationSku(mercadoVarInfo.getSku());
                            mercadoProductCombination.setSizeChartId(mercadoVarInfo.getSizeChartId());
                            mercadoProductCombination.setPictures(JSON.toJSONString(mercadoVarInfo.getPictureIds()));
                            mercadoProductCombination.setCombinationJson(JSON.toJSONString(mercadoVarInfo.getAttributeCombinations()));
                            mercadoProductCombination.setAvailableQuantity(99);
                            mercadoProductCombination.setCombinationSort(index);
                            mercadoProductCombination.setCreateTime(new Date());
                            mercadoProductCombination.setCreateBy(String.valueOf(currentUser.getUserid()));
                            mercadoProductCombination.setUpdateTime(new Date());
                            mercadoProductCombination.setUpdateBy(String.valueOf(currentUser.getUserid()));
                            mercadoProductCombination.setDeleted(false);
                            insertCombinationList.add(mercadoProductCombination);
                        }
                        mercadoProductCombinationMapper.batchMercadoProductCombination(insertCombinationList);
                    }
                }
            }
        }
        return productIds;
    }

    // 获取一个upc
    private synchronized String getOneUpcByDeptId(Long deptId) {
        // 根据redis做全局锁
        // TODO
        Map<String, Object> upc = mercadoProductMapper.getOneUpcByDeptId(deptId);
        if (upc == null || upc.get("upcCode") == null) {
            throw new ServiceException("没有合适的upc码了,请导入upc");
        }
        mercadoProductMapper.updateUpcItemStatus(Long.parseLong(upc.get("id").toString()));
        mercadoProductMapper.updateUpcCount(Long.parseLong(upc.get("upcId").toString()));
        return upc.get("upcCode").toString();
    }
}
