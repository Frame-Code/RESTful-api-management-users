
package com.firstSpring.firstSpring.controllers;

import com.firstSpring.firstSpring.dto.UserWithPasswordDTO;
import com.firstSpring.firstSpring.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Artist-Code
 */
@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping
    public String login(@RequestBody UserWithPasswordDTO userDTO) {
        return (authService.checkCredentials(userDTO)) ? "Ok" : "fail";
    }
}
