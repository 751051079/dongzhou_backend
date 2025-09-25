package com.smarterp.order.service.impl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.common.security.utils.SecurityUtils;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoBatchCollectionLink;
import com.smarterp.order.domain.dto.*;
import com.smarterp.order.mapper.MercadoBatchCollectionLinkMapper;
import com.smarterp.order.mapper.MercadoProductMapper;
import com.smarterp.order.service.IMercadoBatchCollectionLinkService;
import com.smarterp.system.api.model.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 批量采集的链接Service业务层处理
 *
 * @author smarterp
 * @date 2024-05-27
 */
@Service
public class MercadoBatchCollectionLinkServiceImpl implements IMercadoBatchCollectionLinkService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoBatchCollectionLinkServiceImpl.class);

    @Resource
    private MercadoBatchCollectionLinkMapper mercadoBatchCollectionLinkMapper;

    @Resource
    private MercadoProductMapper mercadoProductMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private AmqpTemplate amqpTemplate;

    /**
     * 查询批量采集的链接
     *
     * @param id 批量采集的链接主键
     * @return 批量采集的链接
     */
    @Override
    public MercadoBatchCollectionLink selectMercadoBatchCollectionLinkById(String id) {
        return mercadoBatchCollectionLinkMapper.selectMercadoBatchCollectionLinkById(id);
    }

    /**
     * 查询批量采集的链接列表
     *
     * @param query 批量采集的链接
     * @return 批量采集的链接
     */
    @Override
    public List<MercadoBatchCollectionLinkDetails> selectMercadoBatchCollectionLinkList(MercadoBatchCollectionLinkQuery query, Integer pageNum, Integer pageSize) {
        String userName = SecurityUtils.getLoginUser().getSysUser().getUserName();
        if (!"admin".equals(userName)) {
            query.setDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
        }
        if (query.getCreateBeginTime() != null && query.getCreateEndTime() != null) {
            query.setCreateBeginTime(query.getCreateBeginTime() + " 00:00:00");
            query.setCreateEndTime(query.getCreateEndTime() + " 23:59:59");
        }
        if (query.getUpdateBeginTime() != null && query.getUpdateEndTime() != null) {
            query.setUpdateBeginTime(query.getUpdateBeginTime() + " 00:00:00");
            query.setUpdateEndTime(query.getUpdateEndTime() + " 23:59:59");
        }
        PageHelper.startPage(pageNum, pageSize);
        logger.info("查询采集列表参数 {}", JSON.toJSONString(query));
        return mercadoBatchCollectionLinkMapper.selectMercadoBatchCollectionLinkList(query);
    }

    /**
     * 新增批量采集的链接
     *
     * @param mercadoBatchCollectionLinkAddDTO 批量采集的链接
     * @return 结果
     */
    @Override
    public AjaxResult insertMercadoBatchCollectionLink(MercadoBatchCollectionLinkAddDTO mercadoBatchCollectionLinkAddDTO) {
        if (mercadoBatchCollectionLinkAddDTO.getProductUrl() == null) {
            return AjaxResult.error("请输入产品链接!");
        }
        String[] split = mercadoBatchCollectionLinkAddDTO.getProductUrl().split("\\r?\\n");
        if (split.length > 0) {
            for (int index = 0; index < split.length; index++) {
                String itemUrl = split[index];
                String itemId = extractProductId(itemUrl);
                if (itemId != null) {
                    logger.info("新增的采集链接 {}", itemId);
                    MercadoBatchCollectionLink mercadoBatchCollectionLink = new MercadoBatchCollectionLink();
                    mercadoBatchCollectionLink.setDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
                    mercadoBatchCollectionLink.setUserId(SecurityUtils.getLoginUser().getUserid());
                    mercadoBatchCollectionLink.setItemId(itemId.replace("-", ""));
                    amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                            RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_BATCH_COLLECTION_LINK, mercadoBatchCollectionLink);
                }
            }
        }
        return AjaxResult.success("批量采集产品成功!");
    }

    /**
     * 修改批量采集的链接
     *
     * @param mercadoBatchCollectionLink 批量采集的链接
     * @return 结果
     */
    @Override
    public int updateMercadoBatchCollectionLink(MercadoBatchCollectionLink mercadoBatchCollectionLink) {
        mercadoBatchCollectionLink.setUpdateTime(new Date());
        return mercadoBatchCollectionLinkMapper.updateMercadoBatchCollectionLink(mercadoBatchCollectionLink);
    }

    /**
     * 批量删除批量采集的链接
     *
     * @param ids 需要删除的批量采集的链接主键
     * @return 结果
     */
    @Override
    public int deleteMercadoBatchCollectionLinkByIds(String[] ids) {
        return mercadoBatchCollectionLinkMapper.deleteMercadoBatchCollectionLinkByIds(ids);
    }

    /**
     * 删除批量采集的链接信息
     *
     * @param id 批量采集的链接主键
     * @return 结果
     */
    @Override
    public int deleteMercadoBatchCollectionLinkById(String id) {
        return mercadoBatchCollectionLinkMapper.deleteMercadoBatchCollectionLinkById(id);
    }

    /**
     * 根据店铺链接提取出店铺id
     *
     * @param shopUrl
     * @return
     */
    private String getMerchantId(String shopUrl) {
        // 正则表达式匹配连续的阿拉伯数字
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(shopUrl);

        while (matcher.find()) {
            String merchantId = matcher.group();
            if (merchantId.length() > 5) {
                return merchantId;
            }
        }
        return null;
    }

    /**
     * 批量采集链接
     *
     * @param mercadoBatchCollectionLinkDTO
     * @return
     */
    @Override
    public AjaxResult batchCollection(MercadoBatchCollectionLinkDTO mercadoBatchCollectionLinkDTO) {
        String shopUrl = mercadoBatchCollectionLinkDTO.getShopUrl();
        if (shopUrl != null) {
            String merchantId = getMerchantId(shopUrl);
            for (int index = 0; index < 42; index++) {
                int pageNo = index * 48 + 1;
                String url = "https://listado.mercadolibre.com.mx/_Desde_" + pageNo + "_CustId_" + merchantId + "_NoIndex_True";
                // 将采集的逻辑放到队列中处理
                MercadoBatchCollectionLinkDTO model = new MercadoBatchCollectionLinkDTO();
                model.setShopUrl(url);
                model.setDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
                model.setUserId(SecurityUtils.getLoginUser().getUserid());
                amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                        RabbitMQConstant.RoutingKey.GET_PRODUCT_URL_BY_SHOP_URL, model);
            }
        }
        return AjaxResult.success("采集链接成功，请稍后刷新页面!");
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
                    amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                            RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_BATCH_COLLECTION_LINK_TO_SHOP, record);
                }
            }
        }
        return AjaxResult.success("链接采集到店铺成功!");
    }

    @Override
    public AjaxResult importData(MultipartFile multipartFile) {
        JSONArray jsonArray = new JSONArray();
        try {
            // 将 MultipartFile 转换为 File 对象
            File file = FileUtil.file("/home/mercado/server/smarterp-order/temp.xlsx");
            multipartFile.transferTo(file);

            // 使用 Hutool 的 ExcelUtil 读取 Excel 文件
            ExcelReader reader = ExcelUtil.getReader(file);

            // 读取 Excel 文件的所有行和列内容
            reader.readAll().forEach(row -> {
                JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(row));
                jsonArray.add(jsonObject);
            });

            // 关闭 ExcelReader 对象
            reader.close();

            // 删除临时文件
            FileUtil.del(file);
        } catch (Exception e) {
            logger.info("解析文件异常 {}", e);
        }
        // 处理采集到的链接信息
        try {
            List<String> processLinkInfo = processLinkInfo(jsonArray);
            logger.error("采集到的链接信息 {}", JSON.toJSONString(processLinkInfo));

            if (!processLinkInfo.isEmpty()) {
                for (String itemId : processLinkInfo) {
                    MercadoBatchCollectionLink mercadoBatchCollectionLink = new MercadoBatchCollectionLink();
                    mercadoBatchCollectionLink.setDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
                    mercadoBatchCollectionLink.setUserId(SecurityUtils.getLoginUser().getUserid());
                    mercadoBatchCollectionLink.setItemId(itemId.replace("-", ""));
                    amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                            RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_BATCH_COLLECTION_LINK, mercadoBatchCollectionLink);
                }
            }
        } catch (Exception e) {
            logger.info("采集链接异常 {}", e);
        }
        return AjaxResult.success("导入链接成功!");
    }

    /**
     * 处理采集到的链接信息
     *
     * @param jsonArray
     */
    private List<String> processLinkInfo(JSONArray jsonArray) {
        List<String> linkIdList = new ArrayList<>();
        if (jsonArray.size() > 0) {
            for (int index = 0; index < jsonArray.size(); index++) {
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                List<String> readJsonObject = readJsonObject(jsonObject);
                if (!readJsonObject.isEmpty()) {
                    String productUrl = readJsonObject.get(0);
                    String productId = extractProductId(productUrl);
                    if (productId != null) {
                        linkIdList.add(productId);
                    }
                }
            }
        }
        return linkIdList;
    }

    /**
     * 提取出MLM编号
     *
     * @param productUrl
     * @return
     */
    public static String extractProductId(String productUrl) {
        // 匹配 MLM- 后面跟随的数字部分
        Pattern pattern = Pattern.compile("MLM-\\d+");
        Matcher matcher = pattern.matcher(productUrl);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * 读取fastjson所有的value值
     *
     * @param jsonObject
     * @return
     */
    public List<String> readJsonObject(JSONObject jsonObject) {
        List<String> result = new ArrayList<>();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                // 如果值是对象类型，递归读取对象
                readJsonObject((JSONObject) value);
            } else {
                result.add(value.toString());
            }
        }
        return result;
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
