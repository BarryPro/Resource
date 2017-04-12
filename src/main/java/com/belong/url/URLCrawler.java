package com.belong.url;

import com.belong.setting.Config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网络爬虫
 * 爬取网上的资源
 * Created by belong on 2017/4/11.
 */
public class URLCrawler {

    public static void main(String[] args) {
        getURLContent();
    }

    public static void getURLContent() {
        // 用于访问的网址
        try {
            InputStream is = URLCrawler.class.getClassLoader().getResourceAsStream(Config.PATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(is,Config.CHARSETNAME));
            String buffer;
            URL url = null;
            // 用于http的协议连接
            HttpURLConnection urlConnection = null;
            // 换出字符输入流
            BufferedReader reader = null;
            ArrayList<String> list = new ArrayList<>();
            // 用于存放类型的键值对儿
            HashMap<String,String> map = new HashMap<>();

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
                System.out.println("请求网页失败!");
            }
            System.out.println(map);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertType(){

    }
}
