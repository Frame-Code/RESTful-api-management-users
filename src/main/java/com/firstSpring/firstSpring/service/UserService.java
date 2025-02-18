package com.firstSpring.firstSpring.service;

import com.firstSpring.firstSpring.dto.UserDTO;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firstSpring.firstSpring.model.User;
import com.firstSpring.firstSpring.repository.UserRepository;
import com.firstSpring.firstSpring.service.mappers.UserMapper;
import java.time.LocalDate;

@Service
public class UserService {

    @Autowired
    private UserRepository  userRepository;

    private final UserMapper userMapper = UserMapper.INSTANCE;
    
    public List<User> findAll() {
        return userRepository.findAllActiveUsers();
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findByIdActive(id).orElseThrow(() ->
                new RuntimeException("User not found")
        );
        return userMapper.toDTO(user);
    }

    public User save(User user) {
        user.setCreatedAt(LocalDate.now());
        user.setDeleted(false);
        return userRepository.save(user);
    }
    
    public void softDeleteById(Long id) {
        userRepository.softDeleteById(id);;
    }
    

}
