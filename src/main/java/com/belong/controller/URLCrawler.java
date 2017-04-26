package com.belong.controller;

import com.belong.ip.CusAccessObjectUtil;
import com.belong.service.IVideoTypeConfig;
import com.belong.setting.Config;
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
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
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
    // 子url列表
    private List urls_list = new ArrayList<>();
    // 用于存放类型的列表
    private List types_list = new ArrayList<>();

    @Autowired
    private IVideoTypeConfig service;

    private static Logger logger = LoggerFactory.getLogger(URLCrawler.class);

    @RequestMapping(value = "/home")
    public String home(){
       return Config.HOME;
    }

    @RequestMapping(value = "/subUrls")
    public String subUrls(){
        this.types_list = (ArrayList<String>) comSubUrls();
        return Config.HOME;
    }


    @RequestMapping(value = "/type")
    public String addType(HttpServletResponse response, HttpServletRequest request) {
        logger.info("ip:"+ CusAccessObjectUtil.getIpAddress(request));
        logger.info("开始插入类型数据");
        Map map = getURLContent();
        logger.info("map"+ map);
        Set<String> set = map.keySet();
        String msg = Config.NUL;
        PrintWriter writer = null;
        try {
            response.setContentType("text/html;charset=utf-8");
            writer = response.getWriter();

            for(String type:set){
                Map inParam = new HashMap<>();
                inParam.put("videoNo",type);
                inParam.put("videoType", map.get(type));
                types_list.add(type);
                service.addTypeConfig(inParam);
                logger.info("插入的信息："+inParam);
            }
            msg = "<span style=\"color: blue\">操作成功</span>";
            logger.info("成功操作"+ map);
        } catch (Exception e) {
            msg = "<span style=\"color: red\">操作失败,已经有数据了</span>";
            logger.info("map:"+ map);
            logger.error("错误信息："+ map +e.getMessage());
        } finally {
            writer.write(msg);
            writer.flush();
            writer.close();
            logger.info("结束插入类型数据");
            return Config.HOME;
        }
    }


    @RequestMapping(value = "/error")
    public String error(){
        return Config.ERROR;
    }

    public HashMap<String, String> getURLContent() {
        // 用于存放类型的键值对儿
        HashMap<String,String> map = new HashMap<>();
        try {
            URL url = null;
            // 用于http的协议连接
            HttpURLConnection urlConnection = null;
            List<String> list = getUrls();
            url = new URL(list.get(0));
            // 打开连接
            urlConnection = (HttpURLConnection) url.openConnection();
            // 得到http的相应码，用来验证请求是否是有效的
            int code = urlConnection.getResponseCode();
            logger.info("状态码是：["+code+"]");
            // 200表示的是请求成功
            if(code == HttpURLConnection.HTTP_OK){
                // 用于保存整个网页
                StringBuffer html = new StringBuffer();
                String buffer;
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),Config.DEFAULTCHARSETNAME));
                while ((buffer = bufferedReader.readLine()) != null) {
                    html.append(buffer+Config.ENTER);
                }
                // 开始提取网页中的想要的信息
                String regex = "<li><a href=\"(.*).html\">(.*)</a></li>\n";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(html);
                while (matcher.find()) {
                    map.put(matcher.group(1),matcher.group(2));
                }
            } else {
                logger.error("访问网页超时");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 用于得到所有的子分类的页
     * @return
     */
    private List comSubUrls(){
        ArrayList<String> list = new ArrayList<>();
        List<String> list_tmp = getUrls();
        this.urls = list_tmp;
        String root = urls.get(0);
        types_list = service.getVideoNO();
        types_list.forEach((no)->list.add(root+Config.SUBPATH+no+Config.HTML));
        logger.info("urls_list:"+list);
        return list;
    }

    /**
     * 得到io流中的网址
     * @return
     */
    private List getUrls(){
        ArrayList<String> list = new ArrayList<>();
        try {
            InputStream is = URLCrawler.class.getClassLoader().getResourceAsStream(Config.PATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(is,Config.CHARSETNAME));
            String buffer;
            while ((buffer = br.readLine()) != null) {
                list.add(buffer.trim());
            }
            this.urls = list;
            logger.info("urls:"+list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


}
