package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
/*
为什么要交该注解呢？原因是，接口有两个实现类，在测试类调用的时候
spring会不知道调用哪个，而加上@Primary，会优先调用这个，有高的权限
 */
@Primary
public class AlphaDaoMyBatisImpl implements AlphaDao {
    @Override
    public String select() {
        return "mybatis";
    }
}
