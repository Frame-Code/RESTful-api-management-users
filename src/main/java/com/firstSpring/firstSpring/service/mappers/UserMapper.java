package com.firstSpring.firstSpring.service.mappers;

import com.firstSpring.firstSpring.dto.UserDTO;
import com.firstSpring.firstSpring.dto.UserLogin;
import com.firstSpring.firstSpring.dto.UserRegister;
import com.firstSpring.firstSpring.dto.UserResponse;
import com.firstSpring.firstSpring.model.User;
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

    //Map User to DTO
    UserDTO toDTO(User user);

    UserRegister toUserRegister(User user);

    UserLogin toUserLogin(User user);
    
    UserResponse toUserResponse(User user);

    @InheritInverseConfiguration(name = "toUserRegister")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "tokens", ignore = true)
    User toEntity(UserRegister userRegister);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserDTO userDTO);

    @InheritInverseConfiguration(name = "toUserLogin")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "tokens", ignore = true)
    User toEntity(UserLogin userLogin);
    
    User toEntity(UserResponse userResponse);

}
