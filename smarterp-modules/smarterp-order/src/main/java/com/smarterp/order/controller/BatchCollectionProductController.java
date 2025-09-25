package com.smarterp.order.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.StringUtils;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.core.utils.mercado.MercadoNewHttpUtil;
import com.smarterp.common.core.utils.mercado.MercadoSecureUtil;
import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.common.security.utils.SecurityUtils;
import com.smarterp.order.domain.dto.*;
import com.smarterp.order.domain.dto.batch.BatchCollectionProductInfo;
import com.smarterp.order.domain.dto.batch.ProductInfo;
import com.smarterp.order.domain.dto.batch.SitesPrice;
import com.smarterp.order.mapper.MercadoProductMapper;
import com.smarterp.order.service.IMercadoProductItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/batch-collection")
public class BatchCollectionProductController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BatchCollectionProductController.class);

    @Resource
    private MercadoProductMapper mercadoProductMapper;

    @Resource
    private IMercadoProductItemService mercadoProductItemService;

    @Resource
    private RedisService redisService;

    // 价格系数，默认是1.15
    @Value("${mercado.price.increaseFactor}")
    private String increaseFactor;

    /**
     * 批量采集产品(目前只支持批量采集墨西哥自发货站点以及上架墨西哥自发货站点的功能)
     *
     * @param batchCollectionDTO
     * @return
     */
    @PostMapping("/product")
    public AjaxResult batchCollectionProduct(@RequestBody BatchCollectionDTO batchCollectionDTO) {
        BatchCollectionProductInfo result = new BatchCollectionProductInfo();
        if (batchCollectionDTO.getItemId() != null) {
            MercadoShopInfo tokenInfo = mercadoProductMapper.getMercadoToken(SecurityUtils.getLoginUser().getSysUser().getDeptId());
            String token = MercadoSecureUtil.decryptMercado(tokenInfo.getId().toString(), tokenInfo.getAccessToken());
            // 获取全局产品id
            String url = "https://api.mercadolibre.com/";
            JSONObject GlobalProductInfo = MercadoHttpUtil.getGlobalAndSiteRelationships(batchCollectionDTO.getItemId(), token);
            if (GlobalProductInfo != null) {
                if (StringUtils.isNotEmpty(GlobalProductInfo.getString("item_id")) && StringUtils.isNotEmpty(GlobalProductInfo.getString("parent_id"))) {
                    // 全局产品id
                    String cbtId = GlobalProductInfo.getString("parent_id");
                    result.setCpProdutId(cbtId);
                    // 查询全局产品的详情
                    JSONObject productInfo = MercadoHttpUtil.getGlobalProductInfo(cbtId, token);
                    // 全局信息不为空并且是激活状态
                    if (productInfo != null && "active".equals(productInfo.getString("status"))) {
                        // 全局产品价格
                        // TODO 此处获取的价格为墨西哥比索，注意需要替换为美元
                        BigDecimal price = productInfo.getBigDecimal("price");
                        result.setPrice(price);
                        // 类目id
                        String categoryId = productInfo.getString("category_id");
                        result.setCategoryId(categoryId);
                        // 采集链接url
                        String mercadoUrl = batchCollectionDTO.getMercadoUrl();
                        result.setMercadoUrl(mercadoUrl);
                        // 采集店铺信息
                        List<String> shopIds = batchCollectionDTO.getShopIds();
                        result.setShopIds(shopIds);
                        // 采集站点信息
                        List<String> siteIds = new ArrayList<>();
                        siteIds.add("MLM");
                        result.setSiteIds(siteIds);
                        // 快递档位
                        result.setGears("2");
                        // 站点价格，标题和描述
                        JSONObject itemInfo = MercadoNewHttpUtil.getProductInfo(batchCollectionDTO.getItemId(), token);
                        if (itemInfo != null) {
                            if ("active".equals(itemInfo.getString("status")) || "paused".equals(itemInfo.getString("status"))) {
                                // 查询当天的汇率
                                String usdExchangeRate = redisService.getCacheObject("updateUsdExchangeRate");
                                // 汇率
                                BigDecimal rate = new BigDecimal(usdExchangeRate);
                                SitesPrice sitesPrice = new SitesPrice();
                                // 价格系数,从redis缓存中获取,如果缓存中没有则取默认值
                                String priceRatio = redisService.getJSONStringByKey("DEPT_PRICE_RATIO" + SecurityUtils.getLoginUser().getSysUser().getDeptId());
                                if (priceRatio != null) {
                                    BigDecimal sitePrice = itemInfo.getBigDecimal("price").divide(rate, 2, BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(priceRatio)).setScale(2, RoundingMode.HALF_UP);
                                    sitesPrice.setPrice(sitePrice);
                                } else {
                                    BigDecimal sitePrice = itemInfo.getBigDecimal("price").divide(rate, 2, BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(increaseFactor)).setScale(2, RoundingMode.HALF_UP);
                                    sitesPrice.setPrice(sitePrice);
                                }
                                sitesPrice.setValue("MLM");
                                sitesPrice.setLabel("墨西哥");
                                List<SitesPrice> sitesPriceList = new ArrayList<>();
                                sitesPriceList.add(sitesPrice);
                                result.setSitesPrice(sitesPriceList);

                                // 西班牙语标题
                                result.setSpanishTitle(itemInfo.getString("title"));
                                // 西班牙语说明
                                JSONObject des = MercadoHttpUtil.getDesByCbtId(token, url, cbtId, itemInfo.getString("site_id"), itemInfo.getJSONObject("shipping").getString("logistic_type"));
                                if (des != null) {
                                    result.setSpanishMsg(des.getString("plain_text"));
                                }
                            }
                        }

                        // 英语标题
                        String englishTitle = productInfo.getString("title");
                        result.setEnglishTitle(englishTitle);
                        // 基础属性
                        JSONArray attributes = productInfo.getJSONArray("attributes");
                        if (attributes != null && attributes.size() > 0) {
                            List<ProductInfo> productInfoList = new ArrayList<>();
                            for (int index = 0; index < attributes.size(); index++) {
                                JSONObject jsonObject = attributes.getJSONObject(index);
                                ProductInfo pro = new ProductInfo();
                                pro.setId(jsonObject.getString("id"));
                                pro.setName(jsonObject.getString("name"));
                                pro.setValue(jsonObject.getString("value_name"));
                                productInfoList.add(pro);
                            }
                            result.setProductInfo(productInfoList);
                        }

                        // 变体
                        List<String> skuList = new ArrayList<>();
                        JSONArray variations = productInfo.getJSONArray("variations");
                        if (variations != null && variations.size() > 0) {
                            JSONObject object = getVariations(productInfo);
                            JSONObject colorMap = object.getJSONObject("colorMap");
                            if (colorMap != null) {
                                JSONArray colorTags = colorMap.getJSONArray("colorTags");
                                if (colorTags != null && colorTags.size() > 0) {
                                    result.setColorId("COLOR");
                                }
                            }
                            JSONObject sizeMap = object.getJSONObject("sizeMap");
                            if (sizeMap != null) {
                                JSONArray sizeTags = sizeMap.getJSONArray("sizeTags");
                                if (sizeTags != null && sizeTags.size() > 0) {
                                    result.setSizeId("SIZE");
                                }
                            }
                            if (colorMap != null && sizeMap != null) {
                                JSONArray colorTags = colorMap.getJSONArray("colorTags");
                                JSONArray sizeTags = sizeMap.getJSONArray("sizeTags");
                                if (colorTags.size() > 0) {
                                    if (sizeTags.size() > 0) {
                                        for (int i = 0; i < colorTags.size(); i++) {
                                            for (int j = 0; j < sizeTags.size(); j++) {
                                                String sku = batchCollectionDTO.getSkuPre() + "_" + colorTags.getString(i) + "_" + sizeTags.getString(j);
                                                skuList.add(sku);
                                            }
                                        }
                                    } else {
                                        for (int k = 0; k < colorTags.size(); k++) {
                                            String sku = batchCollectionDTO.getSkuPre() + "_" + colorTags.getString(k);
                                            skuList.add(sku);
                                        }
                                    }
                                }
                            }
                            // 图片信息
                            JSONArray colorImagesList = object.getJSONArray("colorImagesList");
                            if (colorImagesList != null && colorImagesList.size() > 0) {
                                result.setFileList(colorImagesList);
                            }
                        }
                        if (result.getFileList() == null) {
                            JSONArray jsonArray = new JSONArray();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("color", "");
                            jsonObject.put("fileList", productInfo.getJSONArray("pictures"));
                            jsonArray.add(jsonObject);
                            result.setFileList(jsonArray);
                        }
                        result.setSkuList(skuList);
                        result.setSkuPre(batchCollectionDTO.getSkuPre());
                        result.setOperateType("add");
                        if (result.getSizeId() == null) {
                            result.setSizeId("");
                        }
                        if (result.getColorId() == null) {
                            result.setColorId("");
                        }
                    }
                }
            }
        }
        MercadoInfoDto mercadoInfoDto = productHandleData(JSONObject.parse(JSON.toJSONString(result)));
        mercadoProductItemService.releaseProductHandle(mercadoInfoDto);
        return AjaxResult.success(result);
    }

    /**
     * 处理变体信息
     *
     * @param productInfo
     * @return
     */
    private JSONObject getVariations(JSONObject productInfo) {
        JSONObject result = new JSONObject();
        // 变体数据
        JSONArray variations = productInfo.getJSONArray("variations");
        // 图片
        JSONArray pictures = productInfo.getJSONArray("pictures");
        List<Map<String, Object>> colorImagesList = new ArrayList<>();
        if (variations != null && variations.size() > 0) {
            //变体颜色
            List<String> colorTags = new ArrayList<>();
            String tab = "";
            //变体尺寸
            List<String> sizeTags = new ArrayList<>();
            String bor = "";
            //图片
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
            JSONObject colorMap = new JSONObject();
            colorMap.put("id", tab);
            colorMap.put("colorTags", colorTags);
            result.put("colorMap", colorMap);

            // 存放变体的第二个变量,可能没有或者是尺码
            JSONObject sizeMap = new JSONObject();
            sizeMap.put("id", bor);
            sizeMap.put("sizeTags", sizeTags);
            result.put("sizeMap", sizeMap);

            //图片对象
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
        }
        if (colorImagesList.size() == 0) {
            //说明没有变体,那么图片的color直接用空字符串
            Map<String, Object> map = new HashMap<>();
            map.put("color", "");
            map.put("fileList", pictures);
            colorImagesList.add(map);
        }
        result.put("colorImagesList", colorImagesList);
        return result;
    }

    /**
     * 处理数据
     *
     * @param dataInfos
     */
    public MercadoInfoDto productHandleData(JSONObject dataInfos) {
        logger.info("保存或者发布产品数据中转,参数接收:{}", dataInfos.toJSONString());
        //获取采集的产品id
        String cpProdutId = dataInfos.getString("cpProdutId");
        //获取全局产品价格
        BigDecimal price = dataInfos.getBigDecimal("price");
        //获取全局类目id
        String categoryId = dataInfos.getString("categoryId");
        //获取美客多采集地址
        String mercadoUrl = dataInfos.getString("mercadoUrl");
        //获取店铺list
        JSONArray shopIds = dataInfos.getJSONArray("shopIds");
        //获取站点list
        JSONArray siteIds = dataInfos.getJSONArray("siteIds");
        //获取站点价格
        JSONArray sitesPrice = dataInfos.getJSONArray("sitesPrice");
        //获取英文标题
        String englishTitle = dataInfos.getString("englishTitle");
        //获取西班牙语标题
        String spanishTitle = dataInfos.getString("spanishTitle");
        //获取葡萄牙标题
        String portugueseTitle = dataInfos.getString("portugueseTitle");
        //获取西班牙语说明
        String spanishMsg = dataInfos.getString("spanishMsg");
        //获取葡萄牙语说明
        String portugueseMsg = dataInfos.getString("portugueseMsg");
        //sku前缀
        String skuPre = dataInfos.getString("skuPre");
        //获取产品基础属性
        JSONArray productInfo = dataInfos.getJSONArray("productInfo");
        //获取快递档位
        String gears = dataInfos.getString("gears");
        //获取sku的list
        JSONArray skuList = dataInfos.getJSONArray("skuList");
        //变体第一个变量
        String colorId = dataInfos.getString("colorId");
        //变体第二个变量
        String sizeId = dataInfos.getString("sizeId");
        //获取图片list
        JSONArray fileList = dataInfos.getJSONArray("fileList");
        //判断是否保存或者保存产品后发布
        String operateType = dataInfos.getString("operateType");
        MercadoInfoDto mercadoInfoDto = new MercadoInfoDto();
        mercadoInfoDto.setId(cpProdutId);
        mercadoInfoDto.setTitle(englishTitle);
        mercadoInfoDto.setPrice(price);
        mercadoInfoDto.setCategoryId(categoryId);
        mercadoInfoDto.setMercadoProductUrl(mercadoUrl);
        mercadoInfoDto.setOperateType(operateType);
        mercadoInfoDto.setShipType(Long.valueOf(gears));
        mercadoInfoDto.setSkuPre(skuPre);
        mercadoInfoDto.setProductId(dataInfos.getLong("productId"));
        //设置店铺信息
        List<MercadoShopInfoDTO> mercadoShopInfoDTOList = new ArrayList<>();
        shopIds.forEach(item -> {
            MercadoShopInfoDTO mercadoShopInfoDTO = new MercadoShopInfoDTO();
            mercadoShopInfoDTO.setMerchantId(item.toString());
            mercadoShopInfoDTOList.add(mercadoShopInfoDTO);
        });
        //店铺设置站点
        mercadoShopInfoDTOList.forEach(item -> {
            List<MercadoSiteInfo> mercadoSiteInfoList = new ArrayList<>();
            siteIds.forEach(item2 -> {
                MercadoSiteInfo mercadoSiteInfo = new MercadoSiteInfo();
                mercadoSiteInfo.setSiteId(item2.toString());
                mercadoSiteInfoList.add(mercadoSiteInfo);
            });
            item.setMercadoSiteInfoList(mercadoSiteInfoList);
        });
        //店铺设置价格
        mercadoShopInfoDTOList.forEach(item -> {
            List<MercadoSiteInfo> mercadoSiteInfoList = item.getMercadoSiteInfoList();
            mercadoSiteInfoList.forEach(item2 -> {
                //设置店铺站点的标题和说明
                if (item2.getSiteId().equals("MLM") ||
                        item2.getSiteId().equals("MLC") ||
                        item2.getSiteId().equals("MCO") ||
                        item2.getSiteId().equals("MLMFULL")) {
                    item2.setTitle(spanishTitle);
                    item2.setDes(spanishMsg);
                }
                if (item2.getSiteId().equals("MLB")) {
                    item2.setTitle(portugueseTitle);
                    item2.setDes(portugueseMsg);
                }
                sitesPrice.forEach(item3 -> {
                    JSONObject json = JSONObject.from(item3);
                    if (item2.getSiteId().equals(json.getString("value"))) {
                        item2.setPrice(json.getBigDecimal("price"));
                    }
                });
            });
        });
        mercadoInfoDto.setMercadoShopInfoDTOList(mercadoShopInfoDTOList);
        //productInfo设置属性
        List<MercadoAttrInfo> mercadoAttrInfos = new ArrayList<>();
        productInfo.forEach(item -> {
            JSONObject json = JSONObject.from(item);
            if (json.getString("value") != null || !"".equals(json.getString("value"))) {
                if (!"SIZE_GRID_ID".equals(json.getString("id"))) {
                    MercadoAttrInfo mercadoAttrInfo = new MercadoAttrInfo();
                    mercadoAttrInfo.setId(json.getString("id"));
                    mercadoAttrInfo.setValueName(json.getString("value"));
                    mercadoAttrInfo.setName(json.getString("name"));
                    mercadoAttrInfos.add(mercadoAttrInfo);
                }
            }
        });
        //设置图片对象
        List<MercadoInfoPictureDto> mercadoInfoPictureDtos = new ArrayList<>();
        fileList.forEach(item -> {
            JSONObject json = JSONObject.from(item);
            JSONArray fileList1 = json.getJSONArray("fileList");
            fileList1.forEach(item2 -> {
                JSONObject json2 = JSONObject.from(item2);
                //过滤避免添加重复的元素
                if (mercadoInfoPictureDtos.stream().noneMatch(item1 -> item1.getId().equals(json2.getString("id")))) {
                    MercadoInfoPictureDto mercadoInfoPictureDto = new MercadoInfoPictureDto();
                    mercadoInfoPictureDto.setId(json2.getString("id"));
                    mercadoInfoPictureDto.setUrl(json2.getString("url"));
                    mercadoInfoPictureDtos.add(mercadoInfoPictureDto);
                }
            });
        });
        mercadoInfoDto.setMercadoInfoPictureDtos(mercadoInfoPictureDtos);
        //设置变体
        List<MercadoVarInfo> mercadoVarInfos = new ArrayList<>();
        if (StringUtils.isNotEmpty(colorId)) {
            skuList.forEach(item -> {
                MercadoVarInfo mercadoVarInfo = new MercadoVarInfo();
                String itemstring = item.toString();
                mercadoVarInfo.setSku(itemstring);
                String[] s = itemstring.split("_");
                //第一个变量变体的值
                String colorValue = s[1];
                String sizeValue = null;
                //说明有两个变体变量
                if (s.length == 3) {
                    //第二个变量变体的值
                    sizeValue = s[2];
                }
                fileList.forEach(item1 -> {
                    JSONObject json = JSONObject.from(item1);
                    if (json.getString("color").equals(colorValue)) {
                        List<String> urls = json.getJSONArray("fileList").stream()
                                .map(file -> {
                                    JSONObject json1 = JSONObject.from(file);
                                    return json1.getString("id");
                                }).collect(Collectors.toList());
                        mercadoVarInfo.setPictureIds(urls);
                    }
                });
                List<AttributeCombinations> attributeCombinations = new ArrayList<>();
                AttributeCombinations attributeCombinations1 = new AttributeCombinations();
                attributeCombinations1.setId(colorId);
                attributeCombinations1.setValueName(colorValue);
                attributeCombinations.add(attributeCombinations1);
                if (sizeValue != null) {
                    AttributeCombinations attributeCombinations2 = new AttributeCombinations();
                    attributeCombinations2.setId(sizeId);
                    attributeCombinations2.setValueName(sizeValue);
                    attributeCombinations.add(attributeCombinations2);
                }
                mercadoVarInfo.setAttributeCombinations(attributeCombinations);
                mercadoVarInfos.add(mercadoVarInfo);
            });
        }
        // 处理尺码信息
        JSONObject sizeChartInfo = dataInfos.getJSONObject("sizeChartInfo");
        if (sizeChartInfo != null) {
            String id = sizeChartInfo.getString("id");

            MercadoAttrInfo mercadoAttrInfo = new MercadoAttrInfo();
            mercadoAttrInfo.setId("SIZE_GRID_ID");
            mercadoAttrInfo.setValueName(id);
            mercadoAttrInfo.setName("SIZE_GRID_ID");
            mercadoAttrInfos.add(mercadoAttrInfo);

            JSONArray rows = sizeChartInfo.getJSONArray("rows");
            if (rows != null) {
                Map<String, String> rowsMap = new HashMap<>();
                if (rows.size() > 0) {
                    for (int index = 0; index < rows.size(); index++) {
                        JSONObject jsonObject = rows.getJSONObject(index);
                        rowsMap.put(jsonObject.getString("name"), jsonObject.getString("id"));
                    }
                }
                if (!mercadoVarInfos.isEmpty()) {
                    for (MercadoVarInfo mercadoVarInfo : mercadoVarInfos) {
                        String sku = mercadoVarInfo.getSku();
                        if (sku != null) {
                            String[] split = sku.split("_");
                            if (split.length == 3) {
                                String size = split[2];

                                if (rowsMap.get(size) != null) {
                                    mercadoVarInfo.setSizeChartId(rowsMap.get(size));
                                }
                            }
                        }
                    }
                }
            }

        }
        mercadoInfoDto.setMercadoVarInfos(mercadoVarInfos);
        mercadoInfoDto.setMercadoAttrInfos(mercadoAttrInfos);
        return mercadoInfoDto;
    }


}
