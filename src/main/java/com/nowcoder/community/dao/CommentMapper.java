package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    //通过实体查询，是帖子的评论还是评论的评论
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    //查询数据条目数
    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

}
