package com.belong.ip;

/**
 * @Author: belong.
 * @Description:
 * @Date: 2017/4/16.
 */
public class ProxyUrl {
    //public static void getProxyUrl(){
    //    // 创建 HttpClient 的实例
    //    CloseableHttpClient httpClient = HttpClients.createDefault();
    //
    //    // 代理的主机（实际上是一个代理的数组,网上有很多提供代理服务器的）
    //    // 推荐：http://www.xici.net.co/ 动态爬去这个网站的代理ip，然后使用，也可以购买
    //    // 我并没有试过，不过应该是可行的。主要是提供一种解决问题的思路
    //    ProxyHost proxy = new ProxyHost("117.59.224.62", 80);
    //
    //    // 使用代理
    //    httpClient.getHostConfiguration().setProxyHost(proxy);
    //
    //    // 创建Get连接方法的实例
    //    HttpMethod getMethod = new GetMethod("http://vip.bet007.com/OverDown_n.aspx?id=279403");
    //
    //    // 使用系统提供的默认的恢复策略
    //    getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
    //            new DefaultHttpMethodRetryHandler());
    //
    //    try {
    //        // 请求URI
    //        System.out.println("executing request " + getMethod.getURI());
    //
    //        // 执行getMethod
    //        int status = httpClient.executeMethod(getMethod);
    //
    //        System.out.println("status:" + status);
    //
    //        // 连接返回的状态码
    //        if (HttpStatus.SC_OK == status) {
    //
    //            System.out.println("Connection to " + getMethod.getURI()
    //                    + " Success!");
    //
    //            // 获取到的内容
    //            //String responseBody = getMethod.getResponseBodyAsString();
    //            byte[] responseBody = getMethod.getResponseBody();
    //            String  html=new String(responseBody,"gbk");
    //
    //            System.out.println("==="+html);
    //        }
    //
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    } finally {
    //        // 释放连接
    //        getMethod.abort();
    //    }
    //}

}
