package site.festifriends.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseResponseWrapper<T> {
    protected static final int SUCCESS_CODE = HttpStatus.OK.value();
    protected static final int CREATED_CODE = HttpStatus.CREATED.value();
    
    private final Integer code;
    private final String message;
    private final T data;

    protected BaseResponseWrapper(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    protected BaseResponseWrapper(HttpStatus status, String message, T data) {
        this(status.value(), message, data);
    }
} 