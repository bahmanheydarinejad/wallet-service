package com.company.walletservice.controllers.exceptionHandlers;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GeneralExceptionHandler extends OncePerRequestFilter implements ResponseBodyAdvice<Object> {

    private final static String HEADER_APP_NAME = "serviceName";
    private final MessageSource messageSource;
    @Value("${spring.application.name}")
    private String appName;

    @ExceptionHandler(value = {BusinessException.class})
    public ResponseEntity<GeneralResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity.badRequest()
                .header(HEADER_APP_NAME, appName)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new GeneralResponse(toResultMessageDto(e.getMessages())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<GeneralResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .header(HEADER_APP_NAME, appName)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new GeneralResponse(Optional.of(e.getBindingResult())
                        .map(Errors::getAllErrors)
                        .map(errors -> errors.stream()
                                .map(error -> toResultMessageDto(new ExceptionMessage(error.getDefaultMessage(), error.getArguments())))
                                .collect(Collectors.toList()))
                        .orElse(List.of())
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<GeneralResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest()
                .header(HEADER_APP_NAME, appName)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new GeneralResponse(List.of(toResultMessageDto(new ExceptionMessage(null, "input.type.bound.invalid", e.getMessage())))));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<GeneralResponse> handleMethodArgumentNotValidException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest()
                .header(HEADER_APP_NAME, appName)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new GeneralResponse(List.of(toResultMessageDto(new ExceptionMessage(null, "input.type.invalid", e.getName(), e.getValue())))));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public final ResponseEntity<GeneralResponse> handleMethodArgumentNotValidException(NoResourceFoundException e) {
        return ResponseEntity.badRequest()
                .header(HEADER_APP_NAME, appName)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new GeneralResponse(List.of(toResultMessageDto(new ExceptionMessage(null, "resource.not-found", String.format("%s::%s", e.getHttpMethod(), e.getResourcePath()))))));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponse> handleException(Exception ex) {
        log.error("HandleInternalServerError: {}", ex.getMessage(), ex);
        ex.printStackTrace();
        return ResponseEntity.internalServerError()
                .header(HEADER_APP_NAME, appName)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new GeneralResponse(List.of(new ResultMessage(ex.getMessage(), "خطای ناشناخته! لطفا با پشتیبانی تماس بگیرید"))));
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Apply to all responses
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            @Nullable Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        // Modify the response messages here
        if (body instanceof GeneralResponse responseBody) {
            return new GeneralResponse(toResultMessageDto(responseBody.messages()));
        }
        return body;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (StringUtils.isEmpty(response.getHeader("serviceName"))) {
            response.setHeader(HEADER_APP_NAME, appName);
        }
        filterChain.doFilter(request, response);
    }

    private List<ResultMessage> toResultMessageDto(List<? extends MessageCodeContainer> messages) {
        return Optional.ofNullable(messages)
                .map(List::stream)
                .map(stream -> stream.map(this::toResultMessageDto).collect(Collectors.toList()))
                .orElse(List.of());
    }

    private ResultMessage toResultMessageDto(MessageCodeContainer message) {
        if (Objects.isNull(message)) {
            return new ResultMessage("");
        } else if (message instanceof ExceptionMessage) {
            return new ResultMessage(message.getCode(), Optional.of(messageSource.getMessage(message.getCode(), ((ExceptionMessage) message).args(), message.getCode(), LocaleContextHolder.getLocale(new SimpleLocaleContext(Locale.of("fa")))))
                    .map(templateString -> Optional.of(((ExceptionMessage) message).args())
                            .map(args -> MessageFormat.format(templateString, args))
                            .orElse(templateString)
                    )
                    .orElse(message.getCode()));
        } else if (message instanceof ResultMessage) {
            return (ResultMessage) message;
        } else {
            return new ResultMessage("");
        }
    }

}
