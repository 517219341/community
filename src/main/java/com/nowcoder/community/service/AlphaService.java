package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service        //业务组件
//@Scope("prototype")       //实现每次调用此类时，实例化都是不同的对象，很少用
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public AlphaService(){
        System.out.println("构造AlphaService对象.");
    }
    @PostConstruct      //使初始化在构造器后面调用,自动调用
    public void init(){
        System.out.println("初始化AlphaService.");
    }
    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaService.");
    }

    public String testDIGetAlphaDao(){
        return alphaDao.select();
    }
}
