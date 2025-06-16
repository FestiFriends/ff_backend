package site.festifriends.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import site.festifriends.common.model.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "schedule")
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id", nullable = false)
    private Long id;

    @Column(name = "description", nullable = false, length = 500)
    @Comment("일정 내용")
    private String description;

    @Column(name = "location", nullable = false)
    @Comment("장소")
    private String location;

    @Column(name = "start_date", nullable = false)
    @Comment("시작 일시")
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    @Comment("마감 일시")
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Schedule(String description, String location, LocalDateTime startDate, LocalDateTime endDate, Group group,
        Member member) {
        this.description = description;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.group = group;
        this.member = member;
    }

    public void updateSchedule(String description,
        String location, LocalDateTime startDate, LocalDateTime endDate) {
        this.description = description;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
