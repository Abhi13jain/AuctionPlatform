package com.example.auction.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Why logging and monitoring matter in a real system:
 * If a user complains "I tried to bid but got an error at 3:00 PM", without logs, you are completely blind. This filter writes down the details of every single request that enters and exits the server, acting as the "black box" flight recorder for our application.
 *
 * What "production-grade" means for this feature:
 * In development, we use `System.out.println()`. In production, we use structured loggers (like SLF4J) because they can record timestamps, severity levels (INFO/ERROR), and can easily be searched in tools like Datadog or ELK stack.
 */
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // Log the incoming request
        logger.info("INCOMING REQUEST: method=[{}] URI=[{}] client=[{}]", 
                request.getMethod(), request.getRequestURI(), request.getRemoteAddr());

        // Let the request proceed to the controller
        filterChain.doFilter(request, response);

        // Calculate how long it took to process
        long duration = System.currentTimeMillis() - startTime;

        // Log the outgoing response
        logger.info("OUTGOING RESPONSE: status=[{}] duration=[{}ms] URI=[{}]", 
                response.getStatus(), duration, request.getRequestURI());
    }
}
