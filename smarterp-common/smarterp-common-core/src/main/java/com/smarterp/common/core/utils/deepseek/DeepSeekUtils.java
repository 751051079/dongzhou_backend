package com.smarterp.common.core.utils.deepseek;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * deepseek工具类
 */
public class DeepSeekUtils {

    private static final String API_NAMES = "mercado-dev";
    private static final String API_KEY = "sk-2c2209ba42b04668af4e2acd0b271819";
    private static final String BASE_URL = "https://api.deepseek.com";
    private static final String MODEL = "deepseek-chat";
    private static final String CONTENT_TEMPLATE = "itemUrl 这是墨西哥电商平台美客多的一个产品链接，请帮我判断一下这个产品是否有可能侵权？只需回答：不会侵权 可能侵权 一定侵权  并且给出判断依据";
    /**
     * 接口请求连接超时时间
     */
    private static final int connectionTimeout = 20000;

    /**
     * 接口请求读取超时时间
     */
    private static final int readTimeout = 20000;

    /**
     * deepseek对话模型
     *
     * @param itemUrl
     * @return
     */
    public static JSONObject sendMessage(String itemUrl) {

        String content = CONTENT_TEMPLATE.replaceAll("itemUrl", itemUrl);

        // 请求参数
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", MODEL);
        jsonObject.put("stream", false);

        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", content);
        JSONArray messages = new JSONArray();
        messages.add(message);
        jsonObject.put("messages", messages);
        // 请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + API_KEY);
        headers.put("Content-Type", "application/json");

        HttpRequest post = HttpUtil.createPost(BASE_URL + "/chat/completions");
        post.setConnectionTimeout(connectionTimeout);
        post.setReadTimeout(readTimeout);
        String body = post.addHeaders(headers).body(JSON.toJSONString(jsonObject)).execute().body();
        return JSON.parseObject(body);
    }

}
