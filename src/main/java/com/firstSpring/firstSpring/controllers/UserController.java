package com.firstSpring.firstSpring.controllers;

import com.firstSpring.firstSpring.dto.EditUser;
import com.firstSpring.firstSpring.dto.UserResponse;

import com.firstSpring.firstSpring.service.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *  Controller to define the different endpoints to manage the 'user':
 *  we have:
 *  Endpoint 'GET /api/users' to return a json with all users from the database
 *  Endpoint 'GET /api/users/{id}' to return a json with the user searching from that 'id'
 *  Endpoint 'POST /api/users/edit' to update the user from the body of the request
 *  Endpoint 'POST /api/users/reset/{id}' to reset the password of the user
 *  Endpoint 'GET /api/users/search' to search users using name or email (the value to be searched must be in the URI parameters )
 *  Endpoint 'DELETE /api/users/{id}' to 'delete' the user from the database (really is a soft delete. changing the attribute deleted on the respective table)
 * @author Daniel Mora Cantillo
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
        var responseOpt = userService.findById(id);

        return responseOpt.isEmpty()?
                ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error getting user info") :
                ResponseEntity.ok(responseOpt.get());
    }

    @PostMapping(value = "/edit")
    public ResponseEntity<?> editUser(@RequestBody final EditUser editUser) {
        if(editUser.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id parameter not recognize");
        }

        if(editUser.getRoles() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Roles parameter no recognize");
        }

        return userService.editUser(editUser).isEmpty()?
                ResponseEntity.status(HttpStatus.NO_CONTENT).body("User not saved") :
                ResponseEntity.status(HttpStatus.OK).body("User edited correctly");
    }

    @PostMapping(value = "/reset/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable final Long id) {
        var responseOpt = userService.resetPassword(id);
        return responseOpt.isEmpty()?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found") :
                ResponseEntity.ok(responseOpt.get());
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
