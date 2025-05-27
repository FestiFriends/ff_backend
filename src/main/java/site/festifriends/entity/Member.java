package site.festifriends.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import site.festifriends.common.model.SoftDeleteEntity;
import site.festifriends.entity.enums.Gender;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends SoftDeleteEntity {

    @Column(name = "email", nullable = false)
    @Comment("이메일")
    private String email;

    @Column(name = "nickname", length = 20, nullable = false)
    @Comment("닉네임")
    private String nickname;

    @Column(name = "profile_image_url", nullable = false)
    @Comment("프로필 이미지 URL")
    private String profileImageUrl;

    @Column(name = "age", nullable = false)
    @Comment("나이")
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10, nullable = false)
    @Comment("성별")
    private Gender gender;

    @Column(name = "introduce", length = 150)
    @Comment("유저 자기소개")
    private String introduction;

    @ElementCollection
    @CollectionTable(name = "member_tags", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "tag")
    @Comment("유저 태그")
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "member_sns", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "sns_link")
    @Comment("소셜 미디어 링크")
    private List<String> sns = new ArrayList<>();

    @Column(name = "social_id", nullable = false)
    @Comment("소셜 ID")
    private String socialId;

    @Column(name = "refresh_token")
    @Comment("리프레시 토큰")
    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
