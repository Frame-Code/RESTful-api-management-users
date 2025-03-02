package com.firstSpring.firstSpring.service;

import com.firstSpring.firstSpring.FirstSpringApplication;
import com.firstSpring.firstSpring.dto.TokenResponse;
import com.firstSpring.firstSpring.dto.UserDTO;
import com.firstSpring.firstSpring.dto.UserLogin;
import com.firstSpring.firstSpring.dto.UserRegister;
import com.firstSpring.firstSpring.model.Token;
import com.firstSpring.firstSpring.model.TokenType;
import com.firstSpring.firstSpring.model.UserEntity;
import com.firstSpring.firstSpring.repository.TokenRepository;
import com.firstSpring.firstSpring.repository.UserRepository;
import com.firstSpring.firstSpring.service.mappers.UserMapper;

import java.util.ArrayList;
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
import org.springframework.security.core.context.SecurityContextHolder;
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
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

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
            SecurityContextHolder.getContext().setAuthentication(auth);

            Optional<String> jwtToken = jwtUtils.createToken(auth);
            Optional<String> refreshToken = jwtUtils.createRefreshToken(auth);
            if (jwtToken.isEmpty() || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Error creating the token");
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

    public TokenResponse register(UserRegister userRegister) {
        /*UserEntity user = userMapper.toEntity(userRegister); //Mapea el dto a un UserEntity
        user.setPassword(passwordEncoder.encode(userRegister.getPassword())); //Le setea la constraseña encriptada
        UserEntity savedUser = userRepository.save(user); //Guarda el UserEntity en la db

        String jwtToken = jwtService.generateToken(user); //Genera el acces tokenn
        String refreshToken = jwtService.generateRefreshToken(user); //Genera el refresh token
        saveUserToken(savedUser, jwtToken); //Genera la instancia de tipo Token y la guarda en la db
        return new TokenResponse(jwtToken, refreshToken); //Retorna una instancia de tipo TokenResponse que se termina volviendo al front como json

         */
        return null;
    }

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
