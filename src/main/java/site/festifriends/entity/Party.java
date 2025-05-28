package site.festifriends.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import site.festifriends.common.model.SoftDeleteEntity;
import site.festifriends.entity.enums.AgeRange;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "party")
public class Party extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    @Comment("모임 이름")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_type", length = 10, nullable = false)
    @Comment("모임 성별 구분(남자/여자/혼성)")
    private Gender genderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_range", length = 20, nullable = false)
    @Comment("모임 연령대(10대/20대/30대/40대/50대/60대 이상)")
    private AgeRange ageRange;

    @Enumerated(EnumType.STRING)
    @Column(name = "gather_type", length = 20, nullable = false)
    @Comment("모임 방식(동행/탑승/숙박)")
    private GroupCategory gatherType;

    @Column(name = "gather_date", nullable = false)
    @Comment("모임 날짜")
    private LocalDateTime gatherDate;

    @Column(name = "location", nullable = false)
    @Comment("모임 장소")
    private String location;

    @Column(name = "count", nullable = false)
    @Comment("모임 인원 수")
    private Integer count;

    @Column(name = "introduction", length = 200, nullable = false)
    @Comment("모임 소개")
    private String introduction;

    @ElementCollection
    @CollectionTable(name = "party_hash_tags", joinColumns = @JoinColumn(name = "party_id"))
    @Column(name = "hash_tag")
    @Comment("모임 해시태그")
    private List<String> hashTags = new ArrayList<>();

    @Column(name = "announcement")
    @Comment("모임 공지사항")
    private String announcement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id")
    @Comment("관련 공연")
    private Festival festival;

    @Builder
    public Party(String title, Gender genderType, AgeRange ageRange, GroupCategory gatherType, 
                LocalDateTime gatherDate, String location, Integer count, String introduction, Festival festival) {
        this.title = title;
        this.genderType = genderType;
        this.ageRange = ageRange;
        this.gatherType = gatherType;
        this.gatherDate = gatherDate;
        this.location = location;
        this.count = count;
        this.introduction = introduction;
        this.festival = festival;
    }
}
