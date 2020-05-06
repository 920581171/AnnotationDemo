package com.luoyk.annotation.demo.handler;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * CGlib代理类处理程序
 *
 * @author 92058
 */
public class CGLibHandler implements MethodInterceptor {
    /**
     * @param o           代理对象
     * @param method      代理方法
     * @param objects     代理方法形惨
     * @param methodProxy 被代理对象
     * @return 当前调用方法所返回的内容
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return "调用了" + method.getName() + "方法" +
                "，参数" + Arrays.toString(objects) +
                "，方法上的注解：" + Arrays.toString(Arrays.stream(method.getAnnotations())
                .map(annotation -> annotation.annotationType().getSimpleName())
                .toArray());
    }
}
