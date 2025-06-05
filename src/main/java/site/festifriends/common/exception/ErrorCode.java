package site.festifriends.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "요청이 현재 서버 상태와 충돌합니다."),

    // Review 관련 에러
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "모임을 찾을 수 없습니다."),
    TARGET_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰 대상 사용자를 찾을 수 없습니다."),
    NOT_GROUP_PARTICIPANT(HttpStatus.FORBIDDEN, "해당 모임에 참여하지 않은 사용자입니다."),
    GROUP_NOT_ENDED(HttpStatus.BAD_REQUEST, "아직 종료되지 않은 모임입니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 사용자에게 리뷰를 작성했습니다."),
    CANNOT_REVIEW_SELF(HttpStatus.BAD_REQUEST, "자신에게는 리뷰를 작성할 수 없습니다."),

    // 파일 업로드 관련 에러
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 확장자입니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
