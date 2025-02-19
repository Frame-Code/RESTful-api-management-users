package com.firstSpring.firstSpring.controllers;

import com.firstSpring.firstSpring.dto.UserCreateDTO;
import com.firstSpring.firstSpring.dto.UserDTO;

import com.firstSpring.firstSpring.service.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Artist-Code
 */
@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    @GetMapping(value = "/{id}")
    public UserDTO findUserByID(@PathVariable Long id) {
        return userService.findById(id);
    }
    
    @PostMapping
    public UserDTO createUser(@RequestBody UserCreateDTO user) {
        return userService.save(user);
    }
    
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.softDeleteById(id);
    }
}
