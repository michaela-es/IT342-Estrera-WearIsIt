package edu.cit.estrera.wearisit.infrastructure.security.jwt;

import edu.cit.estrera.wearisit.infrastructure.security.SecurityConstants;
import edu.cit.estrera.wearisit.features.user_management.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        if (Arrays.asList(SecurityConstants.WHITELIST_EXACT).contains(path)) {
            return true;
        }

        for (String prefix : SecurityConstants.WHITELIST_PREFIX) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }

//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        final String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        final String jwt = authHeader.substring(7);
//
//        Long userId = jwtService.extractUserId(jwt);
//
//        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            var user = userRepository.findById(userId).orElse(null);
//
//            if (user != null && jwtService.isTokenValid(jwt)) {
//                var authToken = new UsernamePasswordAuthenticationToken(
//                        user,
//                        null,
//                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
//                );
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
@Override
protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
) throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
    }

    final String jwt = authHeader.substring(7);

    Long userId = jwtService.extractUserId(jwt);

    if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        var user = userRepository.findById(userId).orElse(null);

        if (user != null && jwtService.isTokenValid(jwt)) {
            String role = user.getRole();
            String authority = role != null && role.equals("ADMIN") ? "ROLE_ADMIN" : "ROLE_USER";

            var authToken = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    List.of(new SimpleGrantedAuthority(authority))
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
    filterChain.doFilter(request, response);
}
}