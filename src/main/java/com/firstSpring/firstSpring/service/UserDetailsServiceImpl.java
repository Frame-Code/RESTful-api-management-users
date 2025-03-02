package com.firstSpring.firstSpring.service;

import com.firstSpring.firstSpring.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.firstSpring.firstSpring.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

/*
 *
 * @author Artist-Code
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger LOG = Logger.getLogger(UserDetailsServiceImpl.class.getName());
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Searching the user on the db
        Optional<UserEntity> userOpt = userRepository.findByEmail(username);

        if (userOpt.isEmpty()) {
            LOG.log(Level.WARNING, "Email not found");
            return null;
        }

        UserEntity userEntity = userOpt.get();
        //Definicion de una lista con el objeto que representa el rol y permiso del usuario (Clase que entiende el contexto de spring)
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        //A la lista anterior se agrega todos los roles (defined by the context)  agregandole un objeto que representa ese rol
        userEntity.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        //Por cada rol (N roles) se accede a cada permiso que tenga asignado el rol (N permisos, for that is used flatMap) y agrega ese permiso a la lista
        userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionEnum().name())));

        return new User(
                userEntity.getEmail(),
                userEntity.getPassword(),
                authorityList
        );
    }

}
