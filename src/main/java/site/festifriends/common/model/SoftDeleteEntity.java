package site.festifriends.common.model;

import jakarta.persistence.Column;
import java.time.LocalDateTime;
import org.hibernate.annotations.Comment;

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
