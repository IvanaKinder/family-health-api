package com.familyhealth.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        MDC.put("requestId", UUID.randomUUID().toString().substring(0, 8));

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                MDC.put("username", auth.getName());
            }

            log.info("{} {} -> {}", request.getMethod(), request.getRequestURI(), response.getStatus());
        } finally {
            log.debug("{} {} completed in {}ms", request.getMethod(), request.getRequestURI(),
                    System.currentTimeMillis() - start);
            MDC.clear();
        }
    }
}
