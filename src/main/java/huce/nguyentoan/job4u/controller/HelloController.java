package huce.nguyentoan.job4u.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class HelloController {
    @GetMapping("/")
    public String hello() {
        return "Hello test";
    }
    
}
