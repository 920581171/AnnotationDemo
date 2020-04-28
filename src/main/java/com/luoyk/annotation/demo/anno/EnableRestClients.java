package com.luoyk.annotation.demo.anno;

import com.luoyk.annotation.demo.register.RestClientsRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) //运行时保留注解
@Target(ElementType.TYPE) //用于类上
@Import(RestClientsRegister.class) //这里是关键，这里告诉了Spring在开始扫描的时候所执行的类
public @interface EnableRestClients {
    //指定所扫描的包
    String[] basePackages() default {};
}
