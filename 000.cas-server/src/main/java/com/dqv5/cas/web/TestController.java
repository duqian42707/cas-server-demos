package com.dqv5.cas.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author duq
 * @date 2024/5/6
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/test1")
    public String test1() {
        return "test1-ok3";
    }
}
