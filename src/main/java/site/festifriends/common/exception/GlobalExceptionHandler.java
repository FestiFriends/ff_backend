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
import site.festifriends.common.response.ResponseWrapper;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ResponseWrapper<?>> handleBusinessException(BusinessException e) {
        log.warn("handleBusinessException : {}", e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus())
            .body(ResponseWrapper.error(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        MissingServletRequestParameterException.class,
        MissingRequestHeaderException.class,
        BindException.class,
        TypeMismatchException.class,
        MethodArgumentTypeMismatchException.class
    })
    protected ResponseEntity<ResponseWrapper<?>> handleValidException(Exception e) {
        log.warn("handleValidException : {}", e.getMessage());

        if (e instanceof MethodArgumentNotValidException validException) {
            String errorMessage = validException.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(ErrorCode.BAD_REQUEST, errorMessage));
        } else if (e instanceof BindException bindException) {
            String errorMessage = bindException.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(ErrorCode.BAD_REQUEST, errorMessage));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseWrapper.error(ErrorCode.BAD_REQUEST));
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
