package site.festifriends.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.festifriends.domain.auth.repository.BlackListTokenRepository;
import site.festifriends.entity.BlackListToken;

@Service
@RequiredArgsConstructor
public class BlackListTokenService {

    private final BlackListTokenRepository blackListTokenRepository;

    public void addBlackListToken(String token) {
        blackListTokenRepository.save(new BlackListToken(token));
    }

    public boolean isBlackListed(String token) {
        return blackListTokenRepository.existsByToken(token);
    }
}
