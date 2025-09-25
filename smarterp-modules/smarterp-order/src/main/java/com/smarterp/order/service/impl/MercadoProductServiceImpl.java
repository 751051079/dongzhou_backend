package com.smarterp.order.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.exception.ServiceException;
import com.smarterp.common.core.utils.DateUtils;
import com.smarterp.common.core.utils.StringUtils;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.core.utils.mercado.MercadoNewHttpUtil;
import com.smarterp.common.core.utils.mercado.MercadoSecureUtil;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.datascope.annotation.DataScope;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.common.security.utils.SecurityUtils;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.*;
import com.smarterp.order.domain.dto.*;
import com.smarterp.order.domain.vo.MercadoProductDetail;
import com.smarterp.order.domain.vo.MercadoProductItemDTO;
import com.smarterp.order.domain.vo.MercadoReleaseProductDTO;
import com.smarterp.order.domain.vo.MercadoSizeChartDTO;
import com.smarterp.order.mapper.*;
import com.smarterp.order.service.IMercadoProductService;
import com.smarterp.system.api.model.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    private MercadoProductMapper mercadoProductMapper;

    @Resource
    private MercadoProductItemMapper mercadoProductItemMapper;

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private MercadoProductAttributesMapper mercadoProductAttributesMapper;

    @Resource
    private MercadoProductCombinationMapper mercadoProductCombinationMapper;

    @Resource
    private MercadoProductErrorLogMapper mercadoProductErrorLogMapper;

    @Resource
    private MercadoBatchCollectionLinkMapper mercadoBatchCollectionLinkMapper;

    @Resource
    private RedisService redisService;

    // 墨西哥比索兑美元汇率
    @Value("${mercado.price.increaseFactor}")
    private String increaseFactor;

    /**
     * 查询产品
     *
     * @param id 产品主键
     * @return 产品
     */
    @Override
    public MercadoProduct selectMercadoProductById(Long id) {
        return mercadoProductMapper.selectMercadoProductById(id);
    }

    /**
     * 查询产品列表
     *
     * @param mercadoProduct 产品
     * @return 产品
     */
    @Override
    @DataScope(deptAlias = "dept", userAlias = "su")
    public List<MercadoProductDetail> selectMercadoProductList(MercadoProduct mercadoProduct) {
        List<MercadoProductDetail> mercadoProductDetailList = mercadoProductMapper.selectMercadoProductList(mercadoProduct);
        if (!mercadoProductDetailList.isEmpty()) {
            for (MercadoProductDetail item : mercadoProductDetailList) {
                // 处理首图信息
                item.setPictureUrl(JSONArray.parse(item.getPictureUrl()).getJSONObject(0).getString("url"));
                // 处理长宽高信息
                try {
                    if (item.getShipInfo() != null) {
                        JSONArray jsonArray = JSON.parseArray(item.getShipInfo());
                        for (int index = 0; index < jsonArray.size(); index++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(index);
                            String id = jsonObject.getString("id");
                            if ("PACKAGE_HEIGHT".equals(id)) {
                                item.setPackageHeight(jsonObject.getString("value_name"));
                            }
                            if ("PACKAGE_LENGTH".equals(id)) {
                                item.setPackageLength(jsonObject.getString("value_name"));
                            }
                            if ("PACKAGE_WEIGHT".equals(id)) {
                                item.setPackageWeight(jsonObject.getString("value_name"));
                            }
                            if ("PACKAGE_WIDTH".equals(id)) {
                                item.setPackageWidth(jsonObject.getString("value_name"));
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("解析长宽高尺寸异常");
                }
                // 根据id查询具体店铺详情信息
                MercadoProductItem example = new MercadoProductItem();
                example.setProductId(item.getId());
                List<MercadoProductItem> mercadoProductItems = mercadoProductItemMapper.selectMercadoProductItemList(example);
                if (!mercadoProductItems.isEmpty()) {
                    List<MercadoProductItemDTO> mercadoProductItemList = new ArrayList<>();
                    for (MercadoProductItem mercadoProductItem : mercadoProductItems) {
                        MercadoProductItemDTO mercadoProductItemDTO = new MercadoProductItemDTO();
                        mercadoProductItemDTO.setItemId(mercadoProductItem.getId());
                        mercadoProductItemDTO.setProductId(mercadoProductItem.getProductId());
                        mercadoProductItemDTO.setSiteId(mercadoProductItem.getSiteId());
                        mercadoProductItemDTO.setLogisticType(mercadoProductItem.getLogisticType());
                        mercadoProductItemDTO.setSiteSalePrice(mercadoProductItem.getSiteSalePrice());
                        mercadoProductItemDTO.setSiteReleaseStatus(mercadoProductItem.getSiteReleaseStatus());
                        mercadoProductItemDTO.setSiteDescriptionStatus(mercadoProductItem.getSiteDescriptionStatus());
                        mercadoProductItemDTO.setSiteItemId(mercadoProductItem.getSiteItemId());
                        mercadoProductItemDTO.setPermalink(mercadoProductItem.getPermalink());
                        mercadoProductItemList.add(mercadoProductItemDTO);
                    }
                    item.setMercadoProductItemList(mercadoProductItemList);
                }
            }
        }
        return mercadoProductDetailList;
    }

    /**
     * 新增产品
     *
     * @param mercadoProduct 产品
     * @return 结果
     */
    @Override
    public int insertMercadoProduct(MercadoProduct mercadoProduct) {
        mercadoProduct.setCreateTime(DateUtils.getNowDate());
        return mercadoProductMapper.insertMercadoProduct(mercadoProduct);
    }

    /**
     * 修改产品
     *
     * @param mercadoInfoDto 产品
     * @return 结果
     */
    @Override
    @Transactional
    public AjaxResult updateMercadoProduct(MercadoInfoDto mercadoInfoDto) {
        if (mercadoInfoDto.getProductId() == null) {
            return AjaxResult.error("缺少productId参数");
        }
        MercadoProduct mercadoProductQuery = mercadoProductMapper.selectMercadoProductById(mercadoInfoDto.getProductId());
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (mercadoProductQuery != null) {
            // 删除站点数据
            mercadoProductItemMapper.deleteMercadoProductItemByProductId(mercadoProductQuery.getId());
            // 删除属性数据
            mercadoProductAttributesMapper.deleteMercadoProductAttributesByProductId(mercadoProductQuery.getId());
            // 删除变体数据
            mercadoProductCombinationMapper.deleteMercadoProductCombinationByProductId(mercadoProductQuery.getId());
            List<MercadoShopInfoDTO> mercadoShopInfoDTOList = mercadoInfoDto.getMercadoShopInfoDTOList();
            if (!mercadoShopInfoDTOList.isEmpty()) {
                for (MercadoShopInfoDTO mercadoShopInfoDTO : mercadoShopInfoDTOList) {
                    String merchantId = mercadoShopInfoDTO.getMerchantId();
                    MercadoProduct mercadoProduct = new MercadoProduct();
                    mercadoProduct.setId(mercadoProductQuery.getId());
                    mercadoProduct.setUserId(mercadoProductQuery.getUserId());
                    mercadoProduct.setDeptId(mercadoProductQuery.getDeptId());
                    mercadoProduct.setMerchantId(merchantId);
                    mercadoProduct.setMercadoProductUrl(mercadoInfoDto.getMercadoProductUrl());
                    mercadoProduct.setProductTitle(mercadoInfoDto.getTitle());
                    mercadoProduct.setProductDescription("全局产品简述");
                    mercadoProduct.setSalePrice(mercadoInfoDto.getPrice());
                    mercadoProduct.setCbtItemId(mercadoInfoDto.getId());
                    mercadoProduct.setSkuPre(mercadoInfoDto.getSkuPre());
                    mercadoProduct.setCbtCategory(mercadoInfoDto.getCategoryId());
                    // 获取部门的一个upc
                    mercadoProduct.setUpcCode(mercadoProductQuery.getUpcCode());
                    mercadoProduct.setShipType(mercadoInfoDto.getShipType());
                    mercadoProduct.setReleaseStatus("NoRelease");
                    if (!mercadoInfoDto.getMercadoInfoPictureDtos().isEmpty()) {
                        mercadoProduct.setPictures(JSON.toJSONString(mercadoInfoDto.getMercadoInfoPictureDtos()));
                    }
                    mercadoProduct.setAvailableQuantity(999);
                    mercadoProduct.setUpdateTime(new Date());
                    Integer result = mercadoProductMapper.updateMercadoProduct(mercadoProduct);
                    if (result > 0) {
                        // 存储跟产品相关的店铺信息
                        List<MercadoSiteInfo> mercadoSiteInfoList = mercadoShopInfoDTO.getMercadoSiteInfoList();
                        if (mercadoSiteInfoList != null && !mercadoSiteInfoList.isEmpty()) {
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
                                mercadoProductItem.setMerchantId(merchantId);
                                mercadoProductItem.setCreateTime(new Date());
                                mercadoProductItem.setCreateBy(String.valueOf(currentUser.getUserid()));
                                mercadoProductItem.setUpdateTime(new Date());
                                mercadoProductItem.setUpdateBy(String.valueOf(currentUser.getUserid()));
                                mercadoProductItem.setDeleted(false);
                                mercadoProductItemMapper.insertMercadoProductItem(mercadoProductItem);
                            }
                        }
                        // 存储产品相关的产品属性信息
                        // 卖家条款
                        List<MercadoAttrInfo> mercadoSaleTerms = mercadoInfoDto.getMercadoSaleTerms();
                        if (mercadoSaleTerms != null && !mercadoSaleTerms.isEmpty()) {
                            for (MercadoAttrInfo mercadoAttrInfo : mercadoSaleTerms) {
                                MercadoProductAttributes mercadoProductAttributes = new MercadoProductAttributes();
                                mercadoProductAttributes.setId(IdUtil.getSnowflake(1, 1).nextId());
                                mercadoProductAttributes.setProductId(mercadoProduct.getId());
                                mercadoProductAttributes.setMerchantId(merchantId);
                                mercadoProductAttributes.setAttributeId(mercadoAttrInfo.getId());
                                mercadoProductAttributes.setAttributeName(mercadoAttrInfo.getName());
                                mercadoProductAttributes.setAttributeValueId(mercadoAttrInfo.getValueId());
                                mercadoProductAttributes.setAttributeValueName(mercadoAttrInfo.getValueName());
                                mercadoProductAttributes.setAttributeValues(JSON.toJSONString(mercadoAttrInfo.getValues()));
                                mercadoProductAttributes.setValueType(mercadoAttrInfo.getValueType());
                                // SaleTerms表示卖家条款
                                mercadoProductAttributes.setAttributeType("SaleTerms");
                                mercadoProductAttributes.setCreateTime(new Date());
                                mercadoProductAttributes.setCreateBy(String.valueOf(currentUser.getUserid()));
                                mercadoProductAttributes.setUpdateTime(new Date());
                                mercadoProductAttributes.setUpdateBy(String.valueOf(currentUser.getUserid()));
                                mercadoProductAttributes.setDeleted(false);
                                mercadoProductAttributesMapper.insertMercadoProductAttributes(mercadoProductAttributes);
                            }
                        }
                        // 属性
                        List<MercadoAttrInfo> mercadoAttrInfos = mercadoInfoDto.getMercadoAttrInfos();
                        if (!mercadoAttrInfos.isEmpty()) {
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
                                mercadoProductAttributesMapper.insertMercadoProductAttributes(mercadoProductAttributes);
                            }
                        }
                        // 变体
                        List<MercadoVarInfo> mercadoVarInfos = mercadoInfoDto.getMercadoVarInfos();
                        if (mercadoVarInfos != null && !mercadoVarInfos.isEmpty()) {
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
                                mercadoProductCombinationMapper.insertMercadoProductCombination(mercadoProductCombination);
                            }
                        }
                    }
                }
            }
        }
        return AjaxResult.success("更新成功");
    }

    /**
     * 批量删除产品
     *
     * @param ids 需要删除的产品主键
     * @return 结果
     */
    @Override
    public int deleteMercadoProductByIds(Long[] ids) {
        return mercadoProductMapper.deleteMercadoProductByIds(ids);
    }

    /**
     * 删除产品信息
     *
     * @param id 产品主键
     * @return 结果
     */
    @Override
    @Transactional
    public AjaxResult deleteMercadoProductById(Long id) {
        Integer result = mercadoProductMapper.deleteMercadoProductById(id);
        if (result > 0) {
            // 删除站点数据
            mercadoProductItemMapper.deleteMercadoProductItemByProductId(id);
            // 删除属性数据
            mercadoProductAttributesMapper.deleteMercadoProductAttributesByProductId(id);
            // 删除变体数据
            mercadoProductCombinationMapper.deleteMercadoProductCombinationByProductId(id);
        }
        return result > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }

    @Override
    public CpMercadoInfoDto getProductInfoById(Long id) {
        //查询全局产品信息
        MercadoProduct mercadoProduct = mercadoProductMapper.selectMercadoProductById(id);
        //查询站点产品信息

        //查询站点产品信息
        MercadoProductItem example = new MercadoProductItem();
        example.setProductId(id);
        List<MercadoProductItem> mercadoProductItems = mercadoProductItemMapper.selectMercadoProductItemList(example);

        //封装全局产品信息
        CpMercadoInfoDto cpMercadoInfoDto = new CpMercadoInfoDto();
        cpMercadoInfoDto.setProductId(Long.toString(id));
        cpMercadoInfoDto.setId(mercadoProduct.getCbtProId());
        cpMercadoInfoDto.setCategoryId(mercadoProduct.getCbtCategory());
        cpMercadoInfoDto.setEnglishTitle(mercadoProduct.getProductTitle());
        cpMercadoInfoDto.setPrice(mercadoProductItems.get(0).getSiteSalePrice());
        cpMercadoInfoDto.setSkuPre(mercadoProduct.getSkuPre());
        cpMercadoInfoDto.setGears(mercadoProduct.getShipType());
        cpMercadoInfoDto.setMercadoUrl(mercadoProduct.getMercadoProductUrl());
        cpMercadoInfoDto.setDomainId(mercadoProduct.getDomainId());
        cpMercadoInfoDto.setGenderId(mercadoProduct.getGenderId());
        List<String> shopIds = new ArrayList<>();
        shopIds.add(mercadoProduct.getMerchantId());
        cpMercadoInfoDto.setShopIds(shopIds);
        String genderId = mercadoProduct.getGenderId();
        if (genderId != null) {
            if (genderId.equals("339665")) {
                cpMercadoInfoDto.setGenderName("Woman");
            } else if (genderId.equals("339666")) {
                cpMercadoInfoDto.setGenderName("Man");
            } else if (genderId.equals("339668")) {
                cpMercadoInfoDto.setGenderName("Girls");
            } else if (genderId.equals("339667")) {
                cpMercadoInfoDto.setGenderName("Boys");
            } else if (genderId.equals("110461")) {
                cpMercadoInfoDto.setGenderName("Gender_neutral");
            } else if (genderId.equals("1915949")) {
                cpMercadoInfoDto.setGenderName("Gender_neutral_KID");
            }
        }

        //封装基础属性
        List<Map<String, Object>> infoList = new ArrayList<>();
        MercadoProductAttributes attributesExample = new MercadoProductAttributes();
        attributesExample.setProductId(id);
        attributesExample.setAttributeType("attribute");
        List<MercadoProductAttributes> attributeList = mercadoProductAttributesMapper.selectMercadoProductAttributesList(attributesExample);
        attributeList.forEach(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getAttributeId());
            map.put("name", item.getAttributeName());
            map.put("value", item.getAttributeValueName());
            infoList.add(map);
        });
        cpMercadoInfoDto.setInfoList(infoList);
        //封装变体颜色和尺寸和图片
        // 根据productId查询变体信息
        MercadoProductCombination exampleCombination = new MercadoProductCombination();
        exampleCombination.setProductId(id);
        List<MercadoProductCombination> mercadoProductCombinations = mercadoProductCombinationMapper.selectMercadoProductCombinationList(exampleCombination);
        //变体颜色
        List<String> colorTags = new ArrayList<>();
        String tab = "";
        //变体尺寸
        List<String> sizeTags = new ArrayList<>();
        String bor = "";
        //图片
        List<Map<String, Object>> colorImagesList = new ArrayList<>();
        for (MercadoProductCombination mercadoProductCombination : mercadoProductCombinations) {
            String combinationJson = mercadoProductCombination.getCombinationJson();
            JSONArray listNew = JSONArray.parseArray(combinationJson);
            for (int i = 0; i < listNew.size(); i++) {
                JSONObject attribute_combinationJson = JSONObject.from(listNew.get(i));
                if (i == 0 && !colorTags.contains(attribute_combinationJson.getString("valueName"))) {
                    tab = attribute_combinationJson.getString("id");
                    colorTags.add(attribute_combinationJson.getString("valueName"));
                    Map<String, Object> map = new HashMap<>();
                    map.put("color", attribute_combinationJson.getString("valueName"));
                    map.put("listFileOld", JSONArray.parseArray(mercadoProductCombination.getPictures(), String.class));
                    colorImagesList.add(map);
                }

                if (i == 1 && !sizeTags.contains(attribute_combinationJson.getString("valueName"))) {
                    bor = attribute_combinationJson.getString("id");
                    sizeTags.add(attribute_combinationJson.getString("valueName"));
                }
            }
        }


        Map<String, Object> colorMap = new HashMap<>();
        colorMap.put("id", tab);
        colorMap.put("colorTags", colorTags);
        cpMercadoInfoDto.setColorMap(colorMap);

        Map<String, Object> sizeMap = new HashMap<>();
        sizeMap.put("id", bor);
        sizeMap.put("sizeTags", sizeTags);
        cpMercadoInfoDto.setSizeMap(sizeMap);

        //图片对象
        JSONArray pictures = JSONArray.parseArray(mercadoProduct.getPictures());
        for (Map<String, Object> map : colorImagesList) {
            List<String> imageIds = (List<String>) map.get("listFileOld");
            List<Map<String, String>> collect1 = imageIds.stream().map(item -> {
                Map<String, String> mapPic = new HashMap<>();
                for (Object pic : pictures) {
                    JSONObject picjson = JSONObject.from(pic);
                    if (picjson.getString("id").equals(item)) {
                        mapPic.put("id", item);
                        mapPic.put("url", picjson.getString("url"));
                    }
                }
                return mapPic;
            }).collect(Collectors.toList());
            map.put("fileList", collect1);
            map.remove("listFileOld");
        }
        if (colorImagesList.size() == 0) {
            //说明没有变体,那么图片的color直接用空字符串
            Map<String, Object> map = new HashMap<>();
            map.put("color", "");
            map.put("fileList", pictures);
            colorImagesList.add(map);
        }
        cpMercadoInfoDto.setColorImagesList(colorImagesList);
        //标题和说明和站点价格
        List<Map<String, Object>> sitesPrice = cpMercadoInfoDto.getSitesPrice();
        mercadoProductItems.forEach(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getSiteId());
            map.put("price", item.getSiteSalePrice());
            sitesPrice.add(map);
            if ("MLM".equals(item.getSiteId()) || "MLC".equals(item.getSiteId()) ||
                    "MCO".equals(item.getSiteId()) || "MLMFULL".equals(item.getSiteId())) {
                if (cpMercadoInfoDto.getSpanishTitle() == null) {
                    cpMercadoInfoDto.setSpanishTitle(item.getSiteProductTitle());
                }

                if (cpMercadoInfoDto.getSpanishMsg() == null) {
                    cpMercadoInfoDto.setSpanishMsg(item.getSiteProductDescription());
                }
            } else if ("MLB".equals(item.getSiteId())) {
                if (cpMercadoInfoDto.getPortugueseTitle() == null) {
                    cpMercadoInfoDto.setPortugueseTitle(item.getSiteProductTitle());
                }

                if (cpMercadoInfoDto.getPortugueseMsg() == null) {
                    cpMercadoInfoDto.setPortugueseMsg(item.getSiteProductDescription());
                }
            }
        });


        return cpMercadoInfoDto;
    }

    public static void main(String[] args) {
        String url = "https://articulo.mercadolibre.com.mx/MLM-3470110454-zapatillas-unisex-de-diseno-clasico-aic-270-062270-b-_JM";
        String itemIdByProductUrl = MercadoHttpUtil.getItemIdByProductUrl(url);
        System.out.println(itemIdByProductUrl);


    }

    @Override
    public CpMercadoInfoDto getMercadoInfo(String mercadoInfo) {
        // 封装全局产品信息
        CpMercadoInfoDto cpMercadoInfoDto = new CpMercadoInfoDto();
        // 获取token和站点产品ID
        Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
        MercadoShopInfo tokenInfo = mercadoProductMapper.getMercadoToken(SecurityUtils.getLoginUser().getSysUser().getDeptId());
        String accessToken = MercadoSecureUtil.decryptMercado(tokenInfo.getId().toString(), tokenInfo.getAccessToken());
        String url = "https://api.mercadolibre.com/";
        // 获取站点产品id
        String itemId = MercadoHttpUtil.getItemIdByProductUrl(mercadoInfo);
        // 根据站点产品ID获取全局产品ID
        JSONObject siteItemInfo = MercadoHttpUtil.getGlobalAndSiteRelationships(itemId, accessToken);
        if (siteItemInfo == null || StringUtils.isEmpty(siteItemInfo.getString("item_id")) || StringUtils.isEmpty(siteItemInfo.getString("parent_id"))) {
            throw new ServiceException("采集地址有误");
        }
        // 获取全局产品id
        String cbtId = siteItemInfo.getString("parent_id");
        // 再根据全局产品id获取各个站点产品id
        JSONObject cbtInfo = MercadoHttpUtil.getGlobalAndSiteRelationships(cbtId, accessToken);
        // 获取站点的产品id
        JSONArray marketplace_items = cbtInfo.getJSONArray("marketplace_items");
        // 巴西站点产品id
        String mlb = null;
        // 墨西哥站点的产品id
        String mlm = null;
        // 智利站点的产品id
        String mlc = null;
        // 哥伦比亚站点的产品id
        String mco = null;
        for (Object marketplace_item : marketplace_items) {
            JSONObject item = JSONObject.from(marketplace_item);
            if ("MLB".equals(item.getString("site_id"))) {
                mlb = item.getString("item_id");
            } else if ("MLM".equals(item.getString("site_id"))) {
                mlm = item.getString("item_id");
            } else if ("MLC".equals(item.getString("site_id"))) {
                mlc = item.getString("item_id");
            } else if ("MCO".equals(item.getString("site_id"))) {
                mco = item.getString("item_id");
            }
        }
        // 查询全局产品的详情
        JSONObject productInfo = MercadoNewHttpUtil.getProductInfo(cbtId, accessToken);

        // 处理产品类别和性别ID
        String domain_id = productInfo.getString("domain_id");
        if (domain_id != null) {
            String[] split = domain_id.split("-");
            cpMercadoInfoDto.setDomainId(split[1]);
        }
        JSONArray attributesjson = productInfo.getJSONArray("attributes");
        if (attributesjson != null && attributesjson.size() > 0) {
            for (int index = 0; index < attributesjson.size(); index++) {
                JSONObject jsonObject = attributesjson.getJSONObject(index);
                String name = jsonObject.getString("name");
                if ("Gender".equals(name)) {

                    String value_id = jsonObject.getString("value_id");

                    if (value_id != null) {
                        cpMercadoInfoDto.setGenderId(value_id);

                        if (value_id.equals("339665")) {
                            cpMercadoInfoDto.setGenderName("Woman");
                        } else if (value_id.equals("339666")) {
                            cpMercadoInfoDto.setGenderName("Man");
                        } else if (value_id.equals("339668")) {
                            cpMercadoInfoDto.setGenderName("Girls");
                        } else if (value_id.equals("339667")) {
                            cpMercadoInfoDto.setGenderName("Boys");
                        } else if (value_id.equals("110461")) {
                            cpMercadoInfoDto.setGenderName("Gender_neutral");
                        } else if (value_id.equals("1915949")) {
                            cpMercadoInfoDto.setGenderName("Gender_neutral_KID");
                        }
                    }
                }
            }
        }

        if (productInfo == null || !"active".equals(productInfo.getString("status"))) {
            String categoryId = null;
            if (productInfo != null) {
                categoryId = productInfo.getString("category_id");
            }

            //说明产品已不是激活状态
            //throw new ServiceException("采集的产品是未激活状态,可能因侵权暂停了");
            //直接查询采集url
            String item_id = siteItemInfo.getString("item_id");
            productInfo = MercadoNewHttpUtil.getProductInfo(siteItemInfo.getString("item_id"), accessToken);


            if (productInfo == null || !"active".equals(productInfo.getString("status"))) {
                //说明产品已不是激活状态
                throw new ServiceException("采集的产品是未激活状态,可能因侵权暂停了");
            } else {
                //如果是采集的站点url则要重新设置全局cbt类目
                if (categoryId != null) {
                    productInfo.put("category_id", categoryId);
                }
                if (item_id.contains("MLM")) {
                    productInfo.put("price", productInfo.getBigDecimal("price").divide(new BigDecimal("18"), 2, BigDecimal.ROUND_HALF_DOWN));
                } else if (item_id.contains("MLB")) {
                    productInfo.put("price", productInfo.getBigDecimal("price").divide(new BigDecimal("5"), 2, BigDecimal.ROUND_HALF_DOWN));
                } else if (item_id.contains("MLC")) {
                    productInfo.put("price", productInfo.getBigDecimal("price").divide(new BigDecimal("800"), 2, BigDecimal.ROUND_HALF_DOWN));
                } else if (item_id.contains("MCO")) {
                    productInfo.put("price", productInfo.getBigDecimal("price").divide(new BigDecimal("4600"), 2, BigDecimal.ROUND_HALF_DOWN));
                }
            }
        }

        cpMercadoInfoDto.setId(cbtId);
        cpMercadoInfoDto.setCategoryId(productInfo.getString("category_id"));
        cpMercadoInfoDto.setEnglishTitle(productInfo.getString("title"));
        cpMercadoInfoDto.setPrice(productInfo.getBigDecimal("price"));
        //封装基础属性
        List<Map<String, Object>> infoList = new ArrayList<>();
        JSONArray attributes = productInfo.getJSONArray("attributes");
        for (Object attribute : attributes) {
            JSONObject attrJson = JSONObject.from(attribute);
            Map<String, Object> map = new HashMap<>();
            map.put("id", attrJson.getString("id"));
            map.put("name", attrJson.getString("name"));
            map.put("value", attrJson.getString("value_name"));
            infoList.add(map);
        }
        cpMercadoInfoDto.setInfoList(infoList);
        //封装变体颜色和尺寸和图片
        JSONArray variations = productInfo.getJSONArray("variations");
        //变体颜色
        List<String> colorTags = new ArrayList<>();
        String tab = "";
        //变体尺寸
        List<String> sizeTags = new ArrayList<>();
        String bor = "";
        //图片
        List<Map<String, Object>> colorImagesList = new ArrayList<>();
        for (Object variation : variations) {
            JSONObject variationJson = JSONObject.from(variation);
            JSONArray attribute_combinations = variationJson.getJSONArray("attribute_combinations");
            //变体中的可变属性要去除'FABRIC_DESIGN'
            List<Object> listNew = attribute_combinations.stream().filter(item -> {
                JSONObject itemJson = JSONObject.from(item);
                return !"FABRIC_DESIGN".equals(itemJson.getString("id")) && !"PRINT_DESIGN".equals(itemJson.getString("id"));
            }).collect(Collectors.toList());
            for (int i = 0; i < listNew.size(); i++) {
                JSONObject attribute_combinationJson = JSONObject.from(listNew.get(i));
                if (i == 0 && !colorTags.contains(attribute_combinationJson.getString("value_name"))) {
                    tab = attribute_combinationJson.getString("id");
                    colorTags.add(attribute_combinationJson.getString("value_name"));
                    Map<String, Object> map = new HashMap<>();
                    map.put("color", attribute_combinationJson.getString("value_name"));
                    map.put("listFileOld", variationJson.getList("picture_ids", String.class));
                    colorImagesList.add(map);
                }

                if (i == 1 && !sizeTags.contains(attribute_combinationJson.getString("value_name"))) {
                    bor = attribute_combinationJson.getString("id");
                    sizeTags.add(attribute_combinationJson.getString("value_name"));
                }
            }
        }

        //存放变体的第一个变量,一般是颜色等
        //if (tab != null){
        Map<String, Object> colorMap = new HashMap<>();
        colorMap.put("id", tab);
        colorMap.put("colorTags", colorTags);
        cpMercadoInfoDto.setColorMap(colorMap);
        //}

        //存放变体的第二个变量,可能没有或者是尺码
        //if (bor != null){
        Map<String, Object> sizeMap = new HashMap<>();
        sizeMap.put("id", bor);
        sizeMap.put("sizeTags", sizeTags);
        cpMercadoInfoDto.setSizeMap(sizeMap);
        //}

        //图片对象
        JSONArray pictures = productInfo.getJSONArray("pictures");
        for (Map<String, Object> map : colorImagesList) {
            List<String> imageIds = (List<String>) map.get("listFileOld");
            List<Map<String, String>> collect1 = imageIds.stream().map(item -> {
                Map<String, String> mapPic = new HashMap<>();
                for (Object pic : pictures) {
                    JSONObject picjson = JSONObject.from(pic);
                    if (picjson.getString("id").equals(item)) {
                        mapPic.put("id", item);
                        mapPic.put("url", picjson.getString("secure_url"));
                    }
                }
                return mapPic;
            }).collect(Collectors.toList());
            map.put("fileList", collect1);
            map.remove("listFileOld");
        }
        if (colorImagesList.size() == 0) {
            //说明没有变体,那么图片的color直接用空字符串
            Map<String, Object> map = new HashMap<>();
            map.put("color", "");
            map.put("fileList", pictures);
            colorImagesList.add(map);
        }
        cpMercadoInfoDto.setColorImagesList(colorImagesList);
        //标题和说明和站点价格
        this.addSiteProductInfo(cpMercadoInfoDto, mlm, url, accessToken, productInfo.getBigDecimal("price"), cbtId, deptId);
        this.addSiteProductInfo(cpMercadoInfoDto, mlb, url, accessToken, productInfo.getBigDecimal("price"), cbtId, deptId);
        this.addSiteProductInfo(cpMercadoInfoDto, mlc, url, accessToken, productInfo.getBigDecimal("price"), cbtId, deptId);
        this.addSiteProductInfo(cpMercadoInfoDto, mco, url, accessToken, productInfo.getBigDecimal("price"), cbtId, deptId);


        return cpMercadoInfoDto;
    }

    private void addSiteProductInfo(CpMercadoInfoDto cpMercadoInfoDto, String proId, String url, String token, BigDecimal price, String cbt, Long deptId) {
        if (StringUtils.isNotEmpty(proId)) {
            //添加标题和说明,价格
            JSONObject productInfo = MercadoNewHttpUtil.getProductInfo(proId, token);
            if (productInfo != null && ("active".equals(productInfo.getString("status")) || "paused".equals(productInfo.getString("status")))) {
                List<Map<String, Object>> sitesPrice = cpMercadoInfoDto.getSitesPrice();
                Map<String, Object> map = new HashMap<>();
                //汇率查询
                //汇率值
                //添加价格,只有墨西哥才计算汇率
                if ("MLM".equals(productInfo.getString("site_id"))) {
                    BigDecimal rate = this.getRateInfo("USD", productInfo.getString("currency_id"));
                    // 价格系数,从redis缓存中获取,如果缓存中没有则取默认值
                    String priceRatio = redisService.getJSONStringByKey("DEPT_PRICE_RATIO" + deptId);
                    if (priceRatio != null) {
                        map.put("price", productInfo.getBigDecimal("price").divide(rate, 2, BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(priceRatio)).setScale(2, RoundingMode.HALF_UP));
                    } else {
                        map.put("price", productInfo.getBigDecimal("price").divide(rate, 2, BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(increaseFactor)).setScale(2, RoundingMode.HALF_UP));
                    }
                } else {
                    map.put("price", price);
                }
                //添加标题
                if ("MLB".equals(productInfo.getString("site_id"))) {
                    cpMercadoInfoDto.setPortugueseTitle(productInfo.getString("title"));
                } else if (cpMercadoInfoDto.getSpanishTitle() == null) {
                    cpMercadoInfoDto.setSpanishTitle(productInfo.getString("title"));
                }
                map.put("id", productInfo.getString("site_id"));
                sitesPrice.add(map);

                //添加说明
                JSONObject des = MercadoHttpUtil.getDesByCbtId(token, url, cbt, productInfo.getString("site_id"), productInfo.getJSONObject("shipping").getString("logistic_type"));
                logger.info("采集到的描述信息 {}", des.toJSONString());
                if (des != null) {
                    if ("MLB".equals(productInfo.getString("site_id"))) {
                        cpMercadoInfoDto.setPortugueseMsg(des.getString("plain_text"));
                    } else if (cpMercadoInfoDto.getSpanishMsg() == null) {
                        cpMercadoInfoDto.setSpanishMsg(des.getString("plain_text"));
                    }

                }
            }
        }
    }

    //汇率查询
    public BigDecimal getRateInfo(String fromSate, String toSate) {
        Map<String, Object> map = new HashMap<>();
        map.put("from", fromSate);
        map.put("to", toSate);
        map.put("app_id", "iivlkttglgrzrozk");
        map.put("app_secret", "Z0RyRUk5dTY3d1Y1K0hzQk1nWUlsZz09");
        try {
            String usdExchangeRate = redisService.getCacheObject("updateUsdExchangeRate");
            return new BigDecimal(usdExchangeRate);
        } catch (Exception e) {
            logger.error("汇率查询异常", e);
            return new BigDecimal("17.2");
        }
    }

    @Override
    public Map<String, Object> uploadMercadoApi(MultipartFile file) {
        //获取上传美客多的url
        //获取token
        //获取token和url
        //Long userId = SecurityUtils.getUserId();
        Long userId = 102L;
        MercadoShopInfo tokenInfo = mercadoProductMapper.getMercadoToken(SecurityUtils.getLoginUser().getSysUser().getDeptId());
        String token = MercadoSecureUtil.decryptMercado(tokenInfo.getId().toString(), tokenInfo.getAccessToken());
        String mercadoUrl = "https://api.mercadolibre.com/";
        mercadoUrl = mercadoUrl + "pictures/items/upload";
        File fileNew = new File(file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream()) {
            FileUtil.writeFromStream(inputStream, fileNew);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject json = null;
        try {
            json = MercadoHttpUtil.uploadImage(token, mercadoUrl, fileNew);
        } catch (Exception e) {
            logger.error("上传文件失败:", e);
        } finally {
            fileNew.delete();
        }
        logger.info("上传图片返回:{}", json);
        if (json == null || json.getString("status") != null && json.getString("status").equals("400")) {
            throw new ServiceException("图片至少有一边在500~1200范围之间");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", json.getString("id"));
        JSONArray variations = json.getJSONArray("variations");
        String urlIamge = "";
        for (Object variation : variations) {
            JSONObject jsonb = JSONObject.from(variation);
            if (jsonb.getString("secure_url").contains("-O.jpg")) {
                urlIamge = jsonb.getString("secure_url");
            }
        }
        map.put("url", urlIamge);
        return map;
    }

    @Override
    public List<Map<String, Object>> uploadImgByUrl(Map<String, String> urls) {
        //获取上传美客多的url
        //获取token
        //获取token和url
        Long userId = SecurityUtils.getUserId();
        //Long userId = 102L;
        MercadoShopInfo tokenInfo = mercadoProductMapper.getMercadoToken(SecurityUtils.getLoginUser().getSysUser().getDeptId());
        String token = MercadoSecureUtil.decryptMercado(tokenInfo.getId().toString(), tokenInfo.getAccessToken());
        String mercadoUrl = "https://api.mercadolibre.com/";
        mercadoUrl = mercadoUrl + "pictures/items/upload";
        String urlsString = urls.get("urls");
        List<Map<String, Object>> list = new ArrayList<>();
        if (urlsString.contains("\n")) {
            String[] split = urlsString.split("\n");
            for (String url : split) {
                String urlNew = url.trim();
                if (!StringUtils.isEmpty(urlNew)) {
                    MercadoHttpUtil.urlHandleImages(urlNew, list, token, mercadoUrl);
                }
            }
        } else {
            urlsString = urlsString.trim();
            MercadoHttpUtil.urlHandleImages(urlsString, list, token, mercadoUrl);
        }
        return list;
    }

    @Override
    public AjaxResult releaseProduct(MercadoReleaseProductDTO mercadoReleaseProductDTO) {
        // 发布全局产品
        if (mercadoReleaseProductDTO.getReleaseType().equals("cbt") && mercadoReleaseProductDTO.getProductId() != null) {
            logger.info("开始发布全局产品，参数为 {}", JSON.toJSONString(mercadoReleaseProductDTO));
            // 将产品发布状态更改为发布中
            String[] ids = new String[1];
            ids[0] = String.valueOf(mercadoReleaseProductDTO.getProductId());
            Integer result = updateMercadoProductStatus(ids);
            if (result > 0) {
                amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                        RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_PRODUCT_INFO, mercadoReleaseProductDTO.getProductId());
            }
        }
        return AjaxResult.success("发布中，请稍后查询发布结果");
    }

    @Override
    public AjaxResult getSizeChart(MercadoSizeChartDTO inputDto) {
        List<MercadoSizeChartDetail> result = new ArrayList<>();
        // 获取尺码表信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("domain_id", inputDto.getDomainId());
        jsonObject.put("site_id", "CBT");
        jsonObject.put("type", "SPECIFIC");
        jsonObject.put("seller_id", inputDto.getMerchantId());

        JSONArray attributes = new JSONArray();
        JSONObject attribute = new JSONObject();
        attribute.put("id", "GENDER");

        JSONArray values = new JSONArray();
        JSONObject value = new JSONObject();
        value.put("id", inputDto.getGenderId());
        values.add(value);
        attribute.put("values", values);
        attributes.add(attribute);
        jsonObject.put("attributes", attributes);
        MercadoShopInfo mercadoShopInfo = mercadoProductMapper.getAccessTokenByMerchantIds(inputDto.getMerchantId());
        String accessToken = MercadoSecureUtil.decryptMercado(mercadoShopInfo.getId().toString(), mercadoShopInfo.getAccessToken());
        JSONObject sizeChart = MercadoHttpUtil.getSizeChart(jsonObject, accessToken);
        logger.error("从后端获取的美客多尺码表信息 {}", sizeChart.toJSONString());

        if (sizeChart != null) {
            JSONArray charts = sizeChart.getJSONArray("charts");
            if (charts != null && charts.size() > 0) {
                for (int index = 0; index < charts.size(); index++) {
                    JSONObject object = charts.getJSONObject(index);
                    MercadoSizeChartDetail mercadoSizeChartDetail = new MercadoSizeChartDetail();
                    String id = object.getString("id");
                    String name = object.getJSONArray("names").getJSONObject(0).getString("CBT");
                    String mainAttributeId = object.getString("main_attribute_id");

                    JSONArray rows = object.getJSONArray("rows");
                    List<MercadoSizeChart> rowsList = new ArrayList<>();
                    for (int j = 0; j < rows.size(); j++) {
                        JSONObject row = rows.getJSONObject(j);
                        MercadoSizeChart mercadoSizeChart = new MercadoSizeChart();
                        String rowId = row.getString("id");
                        mercadoSizeChart.setId(rowId);
                        JSONArray attributesArray = row.getJSONArray("attributes");
                        for (int k = 0; k < attributesArray.size(); k++) {
                            JSONObject attributesArrayJSONObject = attributesArray.getJSONObject(k);
                            String id1 = attributesArrayJSONObject.getString("id");
                            if (id1.equals(mainAttributeId)) {
                                String string = attributesArrayJSONObject.getJSONArray("values").getJSONObject(0).getString("name");
                                mercadoSizeChart.setName(string);
                            }
                        }
                        rowsList.add(mercadoSizeChart);
                    }
                    mercadoSizeChartDetail.setId(id);
                    mercadoSizeChartDetail.setName(name);
                    mercadoSizeChartDetail.setMainAttributeId(mainAttributeId);
                    mercadoSizeChartDetail.setRows(rowsList);
                    result.add(mercadoSizeChartDetail);
                }
            }
        }
        return AjaxResult.success(processMercadoSizeChart(result));
    }

    @Override
    public Integer releaseGlobalProduct(String productId) {
        amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_PRODUCT_INFO, productId);
        return 1;
    }

    @Override
    public AjaxResult getErrorLog(Long id) {
        JSONObject result = new JSONObject();
        result.put("message", "No error log found yet");
        // 先根据产品id查询
        MercadoProduct mercadoProduct = mercadoProductMapper.selectMercadoProductById(id);
        if (mercadoProduct != null) {
            MercadoProductErrorLog mercadoProductErrorLog = new MercadoProductErrorLog();
            mercadoProductErrorLog.setProductId(id);
            List<MercadoProductErrorLog> mercadoProductErrorLogs = mercadoProductErrorLogMapper.selectMercadoProductErrorLogList(mercadoProductErrorLog);
            if (!mercadoProductErrorLogs.isEmpty()) {
                result = JSONObject.parse(mercadoProductErrorLogs.get(0).getErrorInfo());
            }
        }
        MercadoProductItem mercadoProductItem = mercadoProductItemMapper.selectMercadoProductItemById(id);
        if (mercadoProductItem != null) {
            MercadoProductErrorLog mercadoProductErrorLog = new MercadoProductErrorLog();
            mercadoProductErrorLog.setProductItemId(id);
            List<MercadoProductErrorLog> mercadoProductErrorLogs = mercadoProductErrorLogMapper.selectMercadoProductErrorLogList(mercadoProductErrorLog);
            if (!mercadoProductErrorLogs.isEmpty()) {
                JSONArray jsonArray = JSONArray.parseArray(mercadoProductErrorLogs.get(0).getErrorInfo());
                return AjaxResult.success(jsonArray);
            }
        }
        return AjaxResult.success(result);
    }

    @Override
    public AjaxResult batchCopyToShop(BatchCopyLinkToShop batchCopyLinkToShop) {
        List<String> productIds = batchCopyLinkToShop.getProductIds();
        if (productIds != null && !productIds.isEmpty()) {
            LoginUser currentUser = SecurityUtils.getLoginUser();
            List<MercadoUpcItem> mercadoUpcItems = mercadoBatchCollectionLinkMapper.getUpcCodeByDept(currentUser.getSysUser().getDeptId(), productIds.size());
            if (mercadoUpcItems.isEmpty()) {
                return AjaxResult.error("无可用的UPC编码!");
            }
            if (mercadoUpcItems.size() < productIds.size()) {
                return AjaxResult.error("UPC编码数量不够!此次采集需要" + productIds.size() + "个UPC编码,实际只有" + mercadoUpcItems.size() + "个!");
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
            // 处理复制的数据
            for (int index = 0; index < productIds.size(); index++) {
                String productId = productIds.get(index);
                BatchCopyLinkToShop batch = new BatchCopyLinkToShop();
                batch.setProductId(productId);
                batch.setShopId(batchCopyLinkToShop.getShopId());
                batch.setUpcCode(mercadoUpcItems.get(index).getUpcCode());
                amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                        RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_BATCH_COPY_LINK_TO_SHOP, batch);
            }
        }
        return AjaxResult.success("批量复制成功!");
    }

    @Override
    @Transactional
    public Integer updateMercadoProductStatus(String[] productIds) {
        return mercadoProductMapper.updateMercadoProductStatus(productIds);
    }

    /**
     * 处理重复尺码表的问题
     *
     * @param mercadoSizeChartDetailList
     * @return
     */
    private List<MercadoSizeChartDetail> processMercadoSizeChart(List<MercadoSizeChartDetail> mercadoSizeChartDetailList) {
        if (!mercadoSizeChartDetailList.isEmpty()) {
            for (MercadoSizeChartDetail mercadoSizeChartDetail : mercadoSizeChartDetailList) {
                List<MercadoSizeChart> rows = mercadoSizeChartDetail.getRows();
                List<MercadoSizeChart> newRows = new ArrayList<>();
                if (!rows.isEmpty()) {
                    Map<String, List<MercadoSizeChart>> collect = rows.stream().collect(Collectors.groupingBy(MercadoSizeChart::getName));
                    collect.forEach((key, value) -> {
                        // 处理排序问题
                        for (MercadoSizeChart mercadoSizeChart : value) {
                            String id = mercadoSizeChart.getId();
                            String[] split = id.split(":");
                            mercadoSizeChart.setOrderSeq(Integer.parseInt(split[1]));
                        }
                        // 排序，获取第一个元素
                        List<MercadoSizeChart> rowList = value.stream().sorted(Comparator.comparing(MercadoSizeChart::getOrderSeq)).collect(Collectors.toList());
                        MercadoSizeChart mercadoSizeChart = rowList.get(0);
                        newRows.add(mercadoSizeChart);
                    });
                }
                // newRows根据id排序
                List<MercadoSizeChart> collect = newRows.stream().sorted(Comparator.comparing(MercadoSizeChart::getOrderSeq)).collect(Collectors.toList());
                mercadoSizeChartDetail.setRows(collect);
            }
        }
        return mercadoSizeChartDetailList;
    }

}
