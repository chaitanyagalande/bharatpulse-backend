package com.example.CityPolling.security;


import com.example.CityPolling.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Acts like Middleware
// Checks every request for a Token, if token present it validates it, if absent or fake the request is rejected
// Controller runs only after passing this filter
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserService userService; // makes use of UserService method of findByEmail

    public JwtFilter(JwtUtil jwtUtil, @Lazy UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String header = request.getHeader("Authorization");
        String email = null, token = null;

        if (header != null && header.startsWith("Bearer ")) { // If Token present in request
            token = header.substring(7); // Extract token from request
            email = jwtUtil.extractEmail(token); // Extract email from token
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var user = userService.findByEmail(email);
            if (user.isPresent()) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(email, null, null);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken); // Request is authenticated successfully
            }
        }

        chain.doFilter(request, response);
        // Passes control to the next filter in the chain, and eventually to the servlet/controller that handles the request.
    }

}
