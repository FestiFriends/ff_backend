package site.festifriends.domain.auth;

import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import site.festifriends.entity.Member;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final Long memberId;
    private final String nickname;

    public UserDetailsImpl(Long memberId, String nickname) {
        this.memberId = memberId;
        this.nickname = nickname;
    }

    public static UserDetailsImpl of(Member member) {
        return new UserDetailsImpl(member.getId(), member.getNickname());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.nickname;
    }
}
