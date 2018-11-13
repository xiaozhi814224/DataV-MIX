package com.haier.fintech.modules.report.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.haier.fintech.common.annotation.SysLog;
@Component
public class BaiduMapUtils {
    private static String AK = "gsdG0zjFyZn49fyArYOzPbZi"; // 百度地图密钥
    private static String BASEURL="http://api.map.baidu.com/geocoder/v2/";

    // 调用百度地图API根据地址，获取坐标
    public Map<String,Object> getCoordinate(String province,String city,String district,String addr) {
    	String address =province+city+district+addr;
    	Map<String,Object> map =new HashMap<String,Object>();
        if (address != null && !"".equals(address)) {
            address = address.replaceAll("\\s*", "").replace("#", "栋");
            String url = BASEURL+"?address=" + address + "&output=json&ak=" + AK;
            String json = loadJSON(url);
            String lng = null;
            String lat = null;
            if (json != null && !"".equals(json)) {
                JSONObject obj = JSONObject.parseObject(json);
                if ("0".equals(obj.getString("status"))) {
                    double lng_ = obj.getJSONObject("result").getJSONObject("location").getDouble("lng"); // 经度
                    double lat_ = obj.getJSONObject("result").getJSONObject("location").getDouble("lat"); // 纬度
                    DecimalFormat df = new DecimalFormat("#.######");
                    lng=df.format(lng_) ;
                    lat=df.format(lat_);
                }
            }
            map.put("lng", lng);
            map.put("lat", lat);
        }
        return map;
    }

    public static String loadJSON(String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL oracle = new URL(url);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(), "UTF-8"));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {} catch (IOException e) {}
        return json.toString();
    }
    
}
