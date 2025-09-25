package com.smarterp.common.core.utils.mercado;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class MercadoNewHttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(MercadoNewHttpUtil.class);

    /**
     * Multiget with multiple numbers of items.
     * 参数可以是全局产品ID 例如：CBT2325875190
     * 也可以是站点产品ID 例如：MLM2611604710
     * include_attributes=all 表示查询所有的属性
     */
    private static final String GET_PRODUCT_INFO_URL = "https://api.mercadolibre.com/items?include_attributes=all&ids=$ITEM_ID1";

    /**
     * 查询产品信息(多个参数)
     * 备注：可以是自己店铺的accesstoken查询别人的产品信息
     *
     * @param ids
     * @param accessToken
     * @return
     */
    public static JSONArray getProductInfo(List<String> ids, String accessToken) {
        JSONArray jsonArray = new JSONArray();
        try {
            String productId = ids.stream().collect(Collectors.joining(","));
            String url = GET_PRODUCT_INFO_URL.replace("$ITEM_ID1", productId);
            String body = OkHttpUtils.builder().url(url).addHeader("Authorization", "Bearer " + accessToken).get().sync();
            logger.info("查询多个产品信息响应信息 {}", body);
            if (body != null) {
                JSONArray response = JSON.parseArray(body);
                for (int i = 0; i < response.size(); i++) {
                    JSONObject item = response.getJSONObject(i);
                    jsonArray.add(item.getJSONObject("body"));
                }
            }
        } catch (Exception e) {
            logger.error("查询多个产品信息异常 {}", e);
        }
        return jsonArray;
    }

    /**
     * 查询产品信息(单个参数)
     * 备注：可以是自己店铺的accesstoken查询别人的产品信息
     *
     * @param productId
     * @param accessToken
     * @return
     */
    public static JSONObject getProductInfo(String productId, String accessToken) {
        JSONObject jsonObject = new JSONObject();
        try {
            String url = GET_PRODUCT_INFO_URL.replace("$ITEM_ID1", productId);
            String body = OkHttpUtils.builder().url(url).addHeader("Authorization", "Bearer " + accessToken).get().sync();
            logger.info("查询单个产品信息响应信息 {}", body);
            if (body != null) {
                JSONArray jsonArray = JSON.parseArray(body);
                jsonObject = jsonArray.getJSONObject(0).getJSONObject("body");
            }
        } catch (Exception e) {
            logger.error("查询单个产品信息异常 {}", e);
        }
        return jsonObject;
    }

    /**
     * 获取站点产品信息 只能使用自己的accessToken获取自己的产品信息
     *
     * @param siteProductId
     * @param accessToken
     * @return
     */
    public static JSONObject getSiteProductInfo(String siteProductId, String accessToken) {
        JSONObject jsonObject = new JSONObject();
        try {
            String url = "https://api.mercadolibre.com/marketplace/items/" + siteProductId;
            String body = OkHttpUtils.builder().url(url)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .get()
                    .sync();
            jsonObject = JSON.parseObject(body);
        } catch (Exception e) {
            logger.error("获取站点产品信息异常 {}", e);
        }
        return jsonObject;
    }

    /**
     * 获取全局产品信息 只能使用自己的accessToken获取自己的产品信息
     *
     * @param globalProductId
     * @param accessToken
     * @return
     */
    public static JSONObject getGlobalProductInfo(String globalProductId, String accessToken) {
        JSONObject jsonObject = new JSONObject();
        try {
            String url = "https://api.mercadolibre.com/marketplace/items/" + globalProductId;
            String body = OkHttpUtils.builder().url(url)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .get()
                    .sync();
            jsonObject = JSON.parseObject(body);
        } catch (Exception e) {
            logger.error("获取全局产品信息异常 {}", e);
        }
        return jsonObject;
    }

    public static void main(String[] args) {
        String siteId = "MLM2204355335";
        String accessToken = "APP_USR-XXXXXXXXX-041303-86f04f46a256eff729d31b8aad527be4-1051935981";
        JSONObject siteProductInfo = getSiteProductInfo(siteId, accessToken);
        System.out.println(siteProductInfo);

    }


}
