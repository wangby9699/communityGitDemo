package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype")//范围，表明整个容器中有一个还是有多个,单例是singletop,多个是prototype
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public AlphaService(){
        System.out.println("实例化AlphaService");
    }

    //这个注解表明，该方法会在构造器之后调用
    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaService");
    }

    //销毁方法
    //这个注解表示，在销毁之前调用它
    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaService");
    }

    public String find(){
        return alphaDao.select();
    }

}
