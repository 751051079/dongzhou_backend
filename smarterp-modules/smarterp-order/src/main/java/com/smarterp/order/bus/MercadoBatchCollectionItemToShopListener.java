package com.smarterp.order.bus;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.core.utils.mercado.MercadoNewHttpUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.*;
import com.smarterp.order.domain.dto.BatchCollectionLinkToShop;
import com.smarterp.order.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class MercadoBatchCollectionItemToShopListener {

    private static final Logger logger = LoggerFactory.getLogger(MercadoBatchCollectionItemToShopListener.class);

    public static final String MERCADO_URL = "https://api.mercadolibre.com/";

    @Resource
    private MercadoProductMapper mercadoProductMapper;

    @Resource
    private MercadoProductItemMapper mercadoProductItemMapper;

    @Resource
    private MercadoProductAttributesMapper mercadoProductAttributesMapper;

    @Resource
    private MercadoProductCombinationMapper mercadoProductCombinationMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private MercadoShopItemMapper mercadoShopItemMapper;

    /**
     * 批量采集链接到店铺
     *
     * @param batchCollectionLinkToShop
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.CLOUD_PULL_MERCADO_BATCH_COLLECTION_ITEM_TO_SHOP_QUEUE),
            key = RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_BATCH_COLLECTION_ITEM_TO_SHOP))
    public void batchCollectionLink(BatchCollectionLinkToShop batchCollectionLinkToShop) {
        try {
            // 根据linkId获取店铺accessToken
            MercadoShopItem mercadoShopItem = mercadoShopItemMapper.selectMercadoShopItemById(batchCollectionLinkToShop.getLinkId());
            if (mercadoShopItem != null) {
                logger.info("消费的信息 {}", JSON.toJSONString(batchCollectionLinkToShop));
                // 从缓存中获取accessToken
                String accessToken = redisService.getCacheObject("ACCESS_TOKEN_SHOP:" + mercadoShopItem.getSellerId());
                logger.info("获取到的accessToken {}", accessToken);
                if (accessToken != null) {
                    // 根据MLM获取全局产品id
                    JSONObject parentJson = MercadoHttpUtil.getGlobalAndSiteRelationships(batchCollectionLinkToShop.getLinkId(), accessToken);
                    logger.info("获取到的父节点信息 {}", JSON.toJSONString(parentJson));
                    if (parentJson != null) {
                        // 全局产品id
                        String cbtId = parentJson.getString("parent_id");
                        logger.info("全局产品id {}", cbtId);
                        // 站点产品id
                        String itemId = parentJson.getString("item_id");
                        logger.info("站点产品id {}", itemId);
                        if (cbtId != null && itemId != null) {
                            // 获取全局产品信息
                            JSONObject productInfo = MercadoNewHttpUtil.getGlobalProductInfo(cbtId, accessToken);
                            // 获取站点产品信息
                            JSONObject itemInfo = MercadoNewHttpUtil.getSiteProductInfo(itemId, accessToken);
                            if (productInfo != null && itemInfo != null) {
                                MercadoProduct mercadoProduct = new MercadoProduct();
                                mercadoProduct.setId(IdUtil.getSnowflake(1, 1).nextId());
                                mercadoProduct.setUserId(batchCollectionLinkToShop.getUserId());
                                mercadoProduct.setDeptId(batchCollectionLinkToShop.getDeptId());
                                mercadoProduct.setMerchantId(batchCollectionLinkToShop.getShopId());
                                mercadoProduct.setMercadoProductUrl(itemInfo.getString("permalink"));
                                // 全局产品标题
                                String title = productInfo.getString("title");
                                if (title.length() > 60) {
                                    title = title.substring(0, 60);
                                }
                                mercadoProduct.setProductTitle(title);
                                mercadoProduct.setProductDescription("全局产品简述");
                                mercadoProduct.setSalePrice(productInfo.getBigDecimal("price"));
                                mercadoProduct.setCbtItemId(cbtId);
                                // TODO sku编码默认使用店铺名称拼接编号，每个店铺编号从1开始
                                mercadoProduct.setSkuPre(batchCollectionLinkToShop.getPreSku());
                                mercadoProduct.setCbtCategory(productInfo.getString("category_id"));
                                mercadoProduct.setUpcCode(batchCollectionLinkToShop.getUpcCode());
                                mercadoProduct.setShipType(2L);
                                mercadoProduct.setReleaseStatus("NoRelease");
                                JSONArray pictures = productInfo.getJSONArray("pictures");
                                if (pictures != null && pictures.size() > 0) {
                                    JSONArray jsonArray = new JSONArray();
                                    for (int index = 0; index < pictures.size(); index++) {
                                        JSONObject jsonObject = pictures.getJSONObject(index);
                                        JSONObject object = new JSONObject();
                                        object.put("id", jsonObject.getString("id"));
                                        object.put("url", jsonObject.getString("url"));
                                        jsonArray.add(object);
                                    }
                                    mercadoProduct.setPictures(JSON.toJSONString(jsonArray));
                                }
                                mercadoProduct.setAvailableQuantity(999);
                                mercadoProduct.setCreateTime(new Date());
                                mercadoProduct.setCreateBy(String.valueOf(batchCollectionLinkToShop.getUserId()));
                                mercadoProduct.setUpdateTime(new Date());
                                mercadoProduct.setUpdateBy(String.valueOf(batchCollectionLinkToShop.getUserId()));
                                mercadoProduct.setDeleted(false);
                                // 处理domainId
                                String domain_id = itemInfo.getString("domain_id");
                                if (domain_id != null) {
                                    String[] split = domain_id.split("-");
                                    mercadoProduct.setDomainId(split[1]);
                                }
                                // 处理genderId
                                String genderId = getGenderId(itemInfo);
                                mercadoProduct.setGenderId(genderId);
                                logger.info("插入产品信息表 {}", JSON.toJSONString(mercadoProduct));

                                // 处理重量和尺寸信息
                                JSONArray variations = itemInfo.getJSONArray("variations");
                                JSONArray attributesProductInfo = itemInfo.getJSONArray("attributes");
                                JSONArray shipInfo = new JSONArray();
                                if (variations != null && variations.size() > 0) {
                                    JSONObject jsonObject = variations.getJSONObject(0);
                                    JSONArray attributes = jsonObject.getJSONArray("attributes");
                                    if (attributes != null) {
                                        for (int index = 0; index < attributes.size(); index++) {
                                            JSONObject attribute = attributes.getJSONObject(index);
                                            if ("PACKAGE_HEIGHT".equals(attribute.getString("id"))
                                                    || "PACKAGE_WIDTH".equals(attribute.getString("id"))
                                                    || "PACKAGE_LENGTH".equals(attribute.getString("id"))
                                                    || "PACKAGE_WEIGHT".equals(attribute.getString("id"))) {
                                                JSONObject json = new JSONObject();
                                                json.put("id", attribute.getString("id"));
                                                json.put("value_name", attribute.getString("value_name"));
                                                shipInfo.add(json);
                                            }
                                        }
                                    }
                                } else {
                                    for (int index = 0; index < attributesProductInfo.size(); index++) {
                                        JSONObject jsonObject = attributesProductInfo.getJSONObject(index);
                                        if ("PACKAGE_HEIGHT".equals(jsonObject.getString("id"))
                                                || "PACKAGE_WIDTH".equals(jsonObject.getString("id"))
                                                || "PACKAGE_LENGTH".equals(jsonObject.getString("id"))
                                                || "PACKAGE_WEIGHT".equals(jsonObject.getString("id"))) {
                                            JSONObject json = new JSONObject();
                                            json.put("id", jsonObject.getString("id"));
                                            json.put("value_name", jsonObject.getString("value_name"));
                                            shipInfo.add(json);
                                        }
                                    }
                                }
                                mercadoProduct.setShipInfo(shipInfo.toJSONString());

                                Integer result = mercadoProductMapper.insertMercadoProduct(mercadoProduct);
                                if (result > 0) {
                                    // 处理站点信息
                                    MercadoProductItem mercadoProductItem = new MercadoProductItem();
                                    mercadoProductItem.setId(IdUtil.getSnowflake(1, 1).nextId());
                                    mercadoProductItem.setProductId(mercadoProduct.getId());
                                    mercadoProductItem.setSiteId("MLM");
                                    mercadoProductItem.setLogisticType("remote");
                                    String priceRatio = redisService.getJSONStringByKey("DEPT_PRICE_RATIO" + batchCollectionLinkToShop.getDeptId());
                                    if (priceRatio != null) {
                                        BigDecimal sitePrice = itemInfo.getBigDecimal("price").multiply(new BigDecimal(priceRatio)).setScale(2, RoundingMode.HALF_UP);
                                        mercadoProductItem.setSiteSalePrice(sitePrice);
                                    } else {
                                        BigDecimal sitePrice = itemInfo.getBigDecimal("price").multiply(new BigDecimal("1.00")).setScale(2, RoundingMode.HALF_UP);
                                        mercadoProductItem.setSiteSalePrice(sitePrice);
                                    }
                                    // 获取站点描述
                                    JSONObject des = MercadoHttpUtil.getDesByCbtId(accessToken, MERCADO_URL, cbtId, itemInfo.getString("site_id"), itemInfo.getJSONObject("shipping").getString("logistic_type"));
                                    if (des != null) {
                                        mercadoProductItem.setSiteProductDescription(des.getString("plain_text"));
                                    }
                                    mercadoProductItem.setSiteProductTitle(itemInfo.getString("title"));
                                    mercadoProductItem.setSiteCategory(mercadoProduct.getCbtCategory());
                                    mercadoProductItem.setSiteReleaseStatus("NoRelease");
                                    mercadoProductItem.setSiteDescriptionStatus("NoRelease");
                                    mercadoProductItem.setMerchantId(mercadoProduct.getMerchantId());
                                    mercadoProductItem.setCreateTime(new Date());
                                    mercadoProductItem.setCreateBy(mercadoProduct.getCreateBy());
                                    mercadoProductItem.setUpdateTime(new Date());
                                    mercadoProductItem.setUpdateBy(mercadoProduct.getUpdateBy());
                                    mercadoProductItem.setDeleted(false);
                                    logger.info("插入的站点信息 {}", JSON.toJSONString(mercadoProductItem));
                                    mercadoProductItemMapper.insertMercadoProductItem(mercadoProductItem);

                                    // 处理属性信息
                                    JSONArray attributes = productInfo.getJSONArray("attributes");
                                    if (attributes != null && attributes.size() > 0) {
                                        List<MercadoProductAttributes> inserAttributesList = new ArrayList<>();
                                        for (int index = 0; index < attributes.size(); index++) {
                                            JSONObject jsonObject = attributes.getJSONObject(index);
                                            String id = jsonObject.getString("id");
                                            if (!"SIZE_GRID_ID".equals(id)) {
                                                MercadoProductAttributes mercadoProductAttributes = new MercadoProductAttributes();
                                                mercadoProductAttributes.setId(IdUtil.getSnowflake(1, 1).nextId());
                                                mercadoProductAttributes.setProductId(mercadoProduct.getId());
                                                mercadoProductAttributes.setMerchantId(mercadoProduct.getMerchantId());
                                                mercadoProductAttributes.setAttributeId(id);
                                                mercadoProductAttributes.setAttributeName(jsonObject.getString("name"));
                                                mercadoProductAttributes.setAttributeValueName(jsonObject.getString("value_name"));
                                                // attribute表示属性
                                                mercadoProductAttributes.setAttributeType("attribute");
                                                mercadoProductAttributes.setCreateTime(new Date());
                                                mercadoProductAttributes.setCreateBy(mercadoProduct.getCreateBy());
                                                mercadoProductAttributes.setUpdateTime(new Date());
                                                mercadoProductAttributes.setUpdateBy(mercadoProduct.getUpdateBy());
                                                mercadoProductAttributes.setDeleted(false);
                                                inserAttributesList.add(mercadoProductAttributes);
                                            }
                                        }
                                        logger.info("插入的属性信息 {}", JSON.toJSONString(inserAttributesList));
                                        mercadoProductAttributesMapper.batchMercadoProductAttributes(inserAttributesList);
                                    }

                                    // 处理变体信息
                                    List<MercadoProductCombination> insertCombinationList = progressVariations(productInfo, mercadoProduct);
                                    if (!insertCombinationList.isEmpty()) {
                                        mercadoProductCombinationMapper.batchMercadoProductCombination(insertCombinationList);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("采集链接到店铺异常 {}", e.getMessage());
        }
    }

    /**
     * 获取性别信息
     *
     * @param itemInfo
     * @return
     */
    private String getGenderId(JSONObject itemInfo) {
        JSONArray attributes = itemInfo.getJSONArray("attributes");
        if (attributes != null && attributes.size() > 0) {
            for (int index = 0; index < attributes.size(); index++) {
                JSONObject jsonObject = attributes.getJSONObject(index);
                String id = jsonObject.getString("id");
                if ("GENDER".equals(id)) {
                    return jsonObject.getString("value_id");
                }
            }
        }
        return null;
    }

    /**
     * 处理变体信息
     */
    private List<MercadoProductCombination> progressVariations(JSONObject productInfo, MercadoProduct mercadoProduct) {
        List<MercadoProductCombination> insertCombinationList = new ArrayList<>();
        try {
            // 变体仅有颜色
            Boolean onlyColor = false;
            // 变体仅有尺码
            Boolean onlySize = false;
            // 既有颜色又有尺码
            Boolean sizeAndColor = false;
            JSONObject json = progressVariations(productInfo);
            if (json != null) {
                JSONArray colorJsonArray = json.getJSONObject("colorMap").getJSONArray("colorTags");
                if (colorJsonArray != null && colorJsonArray.size() > 0) {
                    // 表示有颜色
                    onlyColor = true;
                }
                JSONArray sizeJsonArray = json.getJSONObject("sizeMap").getJSONArray("sizeTags");
                if (sizeJsonArray != null && sizeJsonArray.size() > 0) {
                    // 表示有尺码
                    onlySize = true;
                }
                // 表示既有颜色又有尺码
                if (onlyColor && onlySize) {
                    sizeAndColor = true;
                    onlyColor = false;
                    onlySize = false;
                }
                List<MercadoProductCombination> result = new ArrayList<>();
                JSONArray colorImagesList = json.getJSONArray("colorImagesList");
                // key表示颜色，value表示图片
                Map<String, JSONObject> colorMap = new HashMap<>();
                if (colorImagesList != null && colorImagesList.size() > 0) {
                    for (int index = 0; index < colorImagesList.size(); index++) {
                        JSONObject jsonObject = colorImagesList.getJSONObject(index);
                        colorMap.put(jsonObject.getString("color"), jsonObject);
                    }
                }
                if (sizeAndColor) {
                    // 既有颜色又有尺码
                    for (int i = 0; i < colorJsonArray.size(); i++) {
                        for (int j = 0; j < sizeJsonArray.size(); j++) {
                            String combinationSku = mercadoProduct.getSkuPre() + "_" + colorJsonArray.getString(i) + "_" + sizeJsonArray.getString(j);
                            MercadoProductCombination item = new MercadoProductCombination();
                            item.setCombinationSku(combinationSku);
                            JSONObject jsonObject = colorMap.get(colorJsonArray.getString(i));
                            if (jsonObject != null) {
                                JSONArray fileList = jsonObject.getJSONArray("fileList");
                                List<String> Pictures = new ArrayList<>();
                                for (int k = 0; k < fileList.size(); k++) {
                                    JSONObject object = fileList.getJSONObject(k);
                                    Pictures.add(object.getString("id"));
                                }
                                item.setPictures(JSON.toJSONString(Pictures));
                            }
                            JSONArray jsonArray = new JSONArray();
                            JSONObject jsonValue1 = new JSONObject();
                            jsonValue1.put("id", json.getJSONObject("colorMap").getString("id"));
                            jsonValue1.put("valueName", colorJsonArray.getString(i));
                            jsonArray.add(jsonValue1);

                            JSONObject jsonValue2 = new JSONObject();
                            jsonValue2.put("id", json.getJSONObject("sizeMap").getString("id"));
                            jsonValue2.put("valueName", sizeJsonArray.getString(j));
                            jsonArray.add(jsonValue2);
                            item.setCombinationJson(JSON.toJSONString(jsonArray));
                            result.add(item);
                        }
                    }
                }
                // 只有颜色
                if (onlyColor) {
                    for (int i = 0; i < colorJsonArray.size(); i++) {
                        String combinationSku = mercadoProduct.getSkuPre() + "_" + colorJsonArray.getString(i);
                        MercadoProductCombination item = new MercadoProductCombination();
                        item.setCombinationSku(combinationSku);
                        JSONObject jsonObject = colorMap.get(colorJsonArray.getString(i));
                        if (jsonObject != null) {
                            JSONArray fileList = jsonObject.getJSONArray("fileList");
                            List<String> Pictures = new ArrayList<>();
                            for (int k = 0; k < fileList.size(); k++) {
                                JSONObject object = fileList.getJSONObject(k);
                                Pictures.add(object.getString("id"));
                            }
                            item.setPictures(JSON.toJSONString(Pictures));
                        }
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonValue1 = new JSONObject();
                        jsonValue1.put("id", json.getJSONObject("colorMap").getString("id"));
                        jsonValue1.put("valueName", colorJsonArray.getString(i));
                        jsonArray.add(jsonValue1);
                        item.setCombinationJson(JSON.toJSONString(jsonArray));
                        result.add(item);
                    }
                }
                // 只有尺码
                if (onlySize) {
                    for (int i = 0; i < sizeJsonArray.size(); i++) {
                        String combinationSku = mercadoProduct.getSkuPre() + "_" + sizeJsonArray.getString(i);
                        MercadoProductCombination item = new MercadoProductCombination();
                        item.setCombinationSku(combinationSku);
                        JSONObject jsonObject = colorMap.get(colorJsonArray.getString(i));
                        if (jsonObject != null) {
                            JSONArray fileList = jsonObject.getJSONArray("fileList");
                            List<String> Pictures = new ArrayList<>();
                            for (int k = 0; k < fileList.size(); k++) {
                                JSONObject object = fileList.getJSONObject(k);
                                Pictures.add(object.getString("id"));
                            }
                            item.setPictures(JSON.toJSONString(Pictures));
                        }
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonValue2 = new JSONObject();
                        jsonValue2.put("id", json.getJSONObject("sizeMap").getString("id"));
                        jsonValue2.put("valueName", sizeJsonArray.getString(i));
                        jsonArray.add(jsonValue2);
                        item.setCombinationJson(JSON.toJSONString(jsonArray));
                        result.add(item);
                    }
                }
                // 插入变体数据
                if (!result.isEmpty()) {
                    for (int index = 0; index < result.size(); index++) {
                        MercadoProductCombination combination = result.get(index);
                        MercadoProductCombination mercadoProductCombination = new MercadoProductCombination();
                        mercadoProductCombination.setId(IdUtil.getSnowflake(1, 1).nextId());
                        mercadoProductCombination.setProductId(mercadoProduct.getId());
                        mercadoProductCombination.setCombinationSku(combination.getCombinationSku());
                        mercadoProductCombination.setPictures(combination.getPictures());
                        mercadoProductCombination.setCombinationJson(combination.getCombinationJson());
                        mercadoProductCombination.setAvailableQuantity(99);
                        mercadoProductCombination.setCombinationSort(index);
                        mercadoProductCombination.setCreateTime(new Date());
                        mercadoProductCombination.setCreateBy(mercadoProduct.getCreateBy());
                        mercadoProductCombination.setUpdateTime(new Date());
                        mercadoProductCombination.setUpdateBy(mercadoProduct.getUpdateBy());
                        mercadoProductCombination.setDeleted(false);
                        insertCombinationList.add(mercadoProductCombination);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("处理变体信息异常 {}", e);
        }
        return insertCombinationList;
    }

    /**
     * 处理变体信息
     *
     * @param productInfo
     */
    private static JSONObject progressVariations(JSONObject productInfo) {
        // 方法返回对象
        JSONObject jsonObject = new JSONObject();
        // 处理变体信息
        JSONArray variations = productInfo.getJSONArray("variations");
        if (variations != null && variations.size() > 0) {
            // 变体颜色
            List<String> colorTags = new ArrayList<>();
            String tab = "";
            // 变体尺寸
            List<String> sizeTags = new ArrayList<>();
            String bor = "";
            // 图片信息
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

            // 存放变体的第一个变量,一般是颜色等
            Map<String, Object> colorMap = new HashMap<>();
            colorMap.put("id", tab);
            colorMap.put("colorTags", colorTags);
            jsonObject.put("colorMap", colorMap);

            // 存放变体的第二个变量,可能没有或者是尺码
            Map<String, Object> sizeMap = new HashMap<>();
            sizeMap.put("id", bor);
            sizeMap.put("sizeTags", sizeTags);
            jsonObject.put("sizeMap", sizeMap);

            // 图片对象
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
                // 说明没有变体,那么图片的color直接用空字符串
                Map<String, Object> map = new HashMap<>();
                map.put("color", "");
                map.put("fileList", pictures);
                colorImagesList.add(map);
            }
            jsonObject.put("colorImagesList", colorImagesList);
        }
        return jsonObject;
    }

}
