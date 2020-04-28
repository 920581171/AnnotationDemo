package com.luoyk.annotation.demo.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) //运行时保留注解
@Target(ElementType.TYPE) //注解用在类上
public @interface RestClient {
}