package com.example.RESEARCH_SERVICE.utils;

import com.example.RESEARCH_SERVICE.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityResponseUtil {

    private final ObjectMapper objectMapper;

    public void writeError(
            HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {

        // Do not attempt a second response if another filter already completed it.
        if (response.isCommitted()) {
            return;
        }

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(false)
                .message(message)
                .status(status)
                .data(null)
                .errors(List.of(message))
                .path(request.getRequestURI())
                .traceId(TraceIdUtil.generate())
                .timestamp(LocalDateTime.now())
                .build();

        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        byte[] body = objectMapper.writeValueAsBytes(apiResponse);

        response.getOutputStream().write(body);
        response.flushBuffer();
    }
}
