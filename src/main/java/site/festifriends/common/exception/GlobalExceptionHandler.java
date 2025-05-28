package site.festifriends.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import site.festifriends.common.ResponseWrapper;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        MissingServletRequestParameterException.class,
        MissingRequestHeaderException.class,
        BindException.class,
        TypeMismatchException.class,
        MethodArgumentTypeMismatchException.class
    })
    protected ResponseEntity<ResponseWrapper<?>> handleValidException(
        MethodArgumentNotValidException e) {
        log.warn("handleValidException : {}", e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(ResponseWrapper.error(ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler({
        NoHandlerFoundException.class,
        NoResourceFoundException.class
    })
    protected ResponseEntity<ResponseWrapper<?>> handleNotFoundException(Exception e) {
        log.warn("handleNotFoundException : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.error(ErrorCode.NOT_FOUND));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseWrapper<?>> handleException(Exception e) {
        log.error("handleException : {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResponseWrapper.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

}
