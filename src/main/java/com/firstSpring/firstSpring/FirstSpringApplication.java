package com.firstSpring.firstSpring;

import com.firstSpring.firstSpring.dto.TokenResponse;
import com.firstSpring.firstSpring.model.Permission;
import com.firstSpring.firstSpring.model.PermissionsEnum;
import com.firstSpring.firstSpring.model.Role;
import com.firstSpring.firstSpring.model.RoleEnum;
import com.firstSpring.firstSpring.model.UserEntity;
import com.firstSpring.firstSpring.repository.TokenRepository;
import com.firstSpring.firstSpring.repository.UserRepository;
import com.firstSpring.firstSpring.service.AuthService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.List;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication()
public class FirstSpringApplication {

    private static final Logger LOG = Logger.getLogger(FirstSpringApplication.class.getName());

    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry
                    -> System.setProperty(entry.getKey(), entry.getValue())
            );
            LOG.log(Level.INFO, "Environment variables loaded correctly!");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error loading .env: ".concat(e.getMessage()));
        }
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository, TokenRepository tokenRepository, AuthService authService) {
        return args -> {
            //Creating permissions
            Permission permissionCreate = Permission.builder()
                    .permissionEnum(PermissionsEnum.CREATE)
                    .build();

            Permission permissionRead = Permission.builder()
                    .permissionEnum(PermissionsEnum.READ)
                    .build();

            Permission permissionUpdate = Permission.builder()
                    .permissionEnum(PermissionsEnum.UPDATE)
                    .build();

            Permission permissionDelete = Permission.builder()
                    .permissionEnum(PermissionsEnum.DELETE)
                    .build();

            //creating roles
            Role roleAdmin = Role.builder()
                    .roleEnum(RoleEnum.ADMIN)
                    .permissions(Set.of(permissionCreate, permissionRead, permissionUpdate, permissionDelete))
                    .build();

            Role roleUser = Role.builder()
                    .roleEnum(RoleEnum.USER)
                    .permissions(Set.of(permissionCreate, permissionRead))
                    .build();

            Role roleInvited = Role.builder()
                    .roleEnum(RoleEnum.INVITED)
                    .permissions(Set.of(permissionRead))
                    .build();

            Role roleDev = Role.builder()
                    .roleEnum(RoleEnum.DEVELOPER)
                    .permissions(Set.of(permissionCreate, permissionDelete, permissionRead, permissionUpdate))
                    .build();

            //creating users
            UserEntity user1 = UserEntity.builder()
                    .name("Daniel")
                    .lastName("Mora")
                    .email("mail@email.com")
                    .phone("0011111")
                    .password("pass")
                    .roles(Set.of(roleAdmin, roleDev))
                    .build();
            TokenResponse token = authService.registerTest(user1);
            user1.setTokens(List.of(authService.createToken(user1, token.getAccesToken())));

            UserEntity user2 = UserEntity.builder()
                    .name("Isur")
                    .lastName("Cantillo")
                    .email("mail1@email.com")
                    .phone("00111112")
                    .password("pass")
                    .roles(Set.of(roleUser))
                    .build();
            TokenResponse token1 = authService.registe rTest(user2);
            user2.setTokens(List.of(authService.createToken(user2, token1.getAccesToken())));

            UserEntity user3 = UserEntity.builder()
                    .name("Kristell")
                    .lastName("Jimenez")
                    .email("mail2@email.com")
                    .phone("00111113")
                    .password("pass")
                    .roles(Set.of(roleInvited))
                    .build();
            TokenResponse token2 = authService.registerTest(user3);
            user3.setTokens(List.of(authService.createToken(user3, token2.getAccesToken())));

            userRepository.saveAll(List.of(user1, user2, user3));
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(FirstSpringApplication.class, args);
    }

}
