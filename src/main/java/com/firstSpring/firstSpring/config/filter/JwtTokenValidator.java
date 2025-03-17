package com.firstSpring.firstSpring.config.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.firstSpring.firstSpring.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * This is the filter to verify if the request have the respective jwt tokens.
 * Is necessary to the tokens are saved in the browser's cookies, because this filter only search
 * the token on the cookies, NOT in the headers of the request.
 * If the tokens are not present just the request go the following filter
 * If the tokens are present, the user (getting the user around the jwt token) is saved in the spring security context
 *
 * @author Daniel Mora Cantillo
 * */

@RequiredArgsConstructor
public class JwtTokenValidator extends OncePerRequestFilter {
    private static final Logger LOG = Logger.getLogger(JwtTokenValidator.class.getName());
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = null;

        if(request.getCookies() == null) {
            LOG.log(Level.INFO, "No cookies identified");
            filterChain.doFilter(request, response);
            return;
        }

        for(Cookie cookie : request.getCookies()) {
            if(cookie.getName().equals("access_token")) {
                jwtToken = cookie.getValue();
            }
        }

        if(jwtToken == null || jwtToken.isBlank()) {
            LOG.log(Level.INFO, "No token from the cookies identified");
            filterChain.doFilter(request, response);
            return;
        }

        LOG.log(Level.INFO, "Token identified");
        try {
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
            LOG.log(Level.INFO, "Token valid");

            String username = jwtUtils.extracUsername(decodedJWT);
            String authorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();
            Collection<? extends GrantedAuthority> authoritiesCollect = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authoritiesCollect);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            LOG.log(Level.INFO, "User saved in the Spring context");
            filterChain.doFilter(request, response);

        } catch (JWTVerificationException e) {
            LOG.log(Level.SEVERE, "Error verifying the token " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
    }
}
