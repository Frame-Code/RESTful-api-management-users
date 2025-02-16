package com.firstSpring.firstSpring.controllers;

import com.firstSpring.firstSpring.model.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Artist-Code
 */
@RestController
public class UserController {

    @RequestMapping(value = "/api/users/{id}")
    public User findUserByID(@PathVariable Long id) {
        return new User("Daniel", "Mora", "mail@mail.com", "000000000", "password1");
    }
}
