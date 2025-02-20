package com.firstSpring.firstSpring.service;

import com.firstSpring.firstSpring.dto.TokenResponse;
import com.firstSpring.firstSpring.dto.UserWithPasswordDTO;
import com.firstSpring.firstSpring.model.User;
import com.firstSpring.firstSpring.repository.TokenRepository;
import com.firstSpring.firstSpring.repository.UserRepository;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Artist-Code
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    Argon2 argon = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    public boolean isRegister(UserWithPasswordDTO userDTO) {
        Optional<User> userOpt = userRepository.findByEmail(userDTO.getEmail());
        if (userOpt.isEmpty()) {
            return false;
        }

        return argon.verify(userOpt.get().getPassword(), userDTO.getPassword().toCharArray());

    }

    public TokenResponse register(UserWithPasswordDTO userDTO) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public TokenResponse login(UserWithPasswordDTO userDTO) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public TokenResponse refreshToken(String authHeader) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
