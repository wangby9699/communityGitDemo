package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaHibernate")//给这个bean重新自定义名字
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
