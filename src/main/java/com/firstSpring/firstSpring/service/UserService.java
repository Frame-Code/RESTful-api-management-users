package com.firstSpring.firstSpring.service;

import com.firstSpring.firstSpring.dto.GetInfoUser;
import com.firstSpring.firstSpring.dto.UserResponse;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.firstSpring.firstSpring.repository.UserRepository;
import com.firstSpring.firstSpring.service.mappers.UserMapper;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final String defaultPassword = "default123";

    private static final Logger LOG = Logger.getLogger(UserService.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;


    public List<UserResponse> findAll() {
        return userRepository.findAllActiveUsers()
                .stream()
                .map(UserMapper.INSTANCE::toUserResponse)
                .collect(Collectors.toList());
    }

    public Optional<GetInfoUser> findById(final Long id) {
        return userRepository.findByIdActive(id)
                .map(userMapper::toGetInfoUser);
    }

    public Optional<?> resetPassword(final Long id) {
        var responseOpt = userRepository.findByIdActive(id);

        if (responseOpt.isEmpty()) {
            return Optional.empty();
        }

        responseOpt.get().setPassword(passwordEncoder.encode(UserService.defaultPassword));
        userRepository.save(responseOpt.get());
        LOG.log(Level.INFO, "Password of the user with the following email: " + responseOpt.get().getEmail() + " has been reset correctly");
        return Optional.of(defaultPassword);
    }

    public List<UserResponse> findByNameOrEmail(final String value) {
        return userRepository.findByNameOrEmail(value.transform( string -> "%" + string + "%"))
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(userMapper::toUserResponse)
                .toList();

    }

    public void softDeleteById(Long id) {
        userRepository.softDeleteById(id);
        LOG.log(Level.INFO, "User with the following id " + id.toString() + "has been deleted correctly");
    } 

}
