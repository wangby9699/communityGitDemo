package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //行号，以及一页最多显示多少
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    //查询帖子的行数
    //若需要动态的做一个条件，并且该方法有且只有一个条件，则此时参数之前必须取别名，否则报错
    int selectDiscussPostRows(@Param("userId") int userId);


}
