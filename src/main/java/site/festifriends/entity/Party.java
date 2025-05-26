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
@Table(name = "party")
public class Party extends SoftDeleteEntity {

    @Column(name = "title", nullable = false)
    @Comment("모임 이름")
    private String title;
    @Column(name = "gender_type", length = 10, nullable = false)
    @Comment("모임 성별 구분(남자/여자/혼성)")
    private String genderType;
    @Column(name = "age_range", length = 20, nullable = false)
    @Comment("모임 연령대(10대/20대/30대/40대/50대/60대 이상)")
    private String ageRange;
    @Column(name = "gather_type", length = 20, nullable = false)
    @Comment("모임 방식(동행/탑승/숙박)")
    private String gatherType;
    @Column(name = "gather_date", nullable = false)
    @Comment("모임 날짜")
    private String gatherDate;
    @Column(name = "location", nullable = false)
    @Comment("모임 장소")
    private String location;
    @Column(name = "count", nullable = false)
    @Comment("모임 인원 수")
    private Integer count;
    @Column(name = "introduction", length = 200, nullable = false)
    @Comment("모임 소개")
    private String introduction;
    @Column(name = "hash_tags", length = 255)
    @Comment("모임 해시태그(임시로 구분자를 통한 구분: ,)")
    private String hashTags;
    @Column(name = "announcement")
    @Comment("모임 공지사항")
    private String announcement;
}
