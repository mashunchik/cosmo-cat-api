package com.cosmo.cats.security.apikey;

import com.cosmo.cats.persistence.entity.ApiKeyEntity;
import com.cosmo.cats.persistence.repository.ApiKeyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER = "X-API-KEY";
    private final ApiKeyRepository apiKeyRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<ApiKeyEntity> apiKeyEntity = apiKeyRepository.findByApiKey(apiKey);
        if (apiKeyEntity.isEmpty() || !apiKeyEntity.get().isValid()) {
            handleInvalidApiKey(response);
            return;
        }

        ApiKeyEntity entity = apiKeyEntity.get();
        ApiKeyAuthenticationToken authentication = new ApiKeyAuthenticationToken(
            entity.getApiKey(),
            entity.getName(),
            entity.getRoles()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private void handleInvalidApiKey(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorDetails = Map.of(
            "status", HttpStatus.UNAUTHORIZED.value(),
            "error", "Unauthorized",
            "message", "Invalid or expired API key"
        );

        objectMapper.writeValue(response.getWriter(), errorDetails);
    }
}
