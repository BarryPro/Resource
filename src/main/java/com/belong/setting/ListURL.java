package com.belong.setting;

import com.belong.controller.URLCrawler;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

/**
 * @Description: <p>用于可以使用的url的键值对文件</p>
 * @Author: belong.
 * @Date: 2017/5/17.
 */
public class ListURL {
    //日志工厂
    private static Logger logger = Logger.getLogger(ListURL.class);
    /**
     * 得到io流中的网址,通过key来获取工具网址     *
     * @return
     */
    public static void main(String[] args) {
        logger.info(ListURL.getUrls("80s"));
    }

    public static String getUrls(String keyName) {
        InputStream is;
        Properties pro = new Properties();
        // 用于返回的url地址
        String url = null;
        try {
            is = URLCrawler.class.getClassLoader().getResourceAsStream(Config.PATH);
            pro.load(is);
            url = (String) pro.get(keyName);
        } catch (Exception e) {
            logger.info("异常信息是：" + e);
        }
        return url;
    }
}
