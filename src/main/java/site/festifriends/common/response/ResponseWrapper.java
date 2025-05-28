package site.festifriends.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.festifriends.common.exception.ErrorCode;

@Getter
@JsonInclude(Include.NON_NULL)
public class ResponseWrapper<T> extends BaseResponseWrapper<T> {

    private ResponseWrapper(Integer code, String message, T data) {
        super(code, message, data);
    }

    private ResponseWrapper(HttpStatus status, String message, T data) {
        super(status, message, data);
    }

    public static <T> ResponseWrapper<T> success(String message, T data) {
        return new ResponseWrapper<>(SUCCESS_CODE, message, data);
    }

    public static <T> ResponseWrapper<T> success(HttpStatus status, String message, T data) {
        return new ResponseWrapper<>(status, message, data);
    }

    public static <T> ResponseWrapper<T> success(String message) {
        return new ResponseWrapper<>(SUCCESS_CODE, message, null);
    }

    public static <T> ResponseWrapper<T> noContent(HttpStatus status, String message) {
        return new ResponseWrapper<>(status, message, null);
    }

    public static <T> ResponseWrapper<T> created(String message, T data) {
        return new ResponseWrapper<>(CREATED_CODE, message, data);
    }

    public static <T> ResponseWrapper<T> error(ErrorCode errorCode) {
        return new ResponseWrapper<>(errorCode.getStatus().value(), errorCode.getMessage(), null);
    }

    public static <T> ResponseWrapper<T> error(HttpStatus status, String message) {
        return new ResponseWrapper<>(status, message, null);
    }
}
