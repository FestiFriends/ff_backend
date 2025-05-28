package site.festifriends.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.festifriends.common.model.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "black_list_token")
public class BlackListToken extends BaseEntity {

    private String token;

    public BlackListToken(String token) {
        this.token = token;
    }

    public void updateToken(String token) {
        this.token = token;
    }
}
