package site.festifriends.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(Include.NON_NULL)
public class ResponseWrapper<T> {

    private final Integer code;
    private final String message;
    private final T data;

    private ResponseWrapper(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseWrapper<T> noContent(HttpStatus status, String message) {
        return new ResponseWrapper<>(status.value(), message, null);
    }

    public static <T> ResponseWrapper<T> success(HttpStatus status, String message, T data) {
        return new ResponseWrapper<>(status.value(), message, data);
    }

    public static <T> ResponseWrapper<T> error(HttpStatus status, String message) {
        return new ResponseWrapper<>(status.value(), message, null);
    }

}
