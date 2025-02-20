package com.firstSpring.firstSpring.service;

import com.firstSpring.firstSpring.dto.UserWithPasswordDTO;
import com.firstSpring.firstSpring.dto.UserDTO;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firstSpring.firstSpring.model.User;
import com.firstSpring.firstSpring.repository.UserRepository;
import com.firstSpring.firstSpring.service.mappers.UserMapper;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final Argon2 argon = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    private final UserMapper userMapper = UserMapper.INSTANCE;

    public List<UserDTO> findAll() {
        return userRepository.findAllActiveUsers().stream()
                .map(UserMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findByIdActive(id).orElseThrow(()
                -> new RuntimeException("User not found")
        );
        return userMapper.toDTO(user);
    }

    public UserDTO save(UserWithPasswordDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        user.setPassword(
                argon.hash(1, 1024, 1, userDTO.getPassword().toCharArray())
        );
        user.setCreatedAt(LocalDate.now());
        user.setDeleted(false);
        return userMapper.toDTO(userRepository.save(user));
    }

    public void softDeleteById(Long id) {
        userRepository.softDeleteById(id);
    }

}
