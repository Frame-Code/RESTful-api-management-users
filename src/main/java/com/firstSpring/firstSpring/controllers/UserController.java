package com.firstSpring.firstSpring.controllers;

import com.firstSpring.firstSpring.model.User;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Artist-Code
 */
@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    @GetMapping(value = "/{id}")
    public User findUserByID(@PathVariable Long id) {
        return new User("Daniel", "Mora", "mail@mail.com", "000000000", "password1");
    }

    @GetMapping
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        users.add(new User("Daniel", "Mora", "mail@mai.com", "0099999", "pasw"));
        users.add(new User("Isur", "Cantillo", "mail2@mail.com", "000000000", "password1"));
        return users;
    }
}
