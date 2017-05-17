package com.belong.controller;

import com.belong.service.IVideoRec;
import com.belong.service.IVideoTypeConfig;
import com.belong.setting.Config;
import com.belong.setting.GETRequest;
import com.belong.setting.ListURL;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionPoolTimeoutException;
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
import java.io.IOException;
import java.net.UnknownHostException;
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
    //日志工厂
    private static Logger logger = LoggerFactory.getLogger(URLCrawler.class);

    @Autowired
    private IVideoTypeConfig config_service;

    @Autowired
    private IVideoRec rec_service;

    @RequestMapping(value = "/home")
    public String home() {
        return Config.HOME;
    }

    @RequestMapping(value = "/error")
    public String error() {
        return Config.ERROR;
    }

    /**
     * 用于得到远程客户端的ip地址
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/addr")
    public String getAddr(HttpServletRequest request) {
        String Remote_addr = request.getRemoteAddr();
        String Local_addr = request.getLocalAddr();
        // 获取远程客户端的ip
        String ip = getIpAddress(request);
        logger.info("Remote_addr:" + Remote_addr);
        logger.info("Local_addr:" + Local_addr);
        logger.info("ip:" + ip);
        return Config.HOME;
    }

    /**
     * <p>用于给video_type_config插入url数据</p>
     *
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "/type")
    public String getSubUrls(HttpServletResponse response, HttpServletRequest request) {
        URLCrawler crawler = new URLCrawler();
        String html = crawler.getDecodeHtml(ListURL.getUrls("99vv1"));
        logger.info("type=service:" + config_service);
        crawler.getHrefAndType(html, config_service);
        return Config.HOME;
    }

    /**
     * <p>用于查询所有的可访问的超链然后进行访问</p>
     *
     * @return
     */
    @RequestMapping(value = "/subUrls")
    public String cyclicConnUrls(Map<String, String> map) {
        // 用于装入可访问的链表
        List<String> list = config_service.getVideoCate();
        logger.info("可访问的网址是：[" + list.size() + "]" + list);
        // 进行访问查询出来的地址
        String context;
        Document document;
        //定义计数器
        int count = 0;
        for (String url : list) {
            logger.info("当前访问的url是：[第" + (++count) + "个url]" + url);
            context = getDecodeHtml(url);
            //得到dom
            document = Jsoup.parse(context);
            Elements lis = document.getElementsByClass("pagination");
            // 得到最后一个是最后的那个页
            Element last_a = lis.get(lis.size() - 1);
            String regex = "<a href=.*-(.*)\\.html\">尾页</a>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(last_a.html());
            // 循环子页的次数
            int n = 0;
            if (matcher.find()) {
                String num = matcher.group(1);
                n = Integer.parseInt(num);
            }
            //
            String video_no;
            String video_type;
            logger.info("子链的个数是：" + n);
            for (int i = 2; i <= n; i++) {
                video_no = url.substring(0, url.length() - 5) + "-" + i + url.substring(url.length() - 5, url.length());
                video_type = i + " ";
                map.put("videoNo", video_no);
                map.put("videoType", video_type);
                logger.info("要插入的map：" + map);
                try {
                    int code = config_service.addTypeConfig(map);
                    if (code > 0) {
                        logger.info("成功插入：" + map);
                    } else {
                        logger.info("插入失败！");
                    }
                } catch (Exception e) {
                    logger.error("异常信息是：" + e);
                    continue;
                }
            }
        }

        return Config.HOME;
    }

    @RequestMapping(value = "/rec")
    public String getRec(Map map) {
        // 用于装入可访问的链表
        List<String> list = config_service.getVideoCate();
        logger.info("可访问的网址是：[" + list.size() + "]" + list);
        // 进行访问查询出来的地址
        String html = new URLCrawler().GETRequest(ListURL.getUrls("99vv1"), Config.DEFAULTCHARSET);
        String charset = getCharset(html);
        String context;
        Document document;
        //定义计数器
        int count = 0;
        for (String url : list) {
            logger.info("当前访问的url是：[第" + (++count) + "个url]" + url);
            context = getDecodeHtml(url);
            //得到dom
            document = Jsoup.parse(context);
            Elements uls = document.getElementsByTag("ul");
            String class_type;
            for (Element ul : uls) {
                class_type = ul.attr("class");
                // 可以确定是具体的url(而不是分类的url)
                if ("".equals(class_type)) {
                    Elements as = ul.getElementsByTag("a");
                    Elements imgs = ul.getElementsByTag("img");
                    Elements h3s = ul.getElementsByTag("h3");
                    if (imgs.size() > 0) {
                        logger.info("as:" + as.size() + "  imgs:" + imgs.size() + "  h3s:" + h3s.size());
                        // 进行循环取想要的资源
                        String a, img, h3;
                        for (int i = 0; i < imgs.size(); i++) {
                            a = as.get(i).attr("href");
                            img = imgs.get(i).attr("src");
                            h3 = h3s.get(i).text();
                            logger.info("a=" + a + " img=" + img + " h3=" + h3);
                            a = ListURL.getUrls("99vv1") + a;
                            map.put("videoSrc", a);
                            map.put("videoName", h3);
                            map.put("videoPic", img);
                            map.put("videoType", url);
                            logger.info("要插入的信息是：" + map);
                            int code = 0;
                            try {
                                code = rec_service.addRec(map);
                            } catch (Exception e) {
                                continue;
                            }
                            if (code > 0) {
                                logger.info("成功插入：" + map);
                            } else {
                                logger.info("插入失败！");
                            }
                        }
                    } else {
                        // ul有多了，只有有资源的才是有图片的
                        continue;
                    }
                }
            }
        }
        return Config.HOME;
    }

    private String getIpAddress(HttpServletRequest request) {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址  

        String ip = request.getHeader("X-Forwarded-For");
        if (logger.isInfoEnabled()) {
            logger.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);
                }
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * <p>返回解码后的网页信息</p>
     *
     * @return
     */
    public String getDecodeHtml(String url) {
        logger.info("url:" + url);
        String html = new URLCrawler().GETRequest(ListURL.getUrls("99vv1"), Config.DEFAULTCHARSET);
        String charset = getCharset(html);
        return GETRequest(url, charset);
    }

    /**
     * <p>用于得到网页的html的内容</p>
     *
     * @param url
     * @return
     */
    public String GETRequest(String url, String charset) {
        logger.info("当前请求的地址是：" + url);
        String html = null;
        try {
            // 获取客户端,使用客户端来进行网络请求
            HttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
            // 声明请求方法(相当于request的get请求方式)
            HttpGet httpGet = new HttpGet(url);
            // 返回请求的响应
            HttpResponse response = httpClient.execute(httpGet);
            // 得到响应状态
            StatusLine statusLine = response.getStatusLine();
            // 得到请求响应码
            int code = statusLine.getStatusCode();
            logger.info("状态码code：" + code);
            //判断响应状态码
            if (code == HttpStatus.SC_OK) {
                // 得到网页的实体
                HttpEntity entity = response.getEntity();
                // 转换成字符串
                html = EntityUtils.toString(entity, charset);
            } else {
                logger.info("请求失败code是：" + code);
            }
        } catch (ConnectionPoolTimeoutException e) {
            // 可以进行一直访问网页，防止中断
            logger.info("异常信息是：" + e);
            return GETRequest(url, charset);
        } catch (UnknownHostException he) {
            url = "http://"+url;
        } catch (IOException ioe) {
            logger.info("异常信息是：" + ioe);
            return GETRequest(url, charset);
        } catch (Exception ee) {
            logger.info("异常信息是：" + ee);
        }
        return html;
    }

    /**
     * 得到html页中的超链和超链所指向的内容
     *
     * @param html    html的全部内容
     * @param service 用于调用服务的句柄
     */
    private void getHrefAndType(String html, IVideoTypeConfig service) {
        logger.info("开始解析html内容");
        Document document = Jsoup.parse(html);
        // 得到多有的超链信息
        Elements a = document.getElementsByTag("a");
        // 用于过滤有效的超链
        String regex = "^/{1}.+/{1}.+\\.html$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
        logger.info("开始插入数据");
        logger.info("service:" + service);
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
                video_no = ListURL.getUrls("99vv1") + video_no;
                map.put("videoNo", video_no);
                map.put("videoType", video_type);
                logger.info("插入的信息是：" + map);
                // service 是作为参数传进来的（否则在没有RequestMapping的方法先service不会被实例化）
                int code = service.addTypeConfig(map);
                if (code > 0) {
                    logger.info("成功插入" + map);
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
    private String getCharset(String html) {
        // 解析成dom
        if (html != null) {
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
        return "";
    }

    public static void main(String[] args) {
        URLCrawler urlCrawler = new URLCrawler();
        String html = GETRequest.getRequest("http://www.99vv1.com", Config.DEFAULTCHARSET);
        Document document = Jsoup.parse(html);
        Elements scripts = document.getElementsByTag("script");
        for (Element script : scripts) {
            String js = script.attr("src");
            logger.info(js);
        }
    }
}
