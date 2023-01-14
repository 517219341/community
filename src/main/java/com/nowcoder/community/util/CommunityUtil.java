package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

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
}
