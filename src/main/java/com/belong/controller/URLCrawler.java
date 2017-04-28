package com.belong.controller;

import com.belong.service.IVideoTypeConfig;
import com.belong.setting.Config;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网络爬虫
 * 爬取网上的资源
 * Created by belong on 2017/4/11.
 */
@Controller
@RequestMapping(value = "/urlCrawler")
public class URLCrawler {
    private List<String> urls = new ArrayList<>();

    @Autowired
    private IVideoTypeConfig service;
    //日志工厂
    private static Logger logger = LoggerFactory.getLogger(URLCrawler.class);

    @RequestMapping(value = "/home")
    public String home() {
        return Config.HOME;
    }


    @RequestMapping(value = "/error")
    public String error() {
        return Config.ERROR;
    }

    @RequestMapping(value = "/type")
    public String getSubUrls(HttpServletResponse response, HttpServletRequest request) {
        URLCrawler crawler = new URLCrawler();
        String html = crawler.getDecodeHtml();
        logger.info("type=service:"+service);
        crawler.getHrefAndType(html,service);
        return Config.HOME;
    }

    /**
     * <p>返回解码后的网页信息</p>
     *
     * @return
     */
    public String getDecodeHtml() {
        getUrls();
        String url = urls.get(0);
        logger.info("url:" + url);
        String html = new URLCrawler().getHtml(this.urls.get(0), Config.DEFAULTCHARSET);
        String charset = getCharset(html);
        logger.info("charset:" + charset);
        return getHtml(url, charset);
    }

    /**
     * <p>用于得到网页的html的内容</p>
     *
     * @param url
     * @return
     */
    public String getHtml(String url, String charset) {
        String html = null;
        try {
            // 获取客户端,使用客户端来进行网络请求
            HttpClient httpClient = HttpClients.createDefault();
            // 声明请求方法(相当于request的get请求方式)
            HttpGet httpGet = new HttpGet(url);
            // 返回请求的响应
            HttpResponse response = httpClient.execute(httpGet);
            // 得到响应状态
            StatusLine statusLine = response.getStatusLine();
            // 得到请求响应码
            int code = statusLine.getStatusCode();
            logger.info("请求返回的状态码是：" + code);
            //判断响应状态码
            if (code == HttpStatus.SC_OK) {
                // 得到网页的实体
                HttpEntity entity = response.getEntity();
                // 转换成字符串
                html = EntityUtils.toString(entity, charset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return html;
    }

    public void getHrefAndType(String html,IVideoTypeConfig service) {
        logger.info("开始解析html内容");
        Document document = Jsoup.parse(html);
        // 得到多有的超链信息
        Elements a = document.getElementsByTag("a");
        // 用于过滤有效的超链
        String regex = "^/{1}.+/{1}.+\\.html$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
        logger.info("开始插入数据");
        logger.info("service:"+service);
        for (Element element : a) {
            // 得到超链的text内容
            String video_type = element.text();
            matcher = pattern.matcher(element.attr("href"));
            String video_no = null;
            // 得到超链的地址
            if (matcher.find()) {
                video_no = matcher.group(0);
                Map map = new HashMap<>();
                // 拼接全名插入
                video_no = urls.get(0)+video_no;
                map.put("videoNo", video_no);
                map.put("videoType", video_type);
                logger.info("插入的信息是：" + map);
                // service 是作为参数传进来的（否则在没有RequestMapping的方法先service不会被实例化）
                int code = service.addTypeConfig(map);
                if (code > 0) {
                    logger.info("成功插入"+map);
                } else {
                    logger.info("插入失败！");
                }
            }
        }
        logger.info("插入数据结束");
    }

    /**
     * <p>得到网页的翻译的字符集</p>
     *
     * @return 返回字符集
     */
    public String getCharset(String html) {
        // 解析成dom
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("meta");
        String tmp_attr = elements.get(0).attr("content");
        String[] attrs = tmp_attr.split("=");
        if (attrs.length < 2) {
            return null;
        } else {
            return attrs[1];
        }
    }

    /**
     * 得到io流中的网址
     *
     * @return
     */
    private List getUrls() {
        ArrayList<String> list = new ArrayList<>();
        try {
            InputStream is = URLCrawler.class.getClassLoader().getResourceAsStream(Config.PATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Config.DEFAULTCHARSET));
            String buffer;
            while ((buffer = br.readLine()) != null) {
                list.add(buffer.trim());
            }
            urls = list;
            logger.info("urls:" + list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


}
