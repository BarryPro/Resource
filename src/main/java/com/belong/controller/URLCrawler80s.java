package com.belong.controller;

import com.belong.model.ClassifyConfig;
import com.belong.model.ClassifyDetailConfig;
import com.belong.model.VideoUrlConfig;
import com.belong.service.IClassifyConfig;
import com.belong.service.IClassifyDetailConfig;
import com.belong.service.IVideoDetailInfo;
import com.belong.service.IVideoUrlConfig;
import com.belong.setting.Config;
import com.belong.setting.ListURL;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
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

import javax.sql.rowset.serial.SerialBlob;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static String root = ListURL.getUrls("80s");
    //调用现有的爬虫程序
    @Autowired
    private URLCrawler urlCrawler;

    @Autowired
    private IClassifyConfig serviceClassify;

    @Autowired
    private IClassifyDetailConfig serviceClassifyDetail;

    @Autowired
    private IVideoUrlConfig serviceVideoUrl;

    @Autowired
    private IVideoDetailInfo serviceVieoDetailInfo;

    @RequestMapping(value = "/classify")
    public String getClassify() {
        logger.info("list_root:" + root);
        logger.info("当前访问的网址是：" + root);
        String html = urlCrawler.getDecodeHtml(root);
        Document document = Jsoup.parse(html);
        Element element = document.getElementById("nav");
        Elements elements = element.getElementsByTag("a");
        Map map = new HashMap();
        for (Element a : elements) {
            String typeName = a.text();
            String typeHref = a.attr("href");
            String pager = "";
            typeHref = root + typeHref;
            // 二次分类获取分类的页数
            html = urlCrawler.getDecodeHtml(typeHref);
            pager = getPager(html);
            map.put("typeName", typeName);
            map.put("typeHref", typeHref);
            map.put("pager", pager);
            logger.info("插入的数据是：" + map);
            try {
                int code = serviceClassify.addClassify(map);
                if (code > 0) {
                    logger.info("成功插入：" + map);
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
    public String getClassifyDetail(Map map) {
        // 取得大类
        List<ClassifyConfig> list = serviceClassify.getClassify();
        logger.info("list:" + list);
        for (ClassifyConfig config : list) {
            logger.info("当前访问的类型和超链是：" + config.getTypeName() + "," + config.getTypeHref());
            String html = urlCrawler.getDecodeHtml(config.getTypeHref());
            Document document = Jsoup.parse(html);
            Elements dls = document.getElementsByTag("dl");
            for (Element dl : dls) {
                Elements as = dl.getElementsByTag("a");
                for (Element a : as) {
                    String typeDtlhref, typeDtlname, type_Name, pager;
                    typeDtlhref = root + a.attr("href");
                    typeDtlname = a.text();
                    type_Name = config.getTypeName();
                    // 二次访问来获取详细分类的页数
                    html = urlCrawler.getDecodeHtml(typeDtlhref);
                    pager = getPager(html);
                    map.put("typeDtlhref", typeDtlhref);
                    map.put("typeDtlname", typeDtlname);
                    map.put("typeName", type_Name);
                    map.put("pager", pager);
                    logger.info("要插入的数据是：" + map);
                    try {
                        int code = serviceClassifyDetail.addDetailClassify(map);
                        if (code > 0) {
                            logger.info("成功插入：" + map);
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

    /**
     * 按照详细分区表
     *
     * @return
     */
    @RequestMapping(value = "/classifyTable")
    public String addClassifyTalbe() {
        return Config.HOME;
    }

    /**
     * 得到看一共有多少分页
     *
     * @param html
     * @return
     */
    public String getPager(String html) {
        String pager = null;
        String regex = "</strong>.*<a href=\".*/(.*)\">.*</a>\\s+</div>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            pager = matcher.group(1);
        } else {
            pager = "0";
        }
        logger.info("页数是：" + pager);
        return pager;
    }

    @RequestMapping(value = "/specificContent")
    public String getSpecificContent() {
        List<ClassifyDetailConfig> list = serviceClassifyDetail.getClassifyDetail();
        List<Map<String, String>> map_list = new ArrayList<>();
        // 网址个数计数器
        int count = 0;
        // 统计url的总数
        int url_count = 0;
        for (ClassifyDetailConfig detailConfig : list) {
            logger.info("当前页的url：" + detailConfig.getTypeDtlhref());
            List<String> urls = getSpecificUrl(detailConfig.getTypeDtlhref(), detailConfig.getPager());
            logger.info("urls-" + (++count) + ": " + urls);
            url_count += urls.size();
            // 循环处理urls中的数据
            for (String url : urls) {
                logger.info("当前访问的url：" + url);
                String html = urlCrawler.getDecodeHtml(url);
                map_list = addVideo(html);
                logger.info("当前页中的视频链接是:" + map_list);
                try {
                    // 插入数据库视频路径信息
                    if (!map_list.isEmpty()) {
                        for (Map video_Map : map_list) {
                            logger.info("当前插入的视频信息是：" + video_Map);
                            serviceVideoUrl.addVideo(video_Map);
                        }
                    }
                } catch (Exception e) {
                    logger.info(e.getMessage());
                    continue;
                }
            }
        }
        logger.info("总共的网页个数是：[" + url_count + "]");
        return Config.HOME;
    }

    /**
     * 用于添加详细的视频信息
     *
     * @return
     */
    @RequestMapping(value = "/videoDetail")
    public String addVideoDetail() {
        int position = getPosition();
        logger.info("当前的位置是：" + position);
        List<VideoUrlConfig> list = serviceVideoUrl.getVideo();
        // 含有电影具体信息的网页
        String html = null;
        for (int i = position;i<list.size();i++) {
            html = urlCrawler.getDecodeHtml(list.get(i).getVideoHref());
            if (html != null) {
                Map map = getVideoDetail(html);
                Object href = map.get("videoHref");
                logger.info("当前访问第" + (i+1) + "条数据");
                setPosition(i+1);
                if(href == null){
                    continue;
                } else if(href.toString().startsWith("http")) {

                    logger.info("要插入的map是：" + map);
                    try {
                        int code = serviceVieoDetailInfo.addVideoDetail(map);
                        if (code > 0) {
                            logger.info("成功插入："+href);
                        } else {
                            logger.error("插入失败");
                        }
                    } catch (Exception e) {
                        logger.info("错误信息是：" + e.getMessage());
                        continue;
                    }
                } else {
                    logger.info("插入的信息不满足数据类型的规范：["+ href.toString()+"]");
                    continue;
                }
            }
        }
        logger.info("视频全部插入完成！");
        return Config.HOME;
    }

    /**
     * 组合要插入的电影详细信息
     *
     * @param html
     * @return
     */
    public Map<String, Object> getVideoDetail(String html) {
        Map<String, Object> map = new HashMap<>();
        Document document = Jsoup.parse(html);
        Elements videoDetail = document.getElementsByClass("info");
        Elements videoPic_1 = document.getElementsByClass("noborder block1");
        Elements videoHref_1 = document.getElementsByClass("xunlei dlbutton1");
        if (!videoDetail.isEmpty()) {
            Element div = videoDetail.get(0);
            Blob blob = null;
            try {
                blob = new SerialBlob(div.html().getBytes());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            map.put("videoInfo", blob);
        }
        if (!videoPic_1.isEmpty()) {
            Element div = videoPic_1.get(0);
            Elements imgs = div.getElementsByTag("img");
            if (!imgs.isEmpty()) {
                String videoPic = imgs.get(0).attr("_src");
                videoPic = "http:" + videoPic;
                map.put("videoPic", videoPic);
            }
        }
        if (!videoHref_1.isEmpty()) {
            Element spans = videoHref_1.get(0);
            Elements as = spans.getElementsByTag("a");
            if (!as.isEmpty()) {
                String url = as.get(0).attr("href");
                String encodeHtml = getHtml(url);
                String videoHref = getEncodeUrl(encodeHtml);
                map.put("videoHref", videoHref);
            }
        }
        return map;
    }

    /**
     * 用于添加视频的参数
     *
     * @param html
     */
    public List<Map<String, String>> addVideo(String html) {
        List<Map<String, String>> list = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements uls = document.getElementsByClass("me1 clearfix");
        if (uls.isEmpty()) {
            uls = document.getElementsByClass("me3 clearfix");
        }
        Elements lis = null;
        if (!uls.isEmpty()) {
            lis = uls.get(0).getElementsByTag("li");
        }
        Map<String, String> map = new HashMap<>();
        for (Element li : lis) {
            Elements as = li.getElementsByTag("a");
            Elements imgs = li.getElementsByTag("img");
            String videoImg = imgs.get(0).attr("_src");
            // 组合规范的图片访问URL
            videoImg = "http:" + videoImg;
            String videoHref = as.get(0).attr("href");
            videoHref = root + videoHref;
            String[] title = as.get(0).attr("title").toString().split("\\s+");
            // 分割title 分成title 和 rating
            String videoTitle = null;
            String videoRating = null;
            if (title != null && title.length > 1) {
                videoTitle = title[0];
                videoRating = title[1];
            }
            map.put("videoHref", videoHref);
            map.put("videoTitle", videoTitle);
            map.put("videoRating", videoRating);
            map.put("videoImg", videoImg);
            list.add(map);
        }
        return list;
    }

    /**
     * 用于拼接具体的访问连接
     *
     * @param root  主地址
     * @param pager 子页的分页
     * @return
     */
    public List<String> getSpecificUrl(String root, String pager) {
        List<String> urls = new ArrayList<>();

        String regex_root = "(.*)/.*";
        Pattern pattern_root = Pattern.compile(regex_root);
        Matcher matcher_root = pattern_root.matcher(root);
        String regex_pager = "(.*p)(\\d*)";

        Pattern pattern_pager = Pattern.compile(regex_pager);
        Matcher matcher_pager = pattern_pager.matcher(pager);
        int pager_num = 0;
        if (matcher_pager.find()) {
            // 子网页的个数
            pager_num = Integer.parseInt(matcher_pager.group(2));
            // 子网页前缀
            pager = matcher_pager.group(1);
        }
        if (matcher_root.find()) {
            // 重置根目录
            root = matcher_root.group(1);
        }
        // 组合具体可以访问的URLs
        if (pager_num != 0) {
            for (int i = 2; i <= pager_num; i++) {
                urls.add(root + "/" + pager + i);
            }
        } else {
            urls.add(root);
        }
        return urls;
    }

    /**
     * 带参数的访问网页
     *
     * @return
     */
    public String getHtml(String url_input) {
        String html = null;
        try {
            // 获取客户端,使用客户端来进行网络请求
            HttpClient httpClient = HttpClients.createDefault();
            // 声明请求方法(相当于request的get请求方式)
            HttpPost httpPost = new HttpPost(ListURL.getUrls("parse"));
            // 解决中文乱码在外面包一层 StringEntity 继承了HttpEntity
            httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            // 设置网页请求的参数，包括form-data的提交
            List<BasicNameValuePair> form_params = new ArrayList<>();
            form_params.add(new BasicNameValuePair("url-input", url_input));
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(form_params, Config.DEFAULTCHARSET);
            httpPost.setEntity(urlEncodedFormEntity);

            //httpPost
            // 返回请求的响应
            HttpResponse response = httpClient.execute(httpPost);
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
                html = EntityUtils.toString(entity, Config.DEFAULTCHARSET);
            } else {
                logger.info("请求失败code是：" + code);
            }
            //EntityUtils.consume(multipartEntity);
        } catch (Exception e) {
            // 可以进行一直访问网页，防止中断
            System.out.println("异常信息是：" + e);
            return getHtml(url_input);
        }
        return html;
    }

    /**
     * 用于获取解码后的原始地址的超链
     *
     * @param html
     * @return
     */
    public String getEncodeUrl(String html) {
        Document document = Jsoup.parse(html);
        String EncodeUrl = "";
        Elements divs = document.getElementsByClass("url-output");
        if (!divs.isEmpty()) {
            Elements as = divs.get(0).getElementsByTag("a");
            if (!as.isEmpty()) {
                EncodeUrl = as.get(0).attr("href");
            }
        }
        return EncodeUrl;
    }

    /**
     * 得到上次访问的的位置
     */
    public int getPosition() {
        String path = URLCrawler80s.class.getClassLoader().getResource(Config.POSITION).getPath();
        InputStream is = null;
        Properties properties = new Properties();
        Integer pos = 0;
        try {
            is = new FileInputStream(path);
            properties.load(is);
            pos = Integer.parseInt(properties.get("position").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pos;
    }

    /**
     * 设置这次访问的位置
     */
    public void setPosition(Integer position) {
        String path = URLCrawler80s.class.getClassLoader().getResource(Config.POSITION).getPath();
        OutputStream os = null;
        Properties properties = new Properties();
        try {
            os = new FileOutputStream(path);
            properties.setProperty("position", position.toString());
            properties.store(os, "pro_pos_file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
