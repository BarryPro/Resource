package com.belong.controller;

import com.belong.service.IVideoTypeConfig;
import com.belong.setting.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
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
    @Autowired
    private IVideoTypeConfig service;

    private static Logger logger = LoggerFactory.getLogger(URLCrawler.class);

    @RequestMapping(value = "/home")
    public String home(){
       return Config.HOME;
    }

    @RequestMapping(value = "/type")
    public String addType(HashMap<String,String> map) {
        logger.info("type--input--DB");
        map = getURLContent();
        logger.info("map"+map);
        Set<String> set = map.keySet();
        for(String type:set){
            HashMap<String,String> inParam = new HashMap<>();
            inParam.put("videoNo",type);
            inParam.put("videoType",map.get(type));
            service.addTypeConfig(inParam);
        }
        logger.info("成功操作");
        return Config.SUCCESS;
    }

    public HashMap<String, String> getURLContent() {
        // 用于存放类型的键值对儿
        HashMap<String,String> map = new HashMap<>();
        try {
            InputStream is = URLCrawler.class.getClassLoader().getResourceAsStream(Config.PATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(is,Config.CHARSETNAME));
            String buffer;
            URL url = null;
            // 用于http的协议连接
            HttpURLConnection urlConnection = null;
            ArrayList<String> list = new ArrayList<>();
            while ((buffer = br.readLine()) != null) {
                list.add(buffer.trim());
            }
            url = new URL(list.get(0));
            // 打开连接
            urlConnection = (HttpURLConnection) url.openConnection();
            // 得到http的相应码，用来验证请求是否是有效的
            int code = urlConnection.getResponseCode();
            // 200表示的是请求成功
            if(code == HttpURLConnection.HTTP_OK){
                // 用于保存整个网页
                StringBuffer html = new StringBuffer();
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),Config.DEFAULTCHARSETNAME));
                while ((buffer = bufferedReader.readLine()) != null) {
                    html.append(buffer+Config.ENTER);
                }
                // 开始提取网页中的想要的信息
                String regex = "<li><a href=\"/frim/index(.*).html\">(.*)</a></li>\n";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(html);
                while (matcher.find()) {
                    map.put(matcher.group(1),matcher.group(2));
                }
                //System.out.println(html);
            } else {
                logger.error("访问网页超时");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
