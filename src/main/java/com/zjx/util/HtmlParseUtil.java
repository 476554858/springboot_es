package com.zjx.util;

import com.zjx.entity.es.JDPojo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HtmlParseUtil {

    public static void main(String[] args)throws Exception {
        String url = "https://search.jd.com/Search?keyword=java";
        //解析网页
        Document document = Jsoup.parse(new URL(url), 30000);
        Element element = document.getElementById("J_goodsList");
        System.out.println(element.html());
        Elements elements = element.getElementsByTag("li");
        for(Element el : elements){
            Elements img1 = el.getElementsByTag("img");
            int size = img1.size();
            Elements img2 = el.getElementsByTag("img").eq(1);
//            String img = el.getElementsByTag("img").eq(1).attr("source-data-lazy-advertisement"); //source-data-lazy-img
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            System.out.println("=====================================");
            System.out.println(img);
            System.out.println(price);
            System.out.println(title);
        }
    }

    public static List<JDPojo> getJDPojoList(String keyword) {
        String url = "https://search.jd.com/Search?keyword=java";
        //解析网页
        Document document = null;
        try {
            document = Jsoup.parse(new URL(url), 30000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element element = document.getElementById("J_goodsList");
        Elements elements = element.getElementsByTag("li");

        List<JDPojo> pojoList = new ArrayList<JDPojo>();
        for (Element el : elements) {
            Elements img1 = el.getElementsByTag("img");
            int size = img1.size();
            Elements img2 = el.getElementsByTag("img").eq(1);
//            String img = el.getElementsByTag("img").eq(1).attr("source-data-lazy-advertisement"); //source-data-lazy-img
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            JDPojo pojo = new JDPojo();
            pojo.setImg(img);
            pojo.setPrice(price);
            pojo.setTitle(title);
            pojoList.add(pojo);
        }
        return pojoList;
    }
}
