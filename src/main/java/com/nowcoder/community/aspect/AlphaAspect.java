package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//真正用到该知识点的时候要写这个注解的，本次因为不想每次都出这个，它只是个demo，才注释掉的
//@Component
//@Aspect
public class AlphaAspect {

    //第一个*，代表方法的返回值,第二个表示所有类，第三个表示所有方法  (..)表示所有参数
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut(){

    }


    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }


    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }

    //在返回值以后再处理
    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }

    //在抛异常的时候再处理
    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("AfterThrowing");
    }

    //既想在前，也想在后
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        //得到目标组件
        Object obj = joinPoint.proceed();
        System.out.println("around after");
        return obj;
    }

}
