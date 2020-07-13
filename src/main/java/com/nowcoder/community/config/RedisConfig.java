package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory){
        //实例化
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        //把工厂给它
        template.setConnectionFactory(factory);

        //设置key序列化方式
        //RedisSerializer.string()能返回能够序列化字符串的序列化器
        template.setKeySerializer(RedisSerializer.string());

        //设置普通的value的序列化方式
        //json是结构化的，对于value相对来说很好，因为不知道是数组还是字符，不知道是哪种形式的数据
        template.setValueSerializer(RedisSerializer.json());

        //设置hash的key的序列化方式,因为value本身就是个hash
        template.setHashKeySerializer(RedisSerializer.string());

        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        //使设置好的生效
        template.afterPropertiesSet();
        return template;
    }

}
