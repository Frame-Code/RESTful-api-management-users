package com.firstSpring.firstSpring.service;

import com.firstSpring.firstSpring.dto.EditUser;
import com.firstSpring.firstSpring.dto.UserResponse;

import java.util.HashSet;
import java.util.List;

import com.firstSpring.firstSpring.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

/** Service to manage all actions related with the users as find all, find by id, reset password, find a value X, or softDeleted
 *
 * @author Daniel Mora Cantillo
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final String defaultPassword = "default123";

    private static final Logger LOG = Logger.getLogger(UserService.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    /** Method to find all users from the db
     *
     * @return a list of users type UserResponse dto
     * */
    public List<UserResponse> findAll() {
        return userRepository.findAllActiveUsers()
                .stream()
                .map(UserMapper.INSTANCE::toUserResponse)
                .collect(Collectors.toList());
    }

    /** Method to find a user by id
     *
     * @return an Optional type EditUser dto
     * @param id to search
     * */
    public Optional<EditUser> findById(final Long id) {
        return userRepository.findByIdActive(id)
                .map(userMapper::toEditUser);
    }

    /** Method to reset by default the password of a user obtained by his id
     *
     * @return an Optional to will be Empty or with the default password
     * @param id to search
     * */
    public Optional<?> resetPassword(final Long id) {
        return userRepository.findByIdActive(id)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(UserService.defaultPassword));
                    return userRepository.save(user);
                })
                .map(user -> {
                    LOG.log(Level.INFO, "Password of the user with the following email: " + user.getEmail() + " has been reset correctly");
                    return Optional.of(UserService.defaultPassword);
                });
    }

    /** Method to find a user searching the value in the attribute name or email
     *
     * @return a list type UserResponse dto with all users to matches with it value
     * @param value to search
     * */
    public List<UserResponse> findByNameOrEmail(final String value) {
        return userRepository.findByNameOrEmail(value.transform( string -> "%" + string + "%"))
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(userMapper::toUserResponse)
                .toList();

    }

    /** Method to edit data user using EditUser dto
     *
     * @return an Optional type EditUser dto with the data persisted using EditUser param dto
     * @param editUser dto with the important data to edit a user (including the id respective)
     * */
    public Optional<EditUser> editUser(@NotNull final EditUser editUser) {
        return userRepository.findByIdActive(editUser.getId())
                .map(user -> {
                    user.setRoles(new HashSet<>(roleRepository.findByNames(editUser.getRoles().stream().toList())));
                    user.setName(editUser.getName());
                    user.setLastName(editUser.getLastName());
                    user.setPhone(editUser.getPhone());
                    return userRepository.save(user);
                })
                .map(userMapper::toEditUser);
    }

    /** Method to softly delete a user using his id
     *
     * @param id to search
     * */
    public void softDeleteById(Long id) {
        userRepository.softDeleteById(id);
        LOG.log(Level.INFO, "User with the following id " + id.toString() + "has been deleted correctly");
    } 

}
