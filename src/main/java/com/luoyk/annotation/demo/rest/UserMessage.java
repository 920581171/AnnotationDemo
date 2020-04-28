package com.luoyk.annotation.demo.rest;

import com.luoyk.annotation.demo.anno.RestClient;
import org.springframework.web.bind.annotation.PostMapping;

@RestClient
public interface UserMessage {
    @PostMapping("/getOne")
    String method1(int param1,int param2);
}
