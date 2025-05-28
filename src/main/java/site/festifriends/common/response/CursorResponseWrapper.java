package site.festifriends.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CursorResponseWrapper<T> extends BaseResponseWrapper<List<T>> {
    private final Long cursorId;
    private final Boolean hasNext;

    private CursorResponseWrapper(Integer code, String message, List<T> data, Long cursorId, Boolean hasNext) {
        super(code, message, data);
        this.cursorId = cursorId;
        this.hasNext = hasNext;
    }

    private CursorResponseWrapper(HttpStatus status, String message, List<T> data, Long cursorId, Boolean hasNext) {
        super(status, message, data);
        this.cursorId = cursorId;
        this.hasNext = hasNext;
    }

    public static <T> CursorResponseWrapper<T> success(String message, List<T> data, Long cursorId, Boolean hasNext) {
        return new CursorResponseWrapper<>(SUCCESS_CODE, message, data, cursorId, hasNext);
    }

    public static <T> CursorResponseWrapper<T> success(HttpStatus status, String message, List<T> data, Long cursorId, Boolean hasNext) {
        return new CursorResponseWrapper<>(status, message, data, cursorId, hasNext);
    }

    public static <T> CursorResponseWrapper<T> empty(String message) {
        return new CursorResponseWrapper<>(SUCCESS_CODE, message, List.of(), null, false);
    }

    public static <T> CursorResponseWrapper<T> firstPage(String message, List<T> data, Long nextCursorId, Boolean hasNext) {
        return new CursorResponseWrapper<>(SUCCESS_CODE, message, data, nextCursorId, hasNext);
    }

    public static <T> CursorResponseWrapper<T> of(Integer code, String message, List<T> data, Long cursorId, Boolean hasNext) {
        return new CursorResponseWrapper<>(code, message, data, cursorId, hasNext);
    }

    public boolean hasNextPage() {
        return Boolean.TRUE.equals(this.hasNext);
    }

    public boolean isFirstPage() {
        return this.cursorId == null;
    }
} 