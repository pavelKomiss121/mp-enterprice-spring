/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.api.advice;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import ru.mentee.learning.api.annotation.WrapResponse;
import ru.mentee.learning.api.dto.ApiResponse;

@RestControllerAdvice
@Slf4j
public class ResponseEnvelopeAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Применяем только к методам с @WrapResponse
        return returnType.hasMethodAnnotation(WrapResponse.class);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        log.debug("Wrapping response in envelope for: {}", request.getURI().getPath());

        return ApiResponse.builder()
                .success(true)
                .data(body)
                .timestamp(Instant.now())
                .path(request.getURI().getPath())
                .build();
    }
}
