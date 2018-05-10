package com.lei.util;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.*;

/**
 * Created by nowcoder on 2016/7/3.
 */
public class WendaUtil {
    private static final Logger logger = LoggerFactory.getLogger(WendaUtil.class);

    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            logger.error("生成MD5失败", e);
            return null;
        }
    }

    public static String getJSONString(int code) {
        JsonObject json = new JsonObject();
        json.addProperty("code", code);
        return json.toString();
    }

    public static String getJSONString(int code, String msg) {
        JsonObject json = new JsonObject();
        json.addProperty("code", code);
        json.addProperty("msg", msg);
        return json.toString();
    }

    public static String getJSONString(int code, Map<String, Object> map) {
        JsonObject json = new JsonObject();
        json.addProperty("code", code);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.addProperty(entry.getKey(), entry.getValue().toString());
        }
        return json.toString();
    }

    public static List<String> changeToList(Set<String> set) {
        Iterator<String> it = set.iterator();
        List<String> value = new ArrayList<>();
        while (it.hasNext()) {
            String s = it.next();
            value.add(s);
        }
        return value;
    }
}
