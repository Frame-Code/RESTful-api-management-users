package com.firstSpring.firstSpring.service;

import com.firstSpring.firstSpring.dto.TokenResponse;
import com.firstSpring.firstSpring.dto.UserLogin;
import com.firstSpring.firstSpring.dto.UserRegister;
import com.firstSpring.firstSpring.model.Role;
import com.firstSpring.firstSpring.model.Token;
import com.firstSpring.firstSpring.model.TokenType;
import com.firstSpring.firstSpring.model.UserEntity;
import com.firstSpring.firstSpring.repository.RoleRepository;
import com.firstSpring.firstSpring.repository.TokenRepository;
import com.firstSpring.firstSpring.repository.UserRepository;
import com.firstSpring.firstSpring.service.mappers.UserMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.firstSpring.firstSpring.utils.JwtUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Artist-Code
 */
@Service
public class AuthService {
    private static final Logger LOG = Logger.getLogger(AuthService.class.getName());
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

    /****************************************PRINCIPAL METHODS OF THIS CLASS*************************************/
    public ResponseEntity<?> login(@NotNull UserLogin userDTO) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userDTO.getEmail());

            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            if (!passwordEncoder.matches(userDTO.getPassword(), userDetails.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect Password");
            }

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );

            Optional<String> jwtToken = jwtUtils.createToken(auth);
            Optional<String> refreshToken = jwtUtils.createRefreshToken(auth);
            if (jwtToken.isEmpty() || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating the token");
            }
            revokeAllTokens(userMapper.toEntity(userDTO));
            saveUserToken(userRepository.findByEmail(userDTO.getEmail()).get(), jwtToken.get());
            LOG.log(Level.INFO, "User: " + userDetails.getUsername() + " with the following authorities: " + userDetails.getAuthorities().toString() + " logged successfully");
            return ResponseEntity.ok(new TokenResponse(jwtToken.get(), refreshToken.get()));
        } catch (AuthenticationException e) {
            LOG.log(Level.SEVERE, e.getMessage(), "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    public ResponseEntity<?> register(@NotNull UserRegister userRegister) {
        if(userRepository.findByEmail(userRegister.getEmail()).isPresent()) {
            LOG.log(Level.SEVERE, "The user with the email " + userRegister.getEmail() + "already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The user with the email " + userRegister.getEmail() + "already exists");
        }

        if (userRegister.getRolesRegister().getRoleListName().isEmpty()) {
            LOG.log(Level.WARNING, "Roles not identified on the request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Roles not identified on the request");
        }

        List<String> roles = roleRepository.findAllActive().stream()
                .map(role -> role.getRoleEnum().name())
                .toList();

        List<String> userRoles = userRegister.getRolesRegister().getRoleListName().stream()
                .filter(roles::contains)
                .map(String::toUpperCase)
                .toList();

        if (userRoles.isEmpty()) {
            LOG.log(Level.SEVERE, "Roles not identified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Roles not identified");
        }

        List<Role> roleList = roleRepository.findByNames(userRoles);

        UserEntity user = userMapper.toEntity(userRegister);
        user.setPassword(passwordEncoder.encode(userRegister.getPassword()));
        user.setRoles(new HashSet<>(roleList));

        UserEntity savedUser = userRepository.save(user);

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        //ALWAYS is necessary add the string "ROLE_" before the name of "real role"
        savedUser.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));
        savedUser.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionEnum().name())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getEmail(),
                null,
                authorityList
        );

        Optional<String> jwtToken = jwtUtils.createToken(authentication);
        Optional<String> refreshToken = jwtUtils.createRefreshToken(authentication);

        if(jwtToken.isEmpty() || refreshToken.isEmpty()) {
            LOG.log(Level.SEVERE, "Error creating the token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating the token");
        }

        saveUserToken(savedUser, jwtToken.get());
        return ResponseEntity.ok().body(new TokenResponse(jwtToken.get(), refreshToken.get()));
    }

    public TokenResponse refreshToken(String authHeader) {
        return null;
    }

    private void saveUserToken(UserEntity user, String jwtToken) {
        tokenRepository.save(
                Token.builder()
                        .user(user)
                        .token(jwtToken)
                        .tokenType(TokenType.BEARER)
                        .expired(false)
                        .revoked(false)
                        .build()
        );

    }

    private void revokeAllTokens(UserEntity user) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(user.getEmail());
        userOpt.ifPresent(userEntity -> {
            userEntity.getTokens().forEach(token -> token.setRevoked(true));
            userRepository.saveAndFlush(userEntity);
        });
    }

    /***********************METHODS USED TO ADD USERS ON THE DB WHEN THE APPLICATION IS CHARGING*******************
    *************************************USED IN THE MAIN CLASS***************************************************/
    public TokenResponse registerTest(UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); //Le setea la constraseña encriptada
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        //A la lista anterior se agrega todos los roles (defined by the context)  agregandole un objeto que representa ese rol
        user.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        //Por cada rol (N roles) se accede a cada permiso que tenga asignado el rol (N permisos, for that is used flatMap) y agrega ese permiso a la lista
        user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionEnum().name())));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword(),
                authorityList
        );

        Optional<String> jwtToken = jwtUtils.createToken(auth);
        Optional<String> refreshToken = jwtUtils.createRefreshToken(auth);
        if (jwtToken.isEmpty() || refreshToken.isEmpty()) {
            return null;
        }

        return new TokenResponse(jwtToken.get(), refreshToken.get()); //Retorna una instancia de tipo TokenResponse que se termina volviendo al front como json
    }

    public Token createToken(UserEntity user, String jwtToken) {
        return Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
    }
}
