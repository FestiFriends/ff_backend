package site.festifriends.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import site.festifriends.common.model.SoftDeleteEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "festival")
public class Festival extends SoftDeleteEntity {

    @Column(name = "title", nullable = false)
    @Comment("공연 제목")
    private String title;
    @Column(name = "poster_url", nullable = false)
    @Comment("공연 포스터 URL")
    private String posterUrl;
    @Column(name = "start_date", nullable = false)
    @Comment("공연 시작 날짜")
    private Date startDate;
    @Column(name = "end_date", nullable = false)
    @Comment("공연 종료 날짜")
    private Date endDate;
    @Column(name = "location", nullable = false)
    @Comment("공연 장소(공연장명)")
    private String location;
    @Column(name = "cast", nullable = false)
    @Comment("공연 출연진")
    private String cast;
    @Column(name = "price", nullable = false)
    @Comment("티켓 가격")
    private Integer price;
    @Column(name = "state", nullable = false)
    @Comment("공연 상태(공연 예정/공연 중/공연 종료)")
    private String state;
    @Column(name = "visit", nullable = false)
    @Comment("내한 여부")
    private Boolean visit;
}
