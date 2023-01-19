package com.nowcoder.community.util;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    // MD5加密(密码+ salt(随机字符串))
    // MD5只能加密，不能解密
    public static String md5(String key) {
        if (StringUtils.isBlank(key)){
            return  null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 返回JSON字符串
     *
     * @param code 编码
     * @param msg  提示信息
     * @param map  业务数据
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String,Object> map) {
        JSONObject json =new JSONObject();
        json.put("code",code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code,msg,null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code,null, null);
    }


}
