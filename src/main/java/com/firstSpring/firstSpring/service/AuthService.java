
package com.firstSpring.firstSpring.service;

import com.firstSpring.firstSpring.dto.UserWithPasswordDTO;
import com.firstSpring.firstSpring.dto.UserDTO;
import com.firstSpring.firstSpring.model.User;
import com.firstSpring.firstSpring.repository.UserRepository;
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
    
    public boolean checkCredentials(UserWithPasswordDTO userDTO) {
        Optional<User> user = userRepository.findUser(userDTO.getEmail(), userDTO.getPassword());
        return !user.isEmpty();
    }
}
