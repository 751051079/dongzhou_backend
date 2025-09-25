package com.smarterp.order.bus;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoSecureUtil;
import com.smarterp.common.core.utils.mercado.vo.*;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.*;
import com.smarterp.order.domain.dto.MercadoShopInfo;
import com.smarterp.order.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Component
public class MercadoCreateProductListener {

    private static final Logger logger = LoggerFactory.getLogger(MercadoCreateProductListener.class);

    @Resource
    private DelaySend delaySend;

    @Resource
    private MercadoProductMapper mercadoProductMapper;

    @Resource
    private MercadoProductItemMapper mercadoProductItemMapper;

    @Resource
    private MercadoProductAttributesMapper mercadoProductAttributesMapper;

    @Resource
    private MercadoProductCombinationMapper mercadoProductCombinationMapper;

    @Resource
    private MercadoProductErrorLogMapper mercadoProductErrorLogMapper;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    // 快递档位1档(0-0.1kg)
    public static List<String> oneSize = Arrays.asList("0.1 kg", "18 cm", "5 cm", "5 cm");

    // 快递档位2档(0.1-0.2kg)
    public static List<String> twoSize = Arrays.asList("0.19 kg", "23 cm", "7 cm", "6 cm");

    // 快递档位3档(0.2-0.3kg)
    public static List<String> threeSize = Arrays.asList("0.29 kg", "25 cm", "8 cm", "7 cm");

    // 快递档位4档(0.3-0.4kg)
    public static List<String> fourSize = Arrays.asList("0.39 kg", "26 cm", "9 cm", "8 cm");

    // 快递档位5档(0.4-0.5kg)
    public static List<String> fiveSize = Arrays.asList("0.49 kg", "27 cm", "10 cm", "9 cm");

    // 快递档位6档(0.5-0.6kg)
    public static List<String> sixSize = Arrays.asList("0.59 kg", "29 cm", "10 cm", "10 cm");

    // 快递档位7档(0.6-0.7kg)
    public static List<String> sevenSize = Arrays.asList("0.69 kg", "31 cm", "11 cm", "10 cm");

    // 快递档位8档(0.7-0.8kg)
    public static List<String> eightSize = Arrays.asList("0.79 kg", "33 cm", "11 cm", "11 cm");

    // 快递档位9档(0.8-0.9kg)
    public static List<String> nineSize = Arrays.asList("0.89 kg", "34 cm", "12 cm", "11 cm");

    // 快递档位10档(0.9-1.0kg)
    public static List<String> tenSize = Arrays.asList("0.99 kg", "35 cm", "14 cm", "10 cm");

    // 快递档位11档(1.0-1.5kg)
    public static List<String> elevenSize = Arrays.asList("1.49 kg", "38 cm", "16 cm", "12 cm");

    // 快递档位12档(1.5-2.0kg)
    public static List<String> twelveSize = Arrays.asList("1.99 kg", "40 cm", "17 cm", "14 cm");

    // 快递档位13档(2.0-3.0kg)
    public static List<String> thirteenSize = Arrays.asList("2.99 kg", "43 cm", "20 cm", "17 cm");


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
     * 发送全局产品信息消费者逻辑
     *
     * @param productId
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.CLOUD_PULL_MERCADO_PRODUCT_INFO_QUEUE),
            key = RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_PRODUCT_INFO))
    public void pullMercadoProduct(Long productId) {
        // 开始时间
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("RELEASE:GLOBAL:PRODUCT:" + productId);
        boolean b = lock.tryLock();
        try {
            if (b) {
                // 根据productId查询到产品信息
                MercadoProduct mercadoProduct = mercadoProductMapper.selectMercadoProductById(productId);
                if (mercadoProduct != null && !"ReleaseFail".equals(mercadoProduct.getReleaseStatus())
                        && !"ReleaseSuccess".equals(mercadoProduct.getReleaseStatus())) {
                    // 默认发布失败
                    MercadoProduct exampleResult = new MercadoProduct();
                    exampleResult.setId(productId);
                    exampleResult.setReleaseStatus("ReleaseFail");

                    // 发送全局产品
                    // 根据merchantId查询到accessToken
                    String merchantId = mercadoProduct.getMerchantId();
                    MercadoShopInfo mercadoShopInfo = mercadoProductMapper.getAccessTokenByMerchantId(Long.parseLong(merchantId));
                    if (mercadoShopInfo != null) {
                        // 解密accessToken
                        String accessToken = MercadoSecureUtil.decryptMercado(String.valueOf(mercadoShopInfo.getId()), mercadoShopInfo.getAccessToken());
                        HttpRequest post = HttpUtil.createPost("https://api.mercadolibre.com/items");
                        // 设置超时时间
                        post.setConnectionTimeout(connectionTimeout);
                        post.setReadTimeout(readTimeout);
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + accessToken);
                        GrobItems items = new GrobItems();
                        // 类目id
                        items.setCategory_id(mercadoProduct.getCbtCategory());
                        // 标题
                        items.setTitle(mercadoProduct.getProductTitle());
                        // 库存 注意如果没有变体直接使用全局产品的库存数量，如果有变体需要将每个变体的数量相加
                        // 根据productId查询变体信息
                        MercadoProductCombination exampleCombination = new MercadoProductCombination();
                        exampleCombination.setProductId(productId);
                        List<MercadoProductCombination> mercadoProductCombinations = mercadoProductCombinationMapper.selectMercadoProductCombinationList(exampleCombination);
                        if (!mercadoProductCombinations.isEmpty()) {
                            Integer availableQuantity = 0;
                            for (MercadoProductCombination item : mercadoProductCombinations) {
                                availableQuantity += item.getAvailableQuantity();
                            }
                            items.setAvailable_quantity(availableQuantity);
                        } else {
                            items.setAvailable_quantity(mercadoProduct.getAvailableQuantity());
                        }
                        // 全局产品价格
                        items.setPrice(mercadoProduct.getSalePrice());
                        // 处理卖家条款
                        List<Attributes> sales = new ArrayList<>();
                        Attributes sale1 = new Attributes();
                        sale1.setId("WARRANTY_TIME");
                        sale1.setValue_name("1 months");
                        sales.add(sale1);
                        Attributes sale2 = new Attributes();
                        sale2.setId("WARRANTY_TYPE");
                        sale2.setValue_id("2230280");
                        sale2.setValue_name("Seller warranty");
                        sales.add(sale2);
                        items.setSale_terms(sales);

                        // 添加图片信息
                        List<ItemPictures> pictures = items.getPictures();
                        JSONArray picturesArray = JSONArray.parseArray(mercadoProduct.getPictures());
                        for (int index = 0; index < picturesArray.size(); index++) {
                            JSONObject jsonObject = picturesArray.getJSONObject(index);
                            pictures.add(ItemPictures.getInstance(jsonObject.getString("id")));
                        }
                        // 添加属性
                        List<Attributes> attributes = new ArrayList<>();
                        MercadoProductAttributes attributesExample = new MercadoProductAttributes();
                        attributesExample.setProductId(productId);
                        attributesExample.setAttributeType("attribute");
                        List<MercadoProductAttributes> attributeList = mercadoProductAttributesMapper.selectMercadoProductAttributesList(attributesExample);
                        if (!attributeList.isEmpty()) {
                            for (MercadoProductAttributes item : attributeList) {
                                if (item.getAttributeId().equals("BRAND")) {
                                    Attributes attribute = new Attributes();
                                    attribute.setId("BRAND");
                                    attribute.setValue_name("Generic");
                                    attributes.add(attribute);
                                } else if (item.getAttributeId().equals("GTIN")) {
                                    if (mercadoProductCombinations.isEmpty()) {
                                        Attributes attribute = new Attributes();
                                        attribute.setId("GTIN");
                                        attribute.setValue_name(mercadoProduct.getUpcCode());
                                        attributes.add(attribute);
                                    }
                                } else if (item.getAttributeId().equals("SELLER_SKU")) {
                                    if (mercadoProductCombinations.isEmpty()) {
                                        Attributes attribute = new Attributes();
                                        attribute.setId("SELLER_SKU");
                                        attribute.setValue_name(mercadoProduct.getSkuPre());
                                        attributes.add(attribute);
                                    }
                                } /*else if (item.getAttributeId().equals("PACKAGE_WEIGHT")) {
                                    if (mercadoProductCombinations.isEmpty()) {
                                        setShipment(attributes, mercadoProduct, item.getAttributeId());
                                    }
                                } else if (item.getAttributeId().equals("PACKAGE_LENGTH")) {
                                    if (mercadoProductCombinations.isEmpty()) {
                                        setShipment(attributes, mercadoProduct, item.getAttributeId());
                                    }
                                } else if (item.getAttributeId().equals("PACKAGE_WIDTH")) {
                                    if (mercadoProductCombinations.isEmpty()) {
                                        setShipment(attributes, mercadoProduct, item.getAttributeId());
                                    }
                                } else if (item.getAttributeId().equals("PACKAGE_HEIGHT")) {
                                    if (mercadoProductCombinations.isEmpty()) {
                                        setShipment(attributes, mercadoProduct, item.getAttributeId());
                                    }
                                } */ else {
                                    Attributes attribute = new Attributes();
                                    attribute.setId(item.getAttributeId());
                                    attribute.setValue_name(item.getAttributeValueName());
                                    attributes.add(attribute);
                                }
                            }
                        }
                        items.setAttributes(attributes);
                        // 处理变体
                        List<Variations> variations = new ArrayList<>();
                        if (!mercadoProductCombinations.isEmpty()) {
                            // 排序
                            List<MercadoProductCombination> collect = mercadoProductCombinations.stream().sorted(Comparator.comparing(MercadoProductCombination::getCombinationSort)).collect(Collectors.toList());
                            for (MercadoProductCombination item : collect) {
                                Variations variation = new Variations();
                                variation.setPicture_ids(JSONArray.parseArray(item.getPictures()).toJavaList(String.class));
                                variation.setAvailable_quantity(item.getAvailableQuantity());
                                variation.setPrice(mercadoProduct.getSalePrice());
                                List<VariationsAttributeCombinations> attribute_combinations = new ArrayList<>();

                                JSONArray jsonArray = JSONArray.parseArray(item.getCombinationJson());
                                for (int index = 0; index < jsonArray.size(); index++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                                    VariationsAttributeCombinations variationsAttributeCombinations = new VariationsAttributeCombinations();
                                    variationsAttributeCombinations.setId(jsonObject.getString("id"));
                                    variationsAttributeCombinations.setValue_name(jsonObject.getString("valueName"));
                                    attribute_combinations.add(variationsAttributeCombinations);
                                }
                                variation.setAttribute_combinations(attribute_combinations);

                                List<Attributes> attributesVs = new ArrayList<>();
                                Attributes attributesV = new Attributes();
                                attributesV.setId("GTIN");
                                attributesV.setValue_name(mercadoProduct.getUpcCode());
                                attributesVs.add(attributesV);
                                Attributes attributesV2 = new Attributes();
                                attributesV2.setId("SELLER_SKU");
                                attributesV2.setValue_name(item.getCombinationSku());
                                attributesVs.add(attributesV2);

                                if (item.getSizeChartId() != null) {
                                    Attributes attributesV3 = new Attributes();
                                    attributesV3.setId("SIZE_GRID_ROW_ID");
                                    attributesV3.setValue_name(item.getSizeChartId());
                                    attributesVs.add(attributesV3);
                                }

                                // 设置重量和尺寸
                                setShipment(attributesVs, mercadoProduct.getShipInfo());
                                variation.setAttributes(attributesVs);
                                variations.add(variation);
                            }
                        }
                        items.setVariations(variations);
                        logger.info("mercado上传全局产品请求参数 {}", JSONObject.toJSONString(items));
                        String res = post.addHeaders(headers).body(JSONObject.toJSONString(items)).execute().body();
                        logger.info("mercado上传全局产品响应信息 {}", JSONObject.toJSONString(res));
                        if (null != res) {
                            JSONObject jsonObject = JSONObject.parseObject(res);
                            if (!jsonObject.getString("status").equals("active") || jsonObject.getString("status").equals("400")) {
                                // 上架全局产品失败,将状态更改为失败
                                logger.error("mercado上传全局产品失败 {}", res);

                                // 写入错误日志信息
                                MercadoProductErrorLog mercadoProductErrorLog = new MercadoProductErrorLog();
                                mercadoProductErrorLog.setId(IdUtil.getSnowflake(1, 1).nextId());
                                mercadoProductErrorLog.setErrorType("CBT");
                                mercadoProductErrorLog.setProductId(productId);
                                mercadoProductErrorLog.setErrorInfo(res);
                                mercadoProductErrorLogMapper.insertMercadoProductErrorLog(mercadoProductErrorLog);
                            } else {
                                String cbtProId = jsonObject.getString("id");
                                // 更新全局产品id和状态
                                exampleResult.setCbtProId(cbtProId);
                                exampleResult.setReleaseStatus("ReleaseSuccess");

                                // 如果全局产品发送成功，则继续处理发送站点逻辑，将数据丢到队列中
                                MercadoProductItem example = new MercadoProductItem();
                                example.setProductId(productId);
                                List<MercadoProductItem> mercadoProductItems = mercadoProductItemMapper.selectMercadoProductItemList(example);
                                if (!mercadoProductItems.isEmpty()) {
                                    for (MercadoProductItem mercadoProductItem : mercadoProductItems) {
                                        // 第一次发送站点延迟5秒钟
                                        delaySend.sendProductSite(mercadoProductItem.getId(), "5000");
                                    }
                                }
                            }
                        }
                    }

                    // 更新全局产品状态
                    exampleResult.setUpdateTime(new Date());
                    mercadoProductMapper.updateMercadoProduct(exampleResult);
                }
            } else {
                logger.error("没有拿到锁 产品id {}", productId);
            }
        } catch (Exception e) {
            logger.error("上架全局产品异常 {}", e);
            // 将产品发布状态改为发布失败
            MercadoProduct updateReleaseStatus = new MercadoProduct();
            updateReleaseStatus.setId(productId);
            updateReleaseStatus.setReleaseStatus("ReleaseFail");
            updateReleaseStatus.setUpdateTime(new Date());
            mercadoProductMapper.updateMercadoProduct(updateReleaseStatus);
        } finally {
            long end = System.currentTimeMillis();
            logger.info("上架全局产品耗费时间 {} 产品ID {}", (end - startTime), productId);
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("上架全局产品释放锁异常 {}", e);
            }
        }
    }

    /**
     * 设置重量尺寸
     *
     * @param attributesVs
     * @param mercadoProduct
     */
    private void setShipment(List<Attributes> attributesVs, MercadoProduct mercadoProduct) {
        // 添加尺寸
        Attributes attribute3 = new Attributes();
        attributesVs.add(attribute3);
        Attributes attribute4 = new Attributes();
        attributesVs.add(attribute4);
        Attributes attribute5 = new Attributes();
        attributesVs.add(attribute5);
        Attributes attribute6 = new Attributes();
        attributesVs.add(attribute6);
        attribute3.setId("PACKAGE_WEIGHT");
        attribute4.setId("PACKAGE_LENGTH");
        attribute5.setId("PACKAGE_WIDTH");
        attribute6.setId("PACKAGE_HEIGHT");
        if (mercadoProduct.getShipType().equals(1L)) {
            attribute3.setValue_name(oneSize.get(0));
            attribute4.setValue_name(oneSize.get(1));
            attribute5.setValue_name(oneSize.get(2));
            attribute6.setValue_name(oneSize.get(3));
        } else if (mercadoProduct.getShipType().equals(2L)) {
            attribute3.setValue_name(twoSize.get(0));
            attribute4.setValue_name(twoSize.get(1));
            attribute5.setValue_name(twoSize.get(2));
            attribute6.setValue_name(twoSize.get(3));
        } else if (mercadoProduct.getShipType().equals(3L)) {
            attribute3.setValue_name(threeSize.get(0));
            attribute4.setValue_name(threeSize.get(1));
            attribute5.setValue_name(threeSize.get(2));
            attribute6.setValue_name(threeSize.get(3));
        } else if (mercadoProduct.getShipType().equals(4L)) {
            attribute3.setValue_name(fourSize.get(0));
            attribute4.setValue_name(fourSize.get(1));
            attribute5.setValue_name(fourSize.get(2));
            attribute6.setValue_name(fourSize.get(3));
        } else if (mercadoProduct.getShipType().equals(5L)) {
            attribute3.setValue_name(fiveSize.get(0));
            attribute4.setValue_name(fiveSize.get(1));
            attribute5.setValue_name(fiveSize.get(2));
            attribute6.setValue_name(fiveSize.get(3));
        } else if (mercadoProduct.getShipType().equals(6L)) {
            attribute3.setValue_name(sixSize.get(0));
            attribute4.setValue_name(sixSize.get(1));
            attribute5.setValue_name(sixSize.get(2));
            attribute6.setValue_name(sixSize.get(3));
        } else if (mercadoProduct.getShipType().equals(7L)) {
            attribute3.setValue_name(sevenSize.get(0));
            attribute4.setValue_name(sevenSize.get(1));
            attribute5.setValue_name(sevenSize.get(2));
            attribute6.setValue_name(sevenSize.get(3));
        } else if (mercadoProduct.getShipType().equals(8L)) {
            attribute3.setValue_name(eightSize.get(0));
            attribute4.setValue_name(eightSize.get(1));
            attribute5.setValue_name(eightSize.get(2));
            attribute6.setValue_name(eightSize.get(3));
        } else if (mercadoProduct.getShipType().equals(9L)) {
            attribute3.setValue_name(nineSize.get(0));
            attribute4.setValue_name(nineSize.get(1));
            attribute5.setValue_name(nineSize.get(2));
            attribute6.setValue_name(nineSize.get(3));
        } else if (mercadoProduct.getShipType().equals(10L)) {
            attribute3.setValue_name(tenSize.get(0));
            attribute4.setValue_name(tenSize.get(1));
            attribute5.setValue_name(tenSize.get(2));
            attribute6.setValue_name(tenSize.get(3));
        } else if (mercadoProduct.getShipType().equals(11L)) {
            attribute3.setValue_name(elevenSize.get(0));
            attribute4.setValue_name(elevenSize.get(1));
            attribute5.setValue_name(elevenSize.get(2));
            attribute6.setValue_name(elevenSize.get(3));
        } else if (mercadoProduct.getShipType().equals(12L)) {
            attribute3.setValue_name(twelveSize.get(0));
            attribute4.setValue_name(twelveSize.get(1));
            attribute5.setValue_name(twelveSize.get(2));
            attribute6.setValue_name(twelveSize.get(3));
        } else if (mercadoProduct.getShipType().equals(13L)) {
            attribute3.setValue_name(thirteenSize.get(0));
            attribute4.setValue_name(thirteenSize.get(1));
            attribute5.setValue_name(thirteenSize.get(2));
            attribute6.setValue_name(thirteenSize.get(3));
        }


    }

    /**
     * 设置重量尺寸
     *
     * @param attributesVs
     * @param shipInfo
     */
    private void setShipment(List<Attributes> attributesVs, String shipInfo) {
        JSONArray jsonArray = JSON.parseArray(shipInfo);
        if (jsonArray != null && jsonArray.size() > 0) {
            for (int index = 0; index < jsonArray.size(); index++) {
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                Attributes attribute = new Attributes();
                attribute.setId(jsonObject.getString("id"));
                attribute.setValue_name(jsonObject.getString("value_name"));
                attributesVs.add(attribute);
            }
        }
    }


    /**
     * 设置重量尺寸
     *
     * @param attributesVs
     * @param mercadoProduct
     * @param attributeId
     */
    private void setShipment(List<Attributes> attributesVs, MercadoProduct mercadoProduct, String attributeId) {
        Attributes attribute = new Attributes();
        if (attributeId.equals("PACKAGE_WEIGHT")) {
            attribute.setId("PACKAGE_WEIGHT");
        } else if (attributeId.equals("PACKAGE_LENGTH")) {
            attribute.setId("PACKAGE_LENGTH");
        } else if (attributeId.equals("PACKAGE_WIDTH")) {
            attribute.setId("PACKAGE_WIDTH");
        } else if (attributeId.equals("PACKAGE_HEIGHT")) {
            attribute.setId("PACKAGE_HEIGHT");
        }
        if (mercadoProduct.getShipType().equals(1L)) {
            if (attributeId.equals("PACKAGE_WEIGHT")) {
                attribute.setValue_name(oneSize.get(0));
            } else if (attributeId.equals("PACKAGE_LENGTH")) {
                attribute.setValue_name(oneSize.get(1));
            } else if (attributeId.equals("PACKAGE_WIDTH")) {
                attribute.setValue_name(oneSize.get(2));
            } else if (attributeId.equals("PACKAGE_HEIGHT")) {
                attribute.setValue_name(oneSize.get(3));
            }
        } else if (mercadoProduct.getShipType().equals(2L)) {
            if (attributeId.equals("PACKAGE_WEIGHT")) {
                attribute.setValue_name(twoSize.get(0));
            } else if (attributeId.equals("PACKAGE_LENGTH")) {
                attribute.setValue_name(twoSize.get(1));
            } else if (attributeId.equals("PACKAGE_WIDTH")) {
                attribute.setValue_name(twoSize.get(2));
            } else if (attributeId.equals("PACKAGE_HEIGHT")) {
                attribute.setValue_name(twoSize.get(3));
            }
        } else if (mercadoProduct.getShipType().equals(3L)) {
            if (attributeId.equals("PACKAGE_WEIGHT")) {
                attribute.setValue_name(threeSize.get(0));
            } else if (attributeId.equals("PACKAGE_LENGTH")) {
                attribute.setValue_name(threeSize.get(1));
            } else if (attributeId.equals("PACKAGE_WIDTH")) {
                attribute.setValue_name(threeSize.get(2));
            } else if (attributeId.equals("PACKAGE_HEIGHT")) {
                attribute.setValue_name(threeSize.get(3));
            }
        } else if (mercadoProduct.getShipType().equals(4L)) {
            if (attributeId.equals("PACKAGE_WEIGHT")) {
                attribute.setValue_name(fourSize.get(0));
            } else if (attributeId.equals("PACKAGE_LENGTH")) {
                attribute.setValue_name(fourSize.get(1));
            } else if (attributeId.equals("PACKAGE_WIDTH")) {
                attribute.setValue_name(fourSize.get(2));
            } else if (attributeId.equals("PACKAGE_HEIGHT")) {
                attribute.setValue_name(fourSize.get(3));
            }
        } else if (mercadoProduct.getShipType().equals(5L)) {
            if (attributeId.equals("PACKAGE_WEIGHT")) {
                attribute.setValue_name(fiveSize.get(0));
            } else if (attributeId.equals("PACKAGE_LENGTH")) {
                attribute.setValue_name(fiveSize.get(1));
            } else if (attributeId.equals("PACKAGE_WIDTH")) {
                attribute.setValue_name(fiveSize.get(2));
            } else if (attributeId.equals("PACKAGE_HEIGHT")) {
                attribute.setValue_name(fiveSize.get(3));
            }
        } else if (mercadoProduct.getShipType().equals(6L)) {
            if (attributeId.equals("PACKAGE_WEIGHT")) {
                attribute.setValue_name(sixSize.get(0));
            } else if (attributeId.equals("PACKAGE_LENGTH")) {
                attribute.setValue_name(sixSize.get(1));
            } else if (attributeId.equals("PACKAGE_WIDTH")) {
                attribute.setValue_name(sixSize.get(2));
            } else if (attributeId.equals("PACKAGE_HEIGHT")) {
                attribute.setValue_name(sixSize.get(3));
            }
        } else if (mercadoProduct.getShipType().equals(7L)) {
            if (attributeId.equals("PACKAGE_WEIGHT")) {
                attribute.setValue_name(elevenSize.get(0));
            } else if (attributeId.equals("PACKAGE_LENGTH")) {
                attribute.setValue_name(elevenSize.get(1));
            } else if (attributeId.equals("PACKAGE_WIDTH")) {
                attribute.setValue_name(elevenSize.get(2));
            } else if (attributeId.equals("PACKAGE_HEIGHT")) {
                attribute.setValue_name(elevenSize.get(3));
            }
        } else if (mercadoProduct.getShipType().equals(8L)) {
            if (attributeId.equals("PACKAGE_WEIGHT")) {
                attribute.setValue_name(eightSize.get(0));
            } else if (attributeId.equals("PACKAGE_LENGTH")) {
                attribute.setValue_name(eightSize.get(1));
            } else if (attributeId.equals("PACKAGE_WIDTH")) {
                attribute.setValue_name(eightSize.get(2));
            } else if (attributeId.equals("PACKAGE_HEIGHT")) {
                attribute.setValue_name(eightSize.get(3));
            }
        } else if (mercadoProduct.getShipType().equals(9L)) {
            if (attributeId.equals("PACKAGE_WEIGHT")) {
                attribute.setValue_name(nineSize.get(0));
            } else if (attributeId.equals("PACKAGE_LENGTH")) {
                attribute.setValue_name(nineSize.get(1));
            } else if (attributeId.equals("PACKAGE_WIDTH")) {
                attribute.setValue_name(nineSize.get(2));
            } else if (attributeId.equals("PACKAGE_HEIGHT")) {
                attribute.setValue_name(nineSize.get(3));
            }
        } else if (mercadoProduct.getShipType().equals(10L)) {
            if (attributeId.equals("PACKAGE_WEIGHT")) {
                attribute.setValue_name(tenSize.get(0));
            } else if (attributeId.equals("PACKAGE_LENGTH")) {
                attribute.setValue_name(tenSize.get(1));
            } else if (attributeId.equals("PACKAGE_WIDTH")) {
                attribute.setValue_name(tenSize.get(2));
            } else if (attributeId.equals("PACKAGE_HEIGHT")) {
                attribute.setValue_name(tenSize.get(3));
            }
        } else if (mercadoProduct.getShipType().equals(11L)) {
            if (attributeId.equals("PACKAGE_WEIGHT")) {
                attribute.setValue_name(elevenSize.get(0));
            } else if (attributeId.equals("PACKAGE_LENGTH")) {
                attribute.setValue_name(elevenSize.get(1));
            } else if (attributeId.equals("PACKAGE_WIDTH")) {
                attribute.setValue_name(elevenSize.get(2));
            } else if (attributeId.equals("PACKAGE_HEIGHT")) {
                attribute.setValue_name(elevenSize.get(3));
            }
        } else if (mercadoProduct.getShipType().equals(12L)) {
            if (attributeId.equals("PACKAGE_WEIGHT")) {
                attribute.setValue_name(twelveSize.get(0));
            } else if (attributeId.equals("PACKAGE_LENGTH")) {
                attribute.setValue_name(twelveSize.get(1));
            } else if (attributeId.equals("PACKAGE_WIDTH")) {
                attribute.setValue_name(twelveSize.get(2));
            } else if (attributeId.equals("PACKAGE_HEIGHT")) {
                attribute.setValue_name(twelveSize.get(3));
            }
        } else if (mercadoProduct.getShipType().equals(13L)) {
            if (attributeId.equals("PACKAGE_WEIGHT")) {
                attribute.setValue_name(thirteenSize.get(0));
            } else if (attributeId.equals("PACKAGE_LENGTH")) {
                attribute.setValue_name(thirteenSize.get(1));
            } else if (attributeId.equals("PACKAGE_WIDTH")) {
                attribute.setValue_name(thirteenSize.get(2));
            } else if (attributeId.equals("PACKAGE_HEIGHT")) {
                attribute.setValue_name(thirteenSize.get(3));
            }
        }
        attributesVs.add(attribute);
    }

}
