package com.luoyk.annotation.demo.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * jdk代理类处理程序
 *
 * @author 92058
 */
public class JdkHandler implements InvocationHandler {
    /**
     * 每一次调用接口的方法都会进入到invoke方法中
     * 我们可以根据method.getName()和args来判断调用了哪个方法
     * 通过method.getReturnType()来判断当前方法应该返回什么内容
     * 通过method.getAnnotations()可以获得方法上的注解，这样我们就可以通过注解传递数据了
     * <p>
     * 注意因为我们是代理接口，所以不能像平时反射那样调用method.invoke(proxy,args),会导致无限循环
     * invoke中的内容是接口的具体实现，应该直接返回结果
     *
     * @param proxy  代理后的对象
     * @param method 当前执行的方法
     * @param args   当前执行方法的形式参数
     * @return 当前调用方法所返回的内容
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return "调用了" + method.getName() + "方法" +
                "，参数" + Arrays.toString(args) +
                "，方法上的注解：" + Arrays.toString(Arrays.stream(method.getAnnotations())
                .map(annotation -> annotation.annotationType().getSimpleName())
                .toArray());
    }
}
