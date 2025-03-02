package com.firstSpring.firstSpring.config;

import com.firstSpring.firstSpring.config.filter.JwtTokenValidator;
import com.firstSpring.firstSpring.service.UserDetailsServiceImpl;

import com.firstSpring.firstSpring.utils.JwtUtils;
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

/**
 *
 * @author Artist-Code
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity //Is used to can be use annotations to define the filters (here down the .authorizeHttpRequest...) in each class and each endpoint but don´t like it, I prefer this option
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;


    //HttpSecurity object go to around of all filters from SecurityFilerChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                //Filters
                //Recommended in MVC but not in Rest, because in mvn we're working with forms but in rest we works using JSON
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                //The session is managed by the tokens but not the object session on the side of the client (the object session is very wight)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {
                    //Configure public endpoints 
                    http.requestMatchers(HttpMethod.GET, "/auth/**").permitAll();
                    http.requestMatchers(HttpMethod.POST, "/auth/**").permitAll();
                    
                    //configure private endpoints
                    http.requestMatchers(HttpMethod.GET, "/api/users").permitAll();
                    //http.requestMatchers(HttpMethod.GET, "/api/users").hasAnyRole("ADMIN", "USER, DEVELOPER");
                    http.requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("ADMIN", "DEVELOPER");
                    http.requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("DELETE");

                    //configure any endpoints - NOT SPECIFIED
                    http.anyRequest().denyAll();
                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsServiceImpl) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsServiceImpl);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
