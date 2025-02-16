package com.firstSpring.firstSpring.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Artist-Code
 */
@RestController
public class UserController {
    
    @RequestMapping(value = "test")
    public String test() {
        return "Testing it";
    }
    
}
