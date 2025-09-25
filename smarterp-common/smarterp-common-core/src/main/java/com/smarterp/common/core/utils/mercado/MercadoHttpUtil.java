package com.smarterp.common.core.utils.mercado;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MercadoHttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(MercadoHttpUtil.class);

    /**
     * 获取站点产品描述url
     */
    private static final String GET_DES_URL = "marketplace/items/$ITEM_ID/description?site_id=$SITE_ID&logistic_type=$LOGISTIC_TYPE";

    /**
     * 上传站点产品描述url
     */
    private static final String createDes_URL = "https://api.mercadolibre.com/marketplace/items/$ITEM_ID/description";

    /**
     * 应用CLIENT_ID
     */
    private static final String CLIENT_ID = "2910507433773152";

    /**
     * 应用CLIENT_SECRET
     */
    private static final String CLIENT_SECRET = "r0wngzHOCAQE67kv88PZGg7FTc6laBG2";

    /**
     * 获取accessToken url
     */
    private static final String GET_TOKEN_URL = "https://api.mercadolibre.com/oauth/token";

    /**
     * 店铺授权后跳转地址(绑定店铺地址)
     */
    private static final String REDICRET_RUI = "https://amz.eboat.top/mercado/shop";

    /**
     * 获取站点产品ID和全局产品ID映射关系url
     */
    private static final String GET_MARKET_PLACE_ITEMS_URL = "https://api.mercadolibre.com/items/$ITEM_ID/marketplace_items";

    /**
     * 获取全局产品ID或者站点产品ID详情url
     */
    private static final String GET_PRODUCT_INFO_URL = "https://api.mercadolibre.com/items/";

    /**
     * 接口请求连接超时时间
     */
    private static final int connectionTimeout = 20000;

    /**
     * 接口请求读取超时时间
     */
    private static final int readTimeout = 20000;

    /**
     * 接口请求写入超时时间
     */
    private static final int writeTimeout = 20000;

    /**
     * 获取accessToken
     *
     * @param code 店铺登录授权成功后跳转地址链接后的参数
     *             通过code能获取到refresh_token和access_token
     * @return
     */
     /*{
        "access_token": "APP_USR-2271900076226075-042006-efebfbe361f1b5655433f9e40c4b7fb2-1237419550", // token
            "token_type": "Bearer",
            "expires_in": 21600,// token有效期21600秒
            "scope": "offline_access read write",
            "user_id": 1237419550, // 商家id
            "refresh_token": "TG-641b0e5f289e4d00013adbbf-1237419550" // 刷新token
    }*/
    public static JSONObject getAccessToken(String code) {
        logger.info("获取accessToken参数 {}", code);
        JSONObject jsonObject = new JSONObject();
        try {
            // post请求，分为两种，一种是普通表单提交，一种是json提交
            String body = OkHttpUtils.builder()
                    .url(GET_TOKEN_URL)
                    // 有参数的话添加参数，可多个
                    .addParam("grant_type", "authorization_code")
                    .addParam("client_id", CLIENT_ID)
                    .addParam("client_secret", CLIENT_SECRET)
                    .addParam("code", code)
                    .addParam("redirect_uri", REDICRET_RUI)
                    // 也可以添加多个
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    // 如果是true的话，会类似于postman中post提交方式的raw，用json的方式提交，不是表单
                    // 如果是false的话传统的表单提交
                    .post(true)
                    .sync();
            logger.info("获取accessToken响应信息 requestbody {}", body);
            if (body != null) {
                jsonObject = JSON.parseObject(body);
            }
        } catch (Exception e) {
            logger.error("获取accessToken异常 {}", e);
        }
        return jsonObject;
    }

    /**
     * 刷新refreshToken
     *
     * @param refreshToken
     * @return
     */
    /*{
        "access_token": "APP_USR-2271900076226075-042006-efebfbe361f1b5655433f9e40c4b7fb2-1237419550", // token
            "token_type": "Bearer",
            "expires_in": 21600,// token有效期21600秒
            "scope": "offline_access read write",
            "user_id": 1237419550, // 商家id
            "refresh_token": "TG-641b0e5f289e4d00013adbbf-1237419550" // 刷新token
    }*/
    public static JSONObject getRefreshToken(String refreshToken) {
        logger.info("刷新refreshToken {}", refreshToken);
        JSONObject jsonObject = new JSONObject();
        try {
            String body = OkHttpUtils.builder().url(GET_TOKEN_URL)
                    // 有参数的话添加参数，可多个
                    .addParam("grant_type", "refresh_token")
                    .addParam("client_id", CLIENT_ID)
                    .addParam("client_secret", CLIENT_SECRET)
                    .addParam("refresh_token", refreshToken)
                    // 也可以添加多个
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    // 如果是true的话，会类似于postman中post提交方式的raw，用json的方式提交，不是表单
                    // 如果是false的话传统的表单提交
                    .post(true)
                    .sync();
            logger.info("刷新refreshToken响应信息 requestbody {}", body);
            if (body != null) {
                jsonObject = JSON.parseObject(body);
            }
        } catch (Exception e) {
            logger.error("刷新accessToken异常 {}", e);
        }
        return jsonObject;
    }

    /**
     * 获取店铺信息
     *
     * @param accessToken
     * @return
     */
   /* {
        "merchantId": "1237419550",
            "mercadoShopName": "CNMOONSHOP2022",
            "aliasName": "MoonShop2022"
    }*/
    public static JSONObject getShopInfo(String accessToken) {
        logger.info("获取店铺信息参数 accessToken {}", accessToken);
        JSONObject jsonObject = new JSONObject();
        try {
            String body = OkHttpUtils.builder().url("https://api.mercadolibre.com/users/me")
                    // 也可以添加多个
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .get()
                    // 可选择是同步请求还是异步请求
                    .sync();
            if (body != null) {
                logger.info("获取店铺信息响应信息 {}", body);
                JSONObject object = JSON.parseObject(body);
                if (object != null) {
                    jsonObject.put("merchantId", object.getString("id"));
                    jsonObject.put("mercadoShopName", object.getString("nickname"));
                    jsonObject.put("aliasName", object.getString("first_name"));
                }
            }
        } catch (Exception e) {
            logger.error("获取店铺信息异常 {}", e);
        }
        return jsonObject;
    }

    /**
     * 获取店铺开通了哪几个站点信息
     *
     * @param accessToken
     * @return
     */
    /*[
        {
            "user_id": 1237421300,
                "site_id": "MLB",
                "logistic_type": "remote",
                "quota": 15000,
                "total_items": 0
        },
        {
            "user_id": 1237424827,
                "site_id": "MLC",
                "logistic_type": "remote",
                "quota": 15000,
                "total_items": 0
        },
        {
            "user_id": 1237421301,
                "site_id": "MCO",
                "logistic_type": "remote",
                "quota": 15000,
                "total_items": 0
        },
        {
            "user_id": 1237424825,
                "site_id": "MLM",
                "logistic_type": "remote",
                "quota": 15000,
                "total_items": 345
        },
        {
            "user_id": 1237421343,
                "site_id": "MLM",
                "logistic_type": "fulfillment",
                "quota": 50000,
                "total_items": 7
        }
    ]*/
    public static JSONArray getShopSiteInfo(String accessToken) {
        JSONArray jsonArray = new JSONArray();
        try {
            String body = OkHttpUtils.builder().url("https://api.mercadolibre.com/marketplace/users/cap")
                    // 也可以添加多个
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .get()
                    // 可选择是同步请求还是异步请求
                    .sync();
            logger.info("获取店铺站点信息响应信息 {}", body);
            if (body != null) {
                jsonArray = JSONArray.parse(body);
            }
        } catch (Exception e) {
            logger.error("查询店铺站点信息异常 {}", e);
        }
        return jsonArray;
    }

    /**
     * 获取全局产品ID和站点产品ID映射关系
     * 如果输入的是站点ID，则响应结果是全局ID
     * 如果输入的全局ID，则响应的所有的站点ID
     *
     * @param globalProductIdOrItemId 全局产品ID或者站点产品ID
     * @param accessToken             accessToken
     * @return
     */
    /*{
        "item_id": "MLM1806757177",
         "user_id": 1237424825,
         "site_id": "MLM",
         "date_created": "2023-02-07T05:38:27.709Z",
         "parent_id": "CBT1597532561",
         "parent_user_id": 1237419550
    }*/
    public static JSONObject getGlobalAndSiteRelationships(String globalProductIdOrItemId, String accessToken) {
        logger.info("获取全局产品ID和站点产品ID映射关系参数 globalProductIdOrItemId {} accessToken {}", globalProductIdOrItemId, accessToken);
        JSONObject jsonObject = new JSONObject();
        try {
            String body = OkHttpUtils.builder().url(GET_MARKET_PLACE_ITEMS_URL.replace("$ITEM_ID", globalProductIdOrItemId))
                    // 也可以添加多个
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .get()
                    // 可选择是同步请求还是异步请求
                    .sync();
            logger.info("获取全局产品ID和站点产品ID映射关系响应信息 {}", body);
            if (body != null) {
                jsonObject = JSON.parseObject(body);
            }
        } catch (Exception e) {
            logger.error("获取全局产品ID和站点产品ID映射关系异常 {} 产品ID {}", e, globalProductIdOrItemId);
        }
        return jsonObject;
    }

    /**
     * 获取全局产品详情
     *
     * @param globalProductId
     * @return
     */
    public static JSONObject getGlobalProductInfo(String globalProductId, String accessToken) {
        JSONObject jsonObject = new JSONObject();
        try {
            String url = GET_PRODUCT_INFO_URL + globalProductId + "?include_attributes=all";
            logger.info("获取全局产品详情请求地址 {}", url);
            String body = OkHttpUtils.builder().url(url)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .get()
                    .sync();
            logger.info("获取全局产品详情响应信息 {}", body);
            if (body != null) {
                jsonObject = JSON.parseObject(body);
            }
        } catch (Exception e) {
            logger.error("获取全局产品信息异常 {} globalProductId {}", e, globalProductId);
        }
        return jsonObject;
    }

    /**
     * 获取站点产品详情
     *
     * @param itemId
     * @return
     */
    public static JSONObject getItemInfo(String itemId, String accessToken) {
        JSONObject jsonObject = new JSONObject();
        try {
            String url = GET_PRODUCT_INFO_URL + itemId + "?include_attributes=all";
            logger.info("获取站点产品详情请求地址 {}", url);
            String body = OkHttpUtils.builder().url(url)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .get()
                    .sync();
            logger.info("获取站点产品详情响应信息 {}", body);
            if (body != null) {
                jsonObject = JSON.parseObject(body);
            }
        } catch (Exception e) {
            logger.error("获取站点产品详情异常 {} itemId {}", e, itemId);
        }
        return jsonObject;
    }

    /**
     * 获取站点产品详情
     *
     * @param itemId
     * @return
     */
    public static JSONObject getItemInfo(String itemId, String includeAttributes, String accessToken) {
        JSONObject jsonObject = new JSONObject();
        try {
            String url = GET_PRODUCT_INFO_URL + itemId + "?" + includeAttributes;
            logger.info("获取站点产品详情请求地址 {}", url);
            String body = OkHttpUtils.builder().url(url)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .get()
                    .sync();
            logger.info("获取站点产品详情响应信息 {}", body);
            if (body != null) {
                jsonObject = JSON.parseObject(body);
            }
        } catch (Exception e) {
            logger.error("获取站点产品详情异常 {} itemId {}", e, itemId);
        }
        return jsonObject;
    }

    /**
     * 上传图片
     *
     * @param token
     * @param url
     * @param file
     * @return
     */
    public static JSONObject uploadImage(String token, String url, File file) {
        HttpRequest post = HttpUtil.createPost(url);
        post.setConnectionTimeout(connectionTimeout);
        post.setReadTimeout(readTimeout);
        String res = post.header("Authorization", "Bearer " + token).form("file", file).execute().body();
        return JSONObject.parseObject(res);
    }

    /**
     * 根据全局产品id和站点id查看站点的产品说明
     *
     * @param accessToken
     * @param url
     * @param cbt
     * @return
     */
    public static JSONObject getDesByCbtId(String accessToken, String url, String cbt, String siteId, String logisticType) {
        logger.error("获取产品描述参数 accessToken {} url {} cbt {} siteId {} logisticType {}", accessToken, url, cbt, siteId, logisticType);
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + accessToken);
            String getUrl = "";
            if (logisticType != null) {
                if (logisticType.equals("fulfillment")) {
                    getUrl = url + GET_DES_URL.replace("$ITEM_ID", cbt).replace("$SITE_ID", siteId).replace("$LOGISTIC_TYPE", "fulfillment");
                } else {
                    getUrl = url + GET_DES_URL.replace("$ITEM_ID", cbt).replace("$SITE_ID", siteId).replace("$LOGISTIC_TYPE", "remote");
                }
            } else {
                getUrl = url + GET_DES_URL.replace("$ITEM_ID", cbt).replace("$SITE_ID", siteId).replace("$LOGISTIC_TYPE", "remote");
            }
            String itemResult = HttpUtil.createGet(getUrl).setConnectionTimeout(connectionTimeout).setReadTimeout(readTimeout).addHeaders(headers).execute().body();
            Document doc = Jsoup.parse(itemResult);
            String content = doc.body().text();
            String result = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
            logger.info("获取站点产品说明:{}", result);
            return JSON.parseObject(result);
        } catch (Exception e) {
            logger.error("获取全局产品id和站点id查看站点的产品说明失败:{},{}", e, cbt);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传图片
     *
     * @param urlImage
     * @param list
     * @param token
     * @param mercadoUrl
     */
    public static void urlHandleImages(String urlImage, List<Map<String, Object>> list, String token, String mercadoUrl) {
        HttpRequest get = HttpUtil.createGet(urlImage);
        String fileId = IdUtil.simpleUUID() + ".jpg";
        File fileNew = new File(fileId);
        try (InputStream inputStream = get.execute().bodyStream()) {
            FileUtil.writeFromStream(inputStream, fileNew);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject json = uploadImage(token, mercadoUrl, fileNew);
            Map<String, Object> map = new HashMap<>();
            map.put("id", json.getString("id"));
            JSONArray variations = json.getJSONArray("variations");
            String urlIamge = "";
            for (Object variation : variations) {
                JSONObject jsonb = (JSONObject) variation;
                if (jsonb.getString("secure_url").contains("-O.jpg")) {
                    urlIamge = jsonb.getString("secure_url");
                }
            }
            map.put("url", urlIamge);
            list.add(map);
        } catch (Exception e) {
            logger.error("上传文件失败:", e);
        } finally {
            fileNew.delete();
        }
    }

    /**
     * 发送站点描述
     *
     * @param accessToken
     * @param cbtProId
     * @param des
     */
    public static JSONObject createDes(String accessToken, String cbtProId, String des, String siteId, String logisticType) {
        JSONObject result = null;
        try {
            HttpRequest post = HttpUtil.createPost(createDes_URL.replace("$ITEM_ID", cbtProId));
            post.setConnectionTimeout(connectionTimeout);
            post.setReadTimeout(readTimeout);
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + accessToken);
            Map<String, Object> map = new HashMap<>();
            map.put("site_id", siteId);
            map.put("logistic_type", logisticType);
            map.put("plain_text", des);
            logger.info("上传产品描述url:{}", JSON.toJSONString(createDes_URL.replace("$ITEM_ID", cbtProId)));
            logger.info("上传产品描述参数:{}", JSON.toJSONString(map));
            String res = post.addHeaders(headers).body(JSONObject.toJSONString(map)).execute().body();
            if (res != null) {
                result = JSONObject.parseObject(res);
            }
            logger.info("上传产品描述返回:{}", res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取尺码表
     *
     * @param object
     * @return
     */
    public static JSONObject getSizeChart(JSONObject object, String accessToken) {
        JSONObject jsonObject = new JSONObject();
        try {
            HttpRequest post = HttpUtil.createPost("https://api.mercadolibre.com/catalog/charts/search");
            // 设置超时时间
            post.setConnectionTimeout(readTimeout);
            post.setReadTimeout(readTimeout);
            // 请求头参数
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + accessToken);
            String body = post.addHeaders(headers).body(JSON.toJSONString(object)).execute().body();
            Document doc = Jsoup.parse(body);
            String content = doc.body().text();
            System.out.println(content);
            String result = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
            jsonObject = JSON.parseObject(result);
        } catch (Exception e) {
            logger.error("获取尺码表失败:{}", e);
        }
        return jsonObject;
    }

    /**
     * 获取订单数据
     *
     * @param offset      偏移量
     * @param limit       数量
     * @param accessToken accessToken
     * @return
     */
    public static JSONObject getMercadoOrderInfo(Integer offset, Integer limit, String accessToken) {
        JSONObject jsonObject = new JSONObject();
        try {
            // 获取当前日期
            LocalDate currentDate = LocalDate.now();
            // 计算两个月前的日期
            LocalDate twoMonthsAgo = currentDate.minusMonths(2);
            // 定义日期格式化器
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // 格式化日期
            String currentFormattedDate = currentDate.format(formatter);
            String twoMonthsAgoFormattedDate = twoMonthsAgo.format(formatter);
            // 输出结果
            System.out.println("当前日期：" + currentFormattedDate);
            System.out.println("两个月之前的日期：" + twoMonthsAgoFormattedDate);
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + accessToken);
            String url = "https://api.mercadolibre.com/marketplace/orders/search?offset=" + offset + "&limit=" + limit + "&date_created.from=" + twoMonthsAgoFormattedDate + "&date_created.to=" + currentFormattedDate;
            logger.info("获取订单信息url地址 {}", url);
            HttpRequest get = HttpUtil.createGet(url);
            // 设置超时时间
            get.setConnectionTimeout(connectionTimeout);
            get.setReadTimeout(readTimeout);
            String body = get.addHeaders(headers).execute().body();
            Document doc = Jsoup.parse(body);
            String content = doc.body().text();
            String result = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
            jsonObject = JSON.parseObject(result);
        } catch (Exception e) {
            logger.error("获取订单异常 {}", e);
        }
        return jsonObject;
    }

    /**
     * 根据订单id获取订单详情
     *
     * @param accessToken
     * @param orderId
     * @return
     */
    public static JSONObject getMercadoInfoByOrderId(String accessToken, String orderId) {
        JSONObject jsonObject = new JSONObject();
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + accessToken);
            String url = "https://api.mercadolibre.com/marketplace/orders/" + orderId;
            logger.info("获取订单详情url地址 {}", url);
            HttpRequest get = HttpUtil.createGet(url);
            // 设置超时时间
            get.setConnectionTimeout(connectionTimeout);
            get.setReadTimeout(readTimeout);
            String body = get.addHeaders(headers).execute().body();
            Document doc = Jsoup.parse(body);
            String content = doc.body().text();
            String result = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
            jsonObject = JSON.parseObject(result);
        } catch (Exception e) {
            logger.error("获取订单详情异常 {}", e);
        }
        return jsonObject;
    }

    /**
     * 根据物流id获取物流详情
     *
     * @param accessToken
     * @param shipmentId
     * @return
     */
    public static JSONObject getShipDetailByShipId(String accessToken, String shipmentId) {
        JSONObject jsonObject = new JSONObject();
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + accessToken);
            String url = "https://api.mercadolibre.com/marketplace/shipments/" + shipmentId;
            logger.info("获取物流详情url地址 {}", url);
            HttpRequest get = HttpUtil.createGet(url);
            // 设置超时时间
            get.setConnectionTimeout(connectionTimeout);
            get.setReadTimeout(readTimeout);
            String body = get.addHeaders(headers).execute().body();
            Document doc = Jsoup.parse(body);
            String content = doc.body().text();
            String result = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
            jsonObject = JSON.parseObject(result);
        } catch (Exception e) {
            logger.error("获取物流详情异常 {}", e);
        }
        return jsonObject;
    }

    public static void main(String[] args) {
        String itemId = "CBT1835794112";
        String accessToken = "APP_USR-XXXXXXXXX-041208-1a2cf40f114484b9527fe7dd48281a88-1237419550";
        JSONObject itemInfoContainSoldQuantity = getItemInfoContainSoldQuantity(itemId, accessToken);
        System.out.println(itemInfoContainSoldQuantity);


    }

    /**
     * 根据站点产品ID获取站点详情(包括销量数据)
     *
     * @param itemId
     * @param accessToken
     * @return
     */
    public static JSONObject getItemInfoContainSoldQuantity(String itemId, String accessToken) {
        JSONObject jsonObject = new JSONObject();
        try {
            String url = "https://api.mercadolibre.com/marketplace/items/" + itemId;
            String body = OkHttpUtils.builder().url(url)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .get()
                    .sync();
            jsonObject = JSON.parseObject(body);
        } catch (Exception e) {
            logger.error("获取产品销量异常 {}", e);
        }
        return jsonObject;
    }

    /**
     * 获取实时汇率
     *
     * @return
     */
    public static JSONObject getRealTimeExchangeRates() {
        JSONObject jsonObject = new JSONObject();
        try {
            String url = "https://tsanghi.com/api/fin/forex/daily?token=6615f5455f464323883e51b08a8c72ef&ticker=USDMXN&limit=1&order=2";
            HttpRequest get = HttpUtil.createGet(url);
            // 设置超时时间
            get.setConnectionTimeout(connectionTimeout);
            get.setReadTimeout(readTimeout);
            String body = get.execute().body();
            jsonObject = JSON.parseObject(body);
        } catch (Exception e) {
            logger.error("获取实时汇率失败 {}", e);
        }
        return jsonObject;
    }


    /**
     * 根据店铺id查询所有的全局产品ID
     *
     * @param merchantId
     * @param accessToken
     * @param
     * @return
     */
    public static JSONObject getShopGlobalProductId(String merchantId, String accessToken, String scrollId) {
        JSONObject jsonObject = new JSONObject();
        try {
            String url = "";
            if (scrollId != null) {
                url = "https://api.mercadolibre.com/users/" + merchantId + "/items/search?search_type=scan&limit=100&scroll_id=" + scrollId;
            } else {
                url = "https://api.mercadolibre.com/users/" + merchantId + "/items/search?search_type=scan&limit=100";
            }
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + accessToken);
            HttpRequest get = HttpUtil.createGet(url);
            get.setConnectionTimeout(connectionTimeout);
            get.setReadTimeout(readTimeout);
            String body = get.addHeaders(headers).execute().body();
            Document doc = Jsoup.parse(body);
            String content = doc.body().text();
            logger.info("根据店铺id查询所有的全局产品ID {}", content);
            String result = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
            jsonObject = JSON.parseObject(result);
        } catch (Exception e) {
            logger.error("查询所有的全局产品ID异常 {}", e);
        }
        return jsonObject;
    }

    /**
     * 根据产品链接提取出其中的产品ID
     * 例如MLM-2159667866 MLB-2159667866 MLC-2159667866 MCO-2159667866
     *
     * @param productUrl
     * @return
     */
    public static String getItemIdByProductUrl(String productUrl) {
        String itemId = null;
        try {
            // 定义正则表达式
            String regex = "(MLM|MLB|MLC|MCO)-\\d+";
            // 编译正则表达式
            Pattern pattern = Pattern.compile(regex);
            // 创建匹配器
            Matcher matcher = pattern.matcher(productUrl);
            // 查找匹配项并输出
            while (matcher.find()) {
                itemId = matcher.group();
            }
        } catch (Exception e) {
            logger.error("输入的产品链接有误 {}", productUrl);
        }
        if (itemId != null) {
            itemId = itemId.replace("-", "");
        }
        return itemId;
    }

    public static String searchResult(String siteId, String sellId, String accessToken) {
      //  String url = "https://api.mercadolibre.com/sites/" + siteId + "/search?seller_id=" + sellId+"&category=CBT109027";
       // String url = "https://api.mercadolibre.com/sites/"+siteId+"/search?category=MLM16117";
        String url = "https://api.mercadolibre.com/sites/"+siteId+"/search?category=MLM6585&logistic_type=fulfillment&offset=50&limit=50&q=nike";


        System.out.println(url);
        String body = OkHttpUtils.builder().url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .sync();
        return body;
    }

}
