package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//该注解表示只扫描带有Controller注解的bean
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常：" + e.getMessage());
        //遍历数组，得到对象，写进日志记录
        for (StackTraceElement element : e.getStackTrace()){
            logger.error(element.toString());
        }

        //判断请求是普通请求还是异步请求
        //请求的方式
        String xRequestedWith = request.getHeader("x-requested-with");
        //说明是异步请求,XML
        if ("XMLHttpRequest".equals(xRequestedWith)){
            //plain表示向浏览器返回的是普通字符串，可以是json格式，浏览器得到之后需要人为的进行转换  $.parseJSON()方法
            response.setContentType("application/plain;charset = utf-8");
            PrintWriter writer = response.getWriter();
            //向外输出内容
            writer.write(CommunityUtil.getJSONString(1,"服务器异常！"));
        }else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
