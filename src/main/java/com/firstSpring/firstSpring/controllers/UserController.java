package com.firstSpring.firstSpring.controllers;

import com.firstSpring.firstSpring.dto.UserResponse;

import com.firstSpring.firstSpring.service.UserService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> findUserByID(@PathVariable final Long id) {
        Optional<UserResponse> responseOpt = userService.findById(id);
        if(responseOpt.isEmpty()) {
            return new ResponseEntity<>(Optional.empty(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(responseOpt, HttpStatus.OK);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<?> findUserByNameOrEmail(@RequestParam final String value) {
        List<UserResponse> userList = userService.findByNameOrEmail(value.trim().toLowerCase());
        return (!userList.isEmpty())?
                ResponseEntity.ok(userList) :
                ResponseEntity.status(HttpStatus.NO_CONTENT).body("Not users found");
    }
    
    
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable final Long id) {
        userService.softDeleteById(id);
    }
}
