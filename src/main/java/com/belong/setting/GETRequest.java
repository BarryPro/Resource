package com.belong.setting;

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
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * @Description: <p>用于实现GET方式访问网站，用于不同访问的方式访问</p>
 * @Author: belong.
 * @Date: 2017/5/12.
 */
public class GETRequest {
    //日志工厂
    private static Logger logger = Logger.getLogger(GETRequest.class);
    public static void main(String[] args) {
        GETRequest pager = new GETRequest();
        String url = "http://www.toutiao.com/";
        String html = pager.getDecodeHtml(url);
        logger.info(html);
    }

    /**
     * 获取自动使用网站charset来进行解析网站
     *
     * @return
     */
    public String getDecodeHtml(String url) {
        return getRequest(url, "utf-8");
    }

    /**
     * 不需要登录使用GET请求来获取网站数据
     *
     * @param url
     * @param charset
     * @return
     */
    public static String getRequest(String url, String charset) {
        String html = null;
        try {
            // 获取客户端,使用客户端来进行网络请求
            HttpClient httpClient = HttpClients.custom().
                    setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
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
        } catch (ConnectionPoolTimeoutException e) {
            // 可以进行一直访问网页，防止中断
            logger.info("异常信息是：" + e);
            return getRequest(url, charset);
        } catch (UnknownHostException he) {
            url = "http://"+url;
        } catch (IOException ioe) {
            logger.info("异常信息是：" + ioe);
            return getRequest(url, charset);
        } catch (Exception ee) {
            logger.info("异常信息是：" + ee);
        }
        return html;
    }

}
