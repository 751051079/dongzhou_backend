package com.smarterp.order.tools;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author juntao.li
 * @ClassName CrawlingUtil
 * @description 爬取数据工具类
 * @date 2023/12/14 20:22
 * @Version 1.0
 */
public class CrawlingUtil {

    private final static Logger logger = LoggerFactory.getLogger(CrawlingUtil.class);

    /**
     * 根据店铺链接获取所有产品的url
     *
     * @param shopUrl
     * @return
     */
    public static Set<String> getMercadoProductUrl(String shopUrl) {
        Set<String> urlList = new HashSet<>();
        try {
            HttpRequest get = HttpUtil.createGet(shopUrl);
            get.setConnectionTimeout(20000);
            get.setReadTimeout(20000);
            String body = get.execute().body();
            if (body != null) {
                Pattern pattern = Pattern.compile("MLM\\d{10}");
                Matcher matcher = pattern.matcher(body);

                while (matcher.find()) {
                    urlList.add(matcher.group());
                }
            }
        } catch (Exception e) {
            logger.error("获取页面数据失败");
        }
        return urlList;
    }

}
