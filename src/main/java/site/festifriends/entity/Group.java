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
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "groups")
public class Group extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    @Comment("모임 이름")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_type", length = 10, nullable = false)
    @Comment("모임 성별 구분(남자/여자/혼성)")
    private Gender genderType;

    @Column(name = "start_age", nullable = false)
    @Comment("연령대 (시작)")
    private Integer startAge;

    @Column(name = "end_age", nullable = false)
    @Comment("연령대 (끝)")
    private Integer endAge;

    @Enumerated(EnumType.STRING)
    @Column(name = "gather_type", length = 20, nullable = false)
    @Comment("모임 방식(동행/탑승/숙박)")
    private GroupCategory gatherType;

    @Column(name = "start_date", nullable = false)
    @Comment("모임 시작 날짜")
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    @Comment("모임 종료 날짜")
    private LocalDateTime endDate;

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
    @CollectionTable(name = "group_hash_tags", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "hash_tag")
    @Comment("모임 해시태그")
    private List<String> hashTags = new ArrayList<>();

    @Column(name = "announcement")
    @Comment("모임 공지사항")
    private String announcement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    @Comment("관련 공연")
    private Performance performance;

    @Builder
    public Group(String title, Gender genderType, Integer startAge, Integer endAge, GroupCategory gatherType, 
                LocalDateTime startDate, LocalDateTime endDate, String location, Integer count, 
                String introduction, Performance performance) {
        this.title = title;
        this.genderType = genderType;
        this.startAge = startAge;
        this.endAge = endAge;
        this.gatherType = gatherType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.count = count;
        this.introduction = introduction;
        this.performance = performance;
    }
}
