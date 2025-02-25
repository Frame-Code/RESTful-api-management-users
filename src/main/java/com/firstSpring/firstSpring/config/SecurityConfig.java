package com.firstSpring.firstSpring.config;

import com.firstSpring.firstSpring.service.UserDetailsServiceImpl;

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

/**
 *
 * @author Artist-Code
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity //Is used to can be use anotations to define the filters (here down the .authorizettpRequest...) in each class and each endpoint but don´t like it, I prefer this option
public class SecurityConfig {

    //HttpSecurity object go to around of all filters from SecurityyFilerChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                //Filters
                //Recomended in MVC but not in Rest, because in mvn we working with forms but in rest we workes using JSON
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                //The sesion is managed by the tokens but not the object session on the side of the client (the object sesion is very wight)
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {
                    //Configure public endpoints 
                    http.requestMatchers(HttpMethod.GET, "/auth/**").permitAll();
                    http.requestMatchers(HttpMethod.POST, "/auth/**").permitAll();
                    
                    //configure private endpoints
                    http.requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("READ");
                    http.requestMatchers(HttpMethod.GET, "/api/users/**").hasAuthority("READ");
                    http.requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("CREATE");

                    //configure any endpoints - NOT ESPECIFICIED
                    http.anyRequest().denyAll();
                })
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticacionConfiguration) throws Exception {
        return authenticacionConfiguration.getAuthenticationManager();
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
