package com.firstSpring.firstSpring.controllers;

import com.firstSpring.firstSpring.dto.UserResponse;

import com.firstSpring.firstSpring.service.UserService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;

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

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> users = userService.findAll();
        if(users.isEmpty()) { 
            return new ResponseEntity<>(users, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findUserByID(@PathVariable Long id) {
        Optional<UserResponse> responseOpt = userService.findById(id);
        if(responseOpt.isEmpty()) {
            return new ResponseEntity<>(Optional.empty(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(responseOpt, HttpStatus.OK);
    }
    
    
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.softDeleteById(id);
    }
}
