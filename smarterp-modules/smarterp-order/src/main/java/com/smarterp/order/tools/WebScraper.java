package com.smarterp.order.tools;

import com.alibaba.fastjson2.JSON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebScraper {

    /**
     * 爬取指定网页的所有文本内容
     * @param url 目标网页的 URL
     * @return 网页中的所有文本内容
     * @throws IOException 如果访问网页失败
     */
    public static String fetchWebContent(String url) throws IOException {
        // 使用 Jsoup 连接并解析网页
        Document document = Jsoup.connect(url).get();

        Element body = document.body();
        System.out.println(body);

        // 获取网页的所有文本内容
        return document.body().text(); // 获取整个网页的文本内容
    }

    /**
     * 爬取网页的所有链接
     * @param url 目标网页的 URL
     * @return 网页中的所有链接
     * @throws IOException 如果访问网页失败
     */
    public static List<String> fetchLinks(String url) throws IOException {
        List<String> links = new ArrayList<>();

        // 使用 Jsoup 连接并解析网页
        Document document = Jsoup.connect(url).get();

        // 获取网页中的所有<a>标签
        Elements linkElements = document.select("a[href]"); // 选择所有包含链接的 <a> 标签

        // 提取每个链接并添加到列表中
        for (Element element : linkElements) {
            String link = element.attr("href");
            links.add(link);
        }

        return links;
    }

    /**
     * 爬取网页的所有图片链接
     * @param url 目标网页的 URL
     * @return 网页中的所有图片链接
     * @throws IOException 如果访问网页失败
     */
    public static List<String> fetchImages(String url) throws IOException {
        List<String> imageLinks = new ArrayList<>();

        // 使用 Jsoup 连接并解析网页
        Document document = Jsoup.connect(url).get();

        // 获取网页中的所有<img>标签
        Elements imgElements = document.select("img[src]"); // 选择所有包含图片链接的 <img> 标签

        // 提取每个图片链接并添加到列表中
        for (Element element : imgElements) {
            String imgSrc = element.attr("src");
            imageLinks.add(imgSrc);
        }

        return imageLinks;
    }

    public static void main(String[] args) {
        String url = "https://articulo.mercadolibre.com.mx/MLM-2611604710-camuflaje-militar-uniforme-traje-tactico-caza-al-aire-libre-_JM"; // 你要爬取的网页 URL

        try {
            // 爬取并输出网页文本内容
            String content = fetchWebContent(url);
            System.out.println("网页内容：\n" + content);

            // 爬取并输出网页中的所有链接
           /* List<String> links = fetchLinks(url);
            System.out.println("网页中的所有链接：");
            for (String link : links) {
                System.out.println(link);
            }*/

            // 爬取并输出网页中的所有图片链接
          /*  List<String> images = fetchImages(url);
            System.out.println("网页中的所有图片链接：");
            for (String image : images) {
                System.out.println(image);
            }*/

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

