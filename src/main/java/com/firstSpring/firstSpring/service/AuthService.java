package com.firstSpring.firstSpring.service;

import com.auth0.jwt.interfaces.DecodedJWT;
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

import java.util.*;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Class to manage log in, sign up and refresh token, this class is used from the AuthController
 *
 * @author Daniel Mora Cantillo
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
    /**
     * This method is used to logging the user, with the following steps:
     * 1. Using the instance of UserDetailsService we build and get an objet Type User (User class of spring security,
     * this class is understood by Spring) using the parameter object, if the object is null,
     * the user has not founded on the db and returns it
     * 2. Verify if the password matches, if that is not true, the password is incorrect
     * 3. We create an object type Authentication using the object User, to create the respective tokens, and finally return it
     *
     * @param userDTO dto with necessary data to log in a user
     * */
    public ResponseEntity<?> login(@NotNull UserLogin userDTO) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userDTO.getEmail());

            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            if (!passwordEncoder.matches(userDTO.getPassword(), userDetails.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect Password");
            }

            String email = userDTO.getEmail();
            String userName = userDetails.getUsername();


            Authentication auth = new UsernamePasswordAuthenticationToken(
                    email,
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );

            Optional<String> jwtToken = jwtUtils.createToken(auth, userName);
            Optional<String> refreshToken = jwtUtils.createRefreshToken(auth, userName);
            if (jwtToken.isEmpty() || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating the token");
            }
            revokeAllTokens(userMapper.toEntity(userDTO));
            saveUserToken(userRepository.findByEmail(userDTO.getEmail()).get(), jwtToken.get());
            LOG.log(Level.INFO, "User: " + userName + " with the following authorities: " + userDetails.getAuthorities().toString() + " logged successfully");

            return ResponseEntity.ok(new TokenResponse(jwtToken.get(), refreshToken.get(), userName));
        } catch (AuthenticationException e) {
            LOG.log(Level.SEVERE, e.getMessage(), "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    /**
     * This method is used to register a new user, with the following steps:
     * 1. We verify if the user's phone and user's email exist on the db (both attributes are unique),
     * also verify if the request has respective roles,
     * 2. We get a list type string with all role names saved on the db to match the roles got by the request to verify if that list is empty or not
     * 3. Creates an object Type UserEntity with the data got by the request http to persist it and creates a list type
     * SimpleGrantedAuthority with the roles and permissions of the user, to after create an object type Authentication with its data
     * 4. Finally, is created the tokens and return it.
     *
     * @param userRegister dto with the necessary data to register a new user
     * */
    public ResponseEntity<?> register(@NotNull UserRegister userRegister) {
        if (userRepository.findByEmail(userRegister.getEmail()).isPresent()) {
            LOG.log(Level.SEVERE, "The user with the email " + userRegister.getEmail() + " already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The user with the email " + userRegister.getEmail() + " already exists");
        }

        if (userRepository.findByNumberPhone(userRegister.getPhone()).isPresent()) {
            LOG.log(Level.SEVERE, "The user with the phone " + userRegister.getPhone() + " already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The user with the phone " + userRegister.getPhone() + " already exists");
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

        //ALWAYS is necessary to add the string "ROLE_" before the name of "real role"
        savedUser.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));
        savedUser.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionEnum().name())));

        String userName = savedUser.getFullNames();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getEmail(),
                null,
                authorityList
        );

        Optional<String> jwtToken = jwtUtils.createToken(authentication, userName);
        Optional<String> refreshToken = jwtUtils.createRefreshToken(authentication, userName);

        if (jwtToken.isEmpty() || refreshToken.isEmpty()) {
            LOG.log(Level.SEVERE, "Error creating the token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating the token");
        }

        saveUserToken(savedUser, jwtToken.get());

        LOG.log(Level.INFO, "User: " + userName + " with the following authorities: " + authentication.getAuthorities().toString() + " registered successfully");
        return ResponseEntity.ok().body(new TokenResponse(jwtToken.get(), refreshToken.get(), userName));
    }

    /** Method to create a refresh token, this method verifies if the token is valid and get his information
     * to create an object type Authentication, and finally create the respective tokens to return it.
     *
     * @param authHeader header of the request http
     * */
    public ResponseEntity<?> refreshToken(final String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            LOG.log(Level.WARNING, "Invalid bearer token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid bearer token");
        }

        final String refreshToken = authHeader.substring(7);
        DecodedJWT decode = jwtUtils.validateToken(refreshToken);

        final String userEmail = jwtUtils.extracUsername(decode);

        if (userEmail == null) {
            LOG.log(Level.WARNING, "Invalid refresh token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token");
        }

        Optional<UserEntity> user = userRepository.findByEmail(userEmail);

        if (user.isEmpty()) {
            LOG.log(Level.WARNING, "User not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        if (!jwtUtils.isTokenValid(decode, user.get())) {
            LOG.log(Level.WARNING, "Invalid refresh token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token");
        }

        String authorities = jwtUtils.getSpecificClaim(decode, "authorities").asString();
        Collection<? extends GrantedAuthority> authoritiesCollect = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.get().getEmail(),
                null,
                authoritiesCollect
        );
        String userName = user.get().getFullNames();
        Optional<String> accessToken = jwtUtils.createToken(auth, userName);
        Optional<String> refreshTokenFinal = jwtUtils.createRefreshToken(auth, userName);

        if(accessToken.isEmpty() || refreshTokenFinal.isEmpty()) {
            LOG.log(Level.SEVERE, "Error creating tokens");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating tokens");
        }

        revokeAllTokens(user.get());
        saveUserToken(user.get(), accessToken.get());
        return ResponseEntity.ok(new TokenResponse(accessToken.get(), refreshTokenFinal.get(), userName));

    }

    /** Method to saver user tokens
     * @param user object to add a user signature
     * @param jwtToken token to persist
     * */
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

    /** Method to revoke all the tokens of the user
     *
     * @param user user to revoke all tokens
     * */
    private void revokeAllTokens(UserEntity user) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(user.getEmail());
        userOpt.ifPresent(userEntity -> {
            userEntity.getTokens().forEach(token -> {
                token.setRevoked(true);
                token.setExpired(true);
            });
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

        String userName = user.getFullNames();
        Optional<String> jwtToken = jwtUtils.createToken(auth, userName);
        Optional<String> refreshToken = jwtUtils.createRefreshToken(auth, userName);
        if (jwtToken.isEmpty() || refreshToken.isEmpty()) {
            return null;
        }

        return new TokenResponse(jwtToken.get(), refreshToken.get(), userName); //Retorna una instancia de tipo TokenResponse que se termina volviendo al front como json
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
