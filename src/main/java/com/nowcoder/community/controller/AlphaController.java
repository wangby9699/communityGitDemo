package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }


    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //读取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());//路径
        //得到所有请求行的key,给迭代器
        //请求若干行
        Enumeration<String> enumeration=request.getHeaderNames();
        while(enumeration.hasMoreElements()){//遍历
            String name=enumeration.nextElement();
            String value=request.getHeader(name);
            System.out.println(name+":"+value);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据，先设置返回类型
        response.setContentType("text/html;charset=utf-8");
        try (
                PrintWriter writer=response.getWriter();
                ) {

            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //GET请求
    //查询学生，分页显示，当前第1页，每一页最多显示20
    //  /students?current=1&&limit=20
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name="current",required = false,defaultValue = "1") int current,
            @RequestParam(name="limit",required = false ,defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";

    }

    //   /students/123 待获取的参数成为路径的一部分
    @RequestMapping(path = "/students/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(
            @PathVariable("id") int id
    ){
        System.out.println(id);
        return "a student";
    }

    //POST请求
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String  saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应HTML数据
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    //不加@ResponseBody,默认返回html
    public ModelAndView getTeacher(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("name","张三");
        modelAndView.addObject("age",30);
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    //只传了view，由于没有Model，因此我们自己声明
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","东华大学");
        model.addAttribute("age",69);
        return "/demo/view";
    }

    //响应JSON数据（异步请求：当前网页不刷新，但已经悄悄访问过数据库并得到消息了，例如提示用户名已存在）
    //Java面向对象  通过JSON，转为字符串，再给JS对象

    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp=new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",8000.00);
        return emp;
    }

    //查询所有员工
    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>>  list=new ArrayList<>();

        Map<String,Object> emp=new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",8000.00);
        list.add(emp);

        emp=new HashMap<>();
        emp.put("name","李四");
        emp.put("age",20);
        emp.put("salary",6500.00);
        list.add(emp);

        emp=new HashMap<>();
        emp.put("name","王五");
        emp.put("age",25);
        emp.put("salary",10000.00);
        list.add(emp);
        return list;
    }

    //cookie示例
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建cookie
        Cookie cookie=new Cookie("code", CommunityUtil.generateUUID());
        //设置cookie生效范围
        cookie.setPath("/community/alpha");
        //设置cookie生存时间（默认关掉浏览器，cookie失效，若设置了生存时间，会存在硬盘中）
        //单位是秒，本次设置10分钟
        cookie.setMaxAge(60*10);
        //发送cookie,添加到response头中
        response.addCookie(cookie);
        return  "set cookie";
    }


    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }


    //session示例
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session";
    }

    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    //AJAX示例
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功");
    }

}
