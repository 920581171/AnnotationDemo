package com.luoyk.annotation.demo;

import com.luoyk.annotation.demo.rest.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private final UserMessage userMessage;

    //注意这里编译器可能会报红，因为我们是使用手动注册Bean，所以编译器无法检测，实际可以通过编译
    @Autowired
    public TestController(UserMessage userMessage) {
        this.userMessage = userMessage;
    }

    @GetMapping("/method1")
    public String test() {
        return userMessage.method1(123,456);
    }
}
