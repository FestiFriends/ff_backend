package site.festifriends.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponseWrapper<T> extends BaseResponseWrapper<List<T>> {

    private final Integer page;
    private final Integer size;
    private final Integer totalElements;
    private final Integer totalPages;
    private final Boolean first;
    private final Boolean last;

    private PageResponseWrapper(Integer code, String message, List<T> data, Integer page, Integer size,
        Integer totalElements, Integer totalPages, Boolean first, Boolean last) {
        super(code, message, data);
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    private PageResponseWrapper(HttpStatus status, String message, List<T> data, Integer page, Integer size,
        Integer totalElements, Integer totalPages, Boolean first, Boolean last) {
        super(status, message, data);
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    public static <T> PageResponseWrapper<T> success(String message, List<T> data, Integer page, Integer size,
        Integer totalElements, Integer totalPages, Boolean first, Boolean last) {
        return new PageResponseWrapper<>(SUCCESS_CODE, message, data, page, size, totalElements, totalPages, first,
            last);
    }
}
