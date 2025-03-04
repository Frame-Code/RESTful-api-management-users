package com.firstSpring.firstSpring.config.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.firstSpring.firstSpring.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class JwtTokenValidator extends OncePerRequestFilter {
    private static final Logger LOG = Logger.getLogger(JwtTokenValidator.class.getName());
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(jwtToken == null || jwtToken.isBlank() || !jwtToken.startsWith("Bearer")) {
            LOG.log(Level.INFO, "No token identified");
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = jwtToken.substring(7);

        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);


        String username = jwtUtils.extracUsername(decodedJWT);
        String authorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

        Collection<? extends GrantedAuthority> authoritiesCollect = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authoritiesCollect);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        filterChain.doFilter(request, response);
    }
}
