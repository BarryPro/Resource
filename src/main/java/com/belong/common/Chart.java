package com.belong.common;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;

import java.awt.*;
import java.util.Map;

/**
 * @Description: <p>用于处理图表的信息</p>
 * @Author: belong.
 * @Date: 2017/5/18.
 */
public class Chart {

    public static void generateChart(Map map){
        // 声明主题来设置图表的字体和颜色，防止中文乱码
        StandardChartTheme chartTheme = new StandardChartTheme("CN");
        Font font = new Font("黑体",Font.BOLD,20);
        // 设置轴向的字体
        chartTheme.setLargeFont(font);
        // 设置标题的字体
        chartTheme.setExtraLargeFont(font);
        // 设置图例的字体
        chartTheme.setRegularFont(font);
        // 为图表设置主题
        ChartFactory.setChartTheme(chartTheme);


    }

}
