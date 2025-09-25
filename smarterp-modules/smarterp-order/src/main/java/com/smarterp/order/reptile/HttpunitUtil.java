package com.smarterp.order.reptile;


import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import org.htmlunit.BrowserVersion;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HttpunitUtil {

    public static void main(String[] args) throws Exception {
        Document document = Jsoup.connect("https://detail.1688.com/offer/652773998266.html?spm=a360q.7751291.0.0.7c2329a8VQgFK7&sk=common").get();
        Element body = document.body();
        Elements elementsByClass = body.getElementsByClass("detail-gallery-turn-wrapper");

        for (Element byClass : elementsByClass) {
            String attr = byClass.getElementsByTag("img").first().attr("src");
            System.out.println(attr.trim());

        }
    }
}
