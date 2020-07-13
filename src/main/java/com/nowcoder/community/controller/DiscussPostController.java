package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;


@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    //获取当前用户
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    //增加异步请求
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody   //因为返回的是字符串不是网页
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if (user == null){//用户为空，则说明未登录
            return CommunityUtil.getJSONString(403,"你还没有登录！");
        }

        //若用户已登录
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //报错的情况将来统一处理
        return CommunityUtil.getJSONString(0,"发布成功！");
    }

    //查询帖子
    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeCount",likeCount);
        //点赞状态
        //首先判断，因为如果没有登录的话，是也能看，但点赞状态是未赞，为0
        int likeStatus =hostHolder.getUser() == null ? 0: likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeStatus",likeStatus);


        //查评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        //评论：给帖子的评论
        //回复：给评论的评论
        //得到当前帖子的所有的值
        List<Comment> commentList= commentService.findCommentsByEntity(
                ENTITY_TYPE_POST,post.getId(),page.getOffset(),page.getLimit());

        //显示的对象,评论VO的列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null){
            for (Comment comment:commentList){
                //存到Map中,Map就是一个评论的VO
                Map<String, Object> commentVo = new HashMap<>();
                //往VO里面添加评论和作者
                commentVo.put("comment",comment);
                commentVo.put("user",userService.findUserById(comment.getUserId()));

                //点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeCount",likeCount);
                //点赞状态
                //首先判断，因为如果没有登录的话，是也能看，但点赞状态是未赞，为0
                 likeStatus =hostHolder.getUser() == null ? 0: likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);

                //回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                //回复VO的列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if (replyList != null){
                    for (Comment reply: replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        //添加回复和作者
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);

                        //点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeCount",likeCount);
                        //点赞状态
                        //首先判断，因为如果没有登录的话，是也能看，但点赞状态是未赞，为0
                        likeStatus =hostHolder.getUser() == null ? 0: likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                //要把replyVoList装到commentVo中，否则就丢了
                commentVo.put("replys",replyVoList);

                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);

        return "/site/discuss-detail";
    }

}
