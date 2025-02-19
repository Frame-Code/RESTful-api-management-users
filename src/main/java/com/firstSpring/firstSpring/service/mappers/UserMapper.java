package com.firstSpring.firstSpring.service.mappers;

import com.firstSpring.firstSpring.dto.UserWithPasswordDTO;
import com.firstSpring.firstSpring.dto.UserDTO;
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

    //Map User to UserDTO
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    UserDTO toDTO(User user);

    @Mapping(target = "password", source = "password")
    UserWithPasswordDTO toUserCreateDTO(User user);

    @InheritInverseConfiguration(name = "toUserCreateDTO")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserWithPasswordDTO userCreateDTO);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserDTO userDTO);

}
