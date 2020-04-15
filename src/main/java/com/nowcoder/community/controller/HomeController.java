package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Controller的访问路径可以省略
@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    //通过userService,把user的详细数据查到
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index" ,method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){//通过model携带数据给模板

        //方法调用之前，SpringMVC会自动实例化Model和Page，并将Page注入给Model
        //所以，在thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

       List<DiscussPost> list= discussPostService.findDiscussPosts(0,page.getoffset(),page.getLimit());
       //遍历一下DiscussPost，将查到的数据userId，查到user，将数据组装，得到用户名
        //新建集合，可以封装DiscussPost与user对象的一个集合
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if(list!=null){
            for (DiscussPost post:list){
                //遍历，把结果装到Map中
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                //得到用户
               User user= userService.findUserById(post.getUserId());
               map.put("user",user);
                //把结果装到map中
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";//返回index.html
    }

}
