package ar.com.lbr.precisionappbe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        String path = request.getRequestURI();
        System.err.println("--- JWT FILTER START for " + path + " ---");
        System.err.println("Authorization Header: " + (authHeader != null ? "Present, length=" + authHeader.length() : "NULL"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.err.println("Authorization header missing or does not start with Bearer");
            System.err.println("--- JWT FILTER END (No token) ---");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            System.err.println("Raw Token: " + jwt);
            username = jwtService.extractUsername(jwt);
            System.err.println("Extracted Username from Token: " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                System.err.println("Loaded UserDetails for " + username + ": " + userDetails.getUsername() + ", enabled=" + userDetails.isEnabled() + ", roles=" + userDetails.getAuthorities());

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.err.println("Authentication Token successfully set in SecurityContextHolder!");
                } else {
                    System.err.println("isTokenValid returned FALSE for user " + username);
                }
            } else {
                System.err.println("Skipped validation. Username is null or Authentication is already set in SecurityContext");
            }
        } catch (Exception e) {
            System.err.println("JWT Verification EXCEPTION: " + e.getMessage());
            e.printStackTrace();
        }

        System.err.println("--- JWT FILTER END ---");
        filterChain.doFilter(request, response);
    }
}
