package com.firstSpring.firstSpring.service.mappers;

import com.firstSpring.firstSpring.dto.UserDTO;
import com.firstSpring.firstSpring.dto.UserLogin;
import com.firstSpring.firstSpring.dto.UserRegister;
import com.firstSpring.firstSpring.dto.UserResponse;
import com.firstSpring.firstSpring.model.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 *
 * @author Artist-Code
 */
@Mapper
public interface UserMapper {

    public UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    //Map UserEntity to DTO
    UserDTO toDTO(UserEntity user);

    UserRegister toUserRegister(UserEntity user);

    UserLogin toUserLogin(UserEntity user);

    UserResponse toUserResponse(UserEntity user);

    @InheritInverseConfiguration(name = "toUserRegister")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "tokens", ignore = true)
    @Mapping(target = "roles", ignore = true)
    UserEntity toEntity(UserRegister userRegister);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    UserEntity toEntity(UserDTO userDTO);

    @InheritInverseConfiguration(name = "toUserLogin")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "tokens", ignore = true)
    UserEntity toEntity(UserLogin userLogin);
    
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "tokens", ignore = true)
    UserEntity toEntity(UserResponse userResponse);

}
