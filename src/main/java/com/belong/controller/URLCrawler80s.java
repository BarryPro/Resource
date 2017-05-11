package com.belong.controller;

import com.belong.model.ClassifyConfig;
import com.belong.service.IClassifyConfig;
import com.belong.service.IClassifyDetailConfig;
import com.belong.setting.Config;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: <p>负责爬取80s的电影资源</p>
 * @Author: belong.
 * @Date: 2017/5/11.
 */
@Controller
@RequestMapping(value = "/80sHome")
public class URLCrawler80s {
    // 日志
    private static Logger logger = LoggerFactory.getLogger(URLCrawler80s.class);

    // 用于全局共享访问地址的连接
    private List<String> list_root = new ArrayList<>();

    //调用现有的爬虫程序
    @Autowired
    private URLCrawler urlCrawler;

    @Autowired
    private IClassifyConfig serviceClassify;

    @Autowired
    private IClassifyDetailConfig serviceClassifyDetail;

    @RequestMapping(value = "/classify")
    public String getClassify(){
        list_root = urlCrawler.getUrls();
        logger.info("list_root:"+list_root);
        logger.info("当前访问的网址是："+list_root.get(0));
        String html = urlCrawler.getDecodeHtml(list_root.get(0));
        Document document = Jsoup.parse(html);
        Element element = document.getElementById("nav");
        Elements elements = element.getElementsByTag("a");
        Map map = new HashMap();
        for(Element a:elements){
            String typeName = a.text();
            String typeHref = a.attr("href");
            typeHref = list_root.get(0)+typeHref;
            map.put("typeName",typeName);
            map.put("typeHref",typeHref);
            logger.info("插入的数据是："+map);
            try {
                int code = serviceClassify.addClassify(map);
                if(code > 0){
                    logger.info("成功插入："+map);
                } else {
                    logger.info("插入失败");
                }
            } catch (Exception e) {
                continue;
            }
        }
        return Config.HOME;
    }

    @RequestMapping(value = "/classifyDetail")
    public String getClassifyDetail(Map map){
        list_root = urlCrawler.getUrls();
        // 取得大类
        List<ClassifyConfig> list = serviceClassify.getClassify();
        logger.info("list:"+list);
        for(ClassifyConfig config:list){
            logger.info("当前访问的类型和超链是："+config.getTypeName()+","+config.getTypeHref());
            String html = urlCrawler.getDecodeHtml(config.getTypeHref());
            Document document = Jsoup.parse(html);
            Elements dls = document.getElementsByTag("dl");
            for(Element dl:dls){
                Elements as = dl.getElementsByTag("a");
                for(Element a:as){
                    String typeDtlhref,typeDtlname,type_Name;
                    typeDtlhref = list_root.get(0)+a.attr("href");
                    typeDtlname = a.text();
                    type_Name = config.getTypeName();
                    map.put("typeDtlhref",typeDtlhref);
                    map.put("typeDtlname",typeDtlname);
                    map.put("typeName",type_Name);
                    logger.info("要插入的数据是："+map);
                    try {
                        int code = serviceClassifyDetail.addDetailClassify(map);
                        if(code > 0){
                            logger.info("成功插入："+map);
                        } else {
                            logger.info("插入失败");
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }

        return Config.HOME;
    }

}
