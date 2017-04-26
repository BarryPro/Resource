package com.belong.service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @Description: <p></p>
 * @Author: belong.
 * @Date: 2017/4/25.
 */
public class UrlsConfigImpl implements IUrlsConfig{
    @Autowired
    private IUrlsConfig dao;

    @Override
    public int addUrls(Map map) {
        return dao.addUrls(map);
    }
}
