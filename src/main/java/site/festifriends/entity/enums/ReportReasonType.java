package site.festifriends.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportReasonType {
    PROFANITY("욕설, 비방, 차별, 혐오"),
    ADVERTISEMENT("홍보, 영리목적"),
    ILLEGAL("불법 정보"),
    SEXUAL("음란, 청소년 유해"),
    PERSONAL_INFO("개인정보 노출, 유포, 거래"),
    SPAM("도배, 스팸");

    private final String description;
}
