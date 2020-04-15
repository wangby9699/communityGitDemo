package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration//表明此类是配置类
public class AlphaConfig {
    @Bean
    public SimpleDateFormat simpleDateFormat(){
        //方法名就是bean的名字
        //本次代码装配SimpleDateFormat，该方法返回的对象会被装配到容器中
        return  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
