package com.party.exception;

import com.party.common.Result;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return Result.error(500, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        e.printStackTrace();
        String message = "请求数据格式错误";
        Throwable cause = e.getCause();
        if (cause instanceof MismatchedInputException) {
            MismatchedInputException mie = (MismatchedInputException) cause;
            message = "字段 \"" + mie.getPath().stream()
                .map(p -> p.getFieldName())
                .collect(Collectors.joining(".")) + "\" 格式错误: " + mie.getOriginalMessage();
        } else if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            message = "字段 \"" + ife.getPath().stream()
                .map(p -> p.getFieldName())
                .collect(Collectors.joining(".")) + "\" 值 \"" + ife.getValue() + "\" 格式不正确";
        } else if (e.getMessage() != null) {
            message = e.getMessage();
        }
        return Result.error(400, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return Result.error(400, message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        return Result.error(403, "权限不足");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        e.printStackTrace();
        return Result.error(500, "服务器内部错误: " + e.getMessage());
    }
}
