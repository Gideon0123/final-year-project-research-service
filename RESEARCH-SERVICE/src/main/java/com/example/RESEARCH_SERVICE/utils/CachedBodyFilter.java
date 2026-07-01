package com.example.RESEARCH_SERVICE.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Component
public class CachedBodyFilter extends OncePerRequestFilter {

    public static final String REQUEST_BODY = "REQUEST_BODY";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )
            throws ServletException,
            IOException {

        ContentCachingRequestWrapper wrapper =
                new ContentCachingRequestWrapper(request, response.getStatus());

        filterChain.doFilter(wrapper, response);

        String body =
                new String(
                        wrapper.getContentAsByteArray(),
                        wrapper.getCharacterEncoding()
                );

        wrapper.setAttribute(
                REQUEST_BODY,
                body
        );

    }

}