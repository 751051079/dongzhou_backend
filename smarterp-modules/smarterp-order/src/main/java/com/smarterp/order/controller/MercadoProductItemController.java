package com.smarterp.order.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.StringUtils;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.core.utils.mercado.MercadoNewHttpUtil;
import com.smarterp.common.core.utils.poi.ExcelUtil;
import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.core.web.page.TableDataInfo;
import com.smarterp.common.log.annotation.Log;
import com.smarterp.common.log.enums.BusinessType;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.common.security.annotation.RequiresPermissions;
import com.smarterp.order.domain.MercadoProductItem;
import com.smarterp.order.domain.dto.*;
import com.smarterp.order.service.IMercadoProductItemService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 产品，店铺和站点信息关联Controller
 *
 * @author smarterp
 * @date 2023-04-26
 */
@RestController
@RequestMapping("/item")
public class MercadoProductItemController extends BaseController {

    @Resource
    private IMercadoProductItemService mercadoProductItemService;

    @Resource
    private RedisService redisService;

    /**
     * 发布产品
     *
     * @param mercadoInfoDto
     * @return
     */
    @PostMapping("/release/product")
    public AjaxResult releaseProduct(@RequestBody MercadoInfoDto mercadoInfoDto) {
        return mercadoProductItemService.releaseProductHandle(mercadoInfoDto);
    }


    /**
     * 保存或者发布产品数据中转
     *
     * @param dataInfos
     * @return
     */
    @PostMapping("/release/productHandleData")
    public AjaxResult productHandleData(@RequestBody JSONObject dataInfos) {
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
        String accessToken = redisService.getCacheObject("ACCESS_TOKEN_SHOP:" + shopIds.get(0));

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
        // 编辑的时候不做这个操作
        if (mercadoInfoDto.getProductId() == null) {
            JSONObject itemInfo = MercadoNewHttpUtil.getProductInfo(cpProdutId, accessToken);
            if (itemInfo != null) {
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
                    if (attributesProductInfo != null && attributesProductInfo.size() > 0) {
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
                }
                if (shipInfo.size() > 0) {
                    mercadoInfoDto.setShipInfo(shipInfo.toJSONString());
                }
            }
        }

        return releaseProduct(mercadoInfoDto);
    }

    /**
     * 查询产品，店铺和站点信息关联列表
     */
    @RequiresPermissions("order:item:list")
    @GetMapping("/list")
    public TableDataInfo list(MercadoProductItem mercadoProductItem) {
        startPage();
        List<MercadoProductItem> list = mercadoProductItemService.selectMercadoProductItemList(mercadoProductItem);
        return getDataTable(list);
    }

    /**
     * 导出产品，店铺和站点信息关联列表
     */
    @RequiresPermissions("order:item:export")
    @Log(title = "产品，店铺和站点信息关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MercadoProductItem mercadoProductItem) {
        List<MercadoProductItem> list = mercadoProductItemService.selectMercadoProductItemList(mercadoProductItem);
        ExcelUtil<MercadoProductItem> util = new ExcelUtil<MercadoProductItem>(MercadoProductItem.class);
        util.exportExcel(response, list, "产品，店铺和站点信息关联数据");
    }

    /**
     * 获取产品，店铺和站点信息关联详细信息
     */
    @RequiresPermissions("order:item:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(mercadoProductItemService.selectMercadoProductItemById(id));
    }

    /**
     * 新增产品，店铺和站点信息关联
     */
    @RequiresPermissions("order:item:add")
    @Log(title = "产品，店铺和站点信息关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MercadoProductItem mercadoProductItem) {
        return toAjax(mercadoProductItemService.insertMercadoProductItem(mercadoProductItem));
    }

    /**
     * 修改产品，店铺和站点信息关联
     */
    @RequiresPermissions("order:item:edit")
    @Log(title = "产品，店铺和站点信息关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MercadoProductItem mercadoProductItem) {
        return toAjax(mercadoProductItemService.updateMercadoProductItem(mercadoProductItem));
    }

    /**
     * 删除产品，店铺和站点信息关联
     */
    @RequiresPermissions("order:item:remove")
    @Log(title = "产品，店铺和站点信息关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(mercadoProductItemService.deleteMercadoProductItemByIds(ids));
    }
}
