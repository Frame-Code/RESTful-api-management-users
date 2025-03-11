package com.firstSpring.firstSpring.service;

import com.firstSpring.firstSpring.dto.UserResponse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firstSpring.firstSpring.repository.UserRepository;
import com.firstSpring.firstSpring.service.mappers.UserMapper;

import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final UserMapper userMapper;

    public List<UserResponse> findAll() {
        return userRepository.findAllActiveUsers()
                .stream()
                .map(UserMapper.INSTANCE::toUserResponse)
                .collect(Collectors.toList());
    }

    public Optional<UserResponse> findById(Long id) {
        return userRepository.findByIdActive(id).map(userMapper::toUserResponse);
    }

    public List<UserResponse> findByNameOrEmail(String value) {
        return userRepository.findByNameOrEmail(
                value.transform( string -> "%" + string + "%"))
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(userMapper::toUserResponse)
                .toList();

    }

    public void softDeleteById(Long id) {
        userRepository.softDeleteById(id);
    } 

}
