package com.firstSpring.firstSpring.service;

import com.firstSpring.firstSpring.dto.TokenResponse;
import com.firstSpring.firstSpring.dto.UserLogin;
import com.firstSpring.firstSpring.dto.UserRegister;
import com.firstSpring.firstSpring.model.Token;
import com.firstSpring.firstSpring.model.User;
import com.firstSpring.firstSpring.repository.TokenRepository;
import com.firstSpring.firstSpring.repository.UserRepository;
import com.firstSpring.firstSpring.service.mappers.UserMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Artist-Code
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public TokenResponse register(UserRegister userRegister) {
        User user = userMapper.toEntity(userRegister); //Mapea el dto a un User
        user.setPassword(passwordEncoder.encode(userRegister.getPassword())); //Le setea la constraseña encriptada
        User savedUser = userRepository.save(user); //Guarda el User en la db
        
        
        String jwtToken = jwtService.generateToken(user); //Genera el acces tokenn
        String refreshToken = jwtService.generateRefreshToken(user); //Genera el refresh token
        saveUserToken(savedUser, jwtToken); //Genera la instancia de tipo Token y la guarda en la db
        return new TokenResponse(jwtToken, refreshToken); //Retorna una instancia de tipo TokenResponse que se termina volviendo al front como json
    }

    public TokenResponse login(UserLogin userDTO) {
        return null;
    }

    public TokenResponse refreshToken(String authHeader) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private void saveUserToken(User user, String jwtToken) {
        tokenRepository.save(
        Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .expired(false)
                .revoked(false) 
                .build()
        );
                
    }
}
