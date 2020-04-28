package com.luoyk.annotation.demo;

import com.luoyk.annotation.demo.anno.EnableRestClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRestClients
public class AnnotationDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotationDemoApplication.class, args);
    }

}
