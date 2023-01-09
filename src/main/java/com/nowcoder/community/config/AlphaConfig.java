package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;

/**
 * 配置第三方Bean的使用方法
 */
@Configuration      //表示这是一个配置类
public class AlphaConfig {

    @Bean       //这个方法返回的对象将被装配到容器里
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }



}
