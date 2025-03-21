package com.firstSpring.firstSpring.config;

import com.firstSpring.firstSpring.config.filter.JwtTokenValidator;
import com.firstSpring.firstSpring.model.PermissionsEnum;
import com.firstSpring.firstSpring.model.RoleEnum;
import com.firstSpring.firstSpring.service.UserDetailsServiceImpl;

import com.firstSpring.firstSpring.utils.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

/**
 * Security config class to define the security settings necessary in this app
 *
 * @author Daniel Mora Cantillo
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
//Is used to can be use annotations to define the filters (here down the .authorizeHttpRequest...) in each class and each endpoint but don´t like it, I prefer this option
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    //HttpSecurity object go to around of all filters from SecurityFilterChain
    /**
     * This method define to disable of the csrf because this is a apiRest, cors as default,  maintains a session as 'STATELESS' because the session is manage by the tokens
     * also define the resources and endpoints with permissions necessaries, define how to do when occur an exception redirecting if is necessary,
     * define the resource with the login form and last add before of all this, the filter 'JwtTokenValidator' to first verify the token
     * @param httpSecurity the object that runs each filter
     * @return the interface SecurityFilterChain with all my settings
     * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                //Filters
                //Recommended in MVC but not in Rest, because in mvn we're working with forms but in rest we works using JSON
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                //The session is managed by the tokens but not the object session on the side of the client (the object session is very wight)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {
                    //Configure public endpoints
                    http.requestMatchers(
                            "/static/**",
                            "/css/**",
                            "/img/**",
                            "/js/**",
                            "/META-INF/**",
                            "/scss/**",
                            "/vendor/**",
                            "/WEB-INF/**",
                            "/login.html",
                            "/register.html",
                            "/").permitAll();
                    http.requestMatchers(HttpMethod.GET, "/auth/**").permitAll();
                    http.requestMatchers(HttpMethod.POST, "/auth/**").permitAll();

                    //configure private endpoints and resources
                    http.requestMatchers("/index.html").hasAnyRole(
                            RoleEnum.ADMIN.name(), RoleEnum.USER.name(), RoleEnum.INVITED.name(), RoleEnum.DEVELOPER.name());

                    http.requestMatchers(HttpMethod.GET, "/api/users", "/api/users/search/**").hasAnyRole(
                            RoleEnum.ADMIN.name(), RoleEnum.USER.name(), RoleEnum.DEVELOPER.name());
                    http.requestMatchers(HttpMethod.GET, "/api/users/**", "/api/users/reset/{id}").hasAnyRole(
                            RoleEnum.ADMIN.name(), RoleEnum.DEVELOPER.name());
                    http.requestMatchers(HttpMethod.POST, "/api/users/reset/**", "/api/users/edit").hasAnyRole(
                            RoleEnum.ADMIN.name(), RoleEnum.DEVELOPER.name());
                    http.requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority(
                            PermissionsEnum.DELETE.name());

                    //configure any endpoints - NOT SPECIFIED
                    http.anyRequest().denyAll();
                })
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint((request, response, authException) -> {
                        if(request.getCookies() == null || Arrays.stream(request.getCookies()).noneMatch(cookie -> cookie.getName().equals("access_token"))) {
                            response.sendRedirect("/login.html");
                            return;
                        }

                        if(request.getRequestURI().equals("/") || request.getRequestURI().isBlank()) {
                            response.sendRedirect("/index.html");
                        }

                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    });
                })
                .formLogin(form -> {
                    form.loginPage("/login.html");
                    form.loginProcessingUrl("auth/log-in").permitAll();
                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .build();
    }

    /**
     * This method just return an AuthenticationManager to authenticate the user
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * This method get an Object UserDetailsServiceImpl to define the passwordEncoder and the UserDetailsService to return that as
     * object AuthenticationProvider
     * */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsServiceImpl) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsServiceImpl);
        return provider;
    }

    /**
     * This method just return an object BCrypt to encode the passwords
     * */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
