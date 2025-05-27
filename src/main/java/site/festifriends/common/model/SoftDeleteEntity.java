package site.festifriends.common.model;

import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.Comment;

@Getter
public class SoftDeleteEntity extends BaseEntity {

    @Column(name = "deleted")
    @Comment("삭제 시간")
    private LocalDateTime deleted;

    public boolean isDeleted() {
        return deleted != null;
    }

    public void delete() {
        this.deleted = LocalDateTime.now();
    }

}
