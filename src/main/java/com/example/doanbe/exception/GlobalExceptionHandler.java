package com.example.doanbe.exception;

import java.util.*;

import com.example.doanbe.dto.ApiErrorResponseDto;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ApiErrorResponseDto> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        System.out.println("Lỗi cú pháp: " + e.getMessage());

        ApiErrorResponseDto errorResponse = ApiErrorResponseDto.builder()
                .code("7")
                .title("Method Not Supported")
                .message("Lỗi cú pháp trên URL!")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ApiErrorResponseDto> handleBadCredentialsException(
            BadCredentialsException e) {
        System.out.println("Lỗi thông tin xác thực: " + e.getMessage());

        ApiErrorResponseDto errorResponse = ApiErrorResponseDto.builder()
                .code("1")
                .title("Authentication Failed")
                .message("Sai mật khẩu!")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ApiErrorResponseDto> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        System.out.println("Lỗi định dạng body: " + e.getMessage());

        ApiErrorResponseDto errorResponse = ApiErrorResponseDto.builder()
                .code("99")
                .title("Invalid Request Body")
                .message("Body phải ở định dạng của một đối tượng JSON với tất cả các tham số bắt buộc!")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ApiErrorResponseDto> handleAccessDeniedException(AccessDeniedException e) {
        System.out.println("Lỗi bị cấm: " + e.getMessage());

        ApiErrorResponseDto errorResponse = ApiErrorResponseDto.builder()
                .code("43")
                .title("Access Denied")
                .message("Tài khoản của bạn không có quyền sử dụng API này!")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler({AppException.class})
    public ResponseEntity<ApiErrorResponseDto> handleAppException(AppException e) {
        System.out.println("Lỗi trong ứng dụng: " + e.getMessage());

        ApiErrorResponseDto errorResponse = ApiErrorResponseDto.builder()
                .code(String.valueOf(e.getCode()))
                .title("Application Error")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(e.getHttpStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        System.out.println("Lỗi sai dữ liệu: " + e.getMessage());

        ApiErrorResponseDto errorResponse = ApiErrorResponseDto.builder()
                .code("99")
                .title("Validation Error")
                .message("Đăng ký không thành công: Vui lòng cung cấp dữ liệu hợp lệ.")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorResponseDto> handleBindException(BindException e) {
        String errorMessage = null;
        if (e.getBindingResult().hasErrors()) {
            errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            System.out.println("Lỗi định dạng: " + errorMessage);
        }

        ApiErrorResponseDto errorResponse = ApiErrorResponseDto.builder()
                .code("2")
                .title("Invalid Parameters")
                .message("Tham số không đúng định dạng! " + errorMessage)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleUnwantedException(Exception e) {
        System.out.println("Lỗi không mong muốn: " + e.getMessage());
        e.printStackTrace();

        ApiErrorResponseDto errorResponse = ApiErrorResponseDto.builder()
                .code("50")
                .title("Internal Server Error")
                .message("Lỗi hệ thống!")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ApiErrorResponseDto> handleTokenRefreshException(
            TokenRefreshException ex, WebRequest request) {
        System.out.println("Token refresh error: " + ex.getMessage());
        ex.printStackTrace();

        ApiErrorResponseDto errorResponse = ApiErrorResponseDto.builder()
                .code(String.valueOf(HttpStatus.FORBIDDEN.value()))
                .title("Token Refresh Error")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ApiErrorResponseDto> handleStorageException(
            StorageException ex, WebRequest request) {
        System.out.println("Storage error: " + ex.toString());

        ApiErrorResponseDto errorResponse = ApiErrorResponseDto.builder()
                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .title("Storage Error")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponseDto> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponseDto("404", "Resource Not Found", ex.getMessage()));
    }



}
