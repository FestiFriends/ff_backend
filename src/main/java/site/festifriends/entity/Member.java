package site.festifriends.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import site.festifriends.common.model.SoftDeleteEntity;

@Entity
@Getter
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
    @Column(name = "gender", length = 10, nullable = false)
    @Comment("성별")
    private String gender;
    @Column(name = "introduce", length = 150)
    @Comment("유저 자기소개")
    private String introduction;
    @Column(name = "tags", length = 1000)
    @Comment("유저 태그(임시로 구분자를 통한 구분: ,)")
    private String tags;
    @Column(name = "sns", length = 1000)
    @Comment("소셜 미디어 링크(임시로 구분자를 통해 구분: ,)")
    private String sns;
    @Column(name = "social_id", nullable = false)
    @Comment("소셜 ID")
    private String socialId;
}
