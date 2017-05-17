package com.belong.controller;

import com.belong.setting.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description: <p>爬取UC新闻</p>
 * @Author: belong.
 * @Date: 2017/5/16.
 */
@Controller
@RequestMapping("/News")
public class URLCrawlerNews {
    @Autowired
    private URLCrawler urlCrawler;

    @RequestMapping("/classify")
    public String home(){
        return Config.HOME;
    }
}
