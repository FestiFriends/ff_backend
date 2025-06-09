package site.festifriends.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.domain.image.dto.ImageDto;
import site.festifriends.domain.member.repository.MemberImageRepository;
import site.festifriends.entity.MemberImage;

@Service
@RequiredArgsConstructor
public class MemberImageService {


    private final MemberImageRepository memberImageRepository;

    public void updateProfileImage(Long memberId, ImageDto profileImage) {
        MemberImage memberImage = memberImageRepository.findByMemberId(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "존재하지 않는 회원입니다."));

        memberImage.updateImage(profileImage.getSrc(), profileImage.getAlt());
    }
}
