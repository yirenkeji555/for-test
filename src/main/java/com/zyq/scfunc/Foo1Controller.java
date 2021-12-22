package com.zyq.scfunc;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@AllArgsConstructor
@RequestMapping("/foo1")
public class Foo1Controller {

//    private final FooService fooService;

    @GetMapping("/send1")
    public String sendSmsCode() {
        return "OK";
    }

}