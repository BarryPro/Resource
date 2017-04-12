package com.belong.url;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by belong on 2017/4/11.
 */
public class URLCrawler{

    public static void main(String[] args) {
        getURLContent("http://www.5jhp.com/");
    }

    public static void getURLContent(String str_url){
        // 用于访问的网址
        URL url = null;
        // 用于http的协议连接
        HttpURLConnection urlConnection = null;
        // 换出字符输入流
        BufferedReader reader = null;

        try {
            url = new URL(str_url);
            System.out.println(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
