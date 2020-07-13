package com.nowcoder.community.controller.Interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断handler是不是HandlerMethod里面的一员
        if(handler instanceof HandlerMethod){
            //转型
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取到method对象
            Method method =  handlerMethod.getMethod();
            //尝试去取注解
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            //当前方法需要登录，但是你又没有登录
            if (loginRequired != null && hostHolder.getUser() == null){//不为空，表示需要登陆访问
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }

        }
        return true;
    }
}
