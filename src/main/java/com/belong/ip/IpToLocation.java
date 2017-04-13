package com.belong.ip;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

import java.net.URL;

/**
 * Created by belong on 2017/4/11.
 */
public class IpToLocation {
    public static void main(String[] args) {
        try {
            // 得到资源目录下的资源文件
            URL url = IpToLocation.class.getClassLoader().getResource("dat/GeoLiteCity-2013-01-18.dat");
            LookupService cl = new LookupService(url.getPath(), LookupService.GEOIP_MEMORY_CACHE);
            Location l2 = cl.getLocation("104.37.213.4");
            System.out.println(
                    "countryCode: " + l2.countryCode + "\n" +
                            "countryName: " + l2.countryName + "\n" +
                            "region: " + l2.region + "\n" +
                            "city: " + l2.city + "\n" +
                            "latitude: " + l2.latitude + "\n" +
                            "longitude: " + l2.longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
