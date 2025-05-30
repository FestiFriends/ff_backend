package site.festifriends.domain.member.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.common.jwt.TokenResolver;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.auth.KakaoUserInfo;
import site.festifriends.domain.auth.service.BlackListTokenService;
import site.festifriends.domain.member.dto.LikedMemberDto;
import site.festifriends.domain.member.dto.MemberDto;
import site.festifriends.domain.member.repository.MemberRepository;
import site.festifriends.entity.Member;
import site.festifriends.entity.enums.Gender;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BlackListTokenService blackListTokenService;

    public Member loginOrSignUp(KakaoUserInfo userInfo) {

        return memberRepository.findBySocialId(userInfo.getSocialId())
            .orElseGet(() -> {
                Member newMember = Member.builder()
                    .socialId(userInfo.getSocialId())
                    .nickname(userInfo.getName())
                    .email(userInfo.getEmail())
                    .profileImageUrl(userInfo.getProfileImage())
                    .age(0)
                    .gender(Gender.ALL)
                    .introduction("")
                    .build();

                return memberRepository.save(newMember);
            });
    }

    public void saveRefreshToken(Long memberId, String refreshToken) {
        Member member = getMemberById(memberId);
        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);
    }

    public void deleteMember(Long memberId, HttpServletRequest request) {
        Member member = getMemberById(memberId);

        memberRepository.deleteMember(member);

        String accessToken = TokenResolver.extractAccessToken(request);
        String refreshToken = TokenResolver.extractRefreshToken(request);

        blackListTokenService.addBlackListToken(accessToken);
        blackListTokenService.addBlackListToken(refreshToken);
    }

    public CursorResponseWrapper<MemberDto> getMyLikedMembers(Long memberId, Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        Slice<LikedMemberDto> slice = memberRepository.getMyLikedMembers(memberId, cursorId, pageable);

        if (slice.isEmpty()) {
            return CursorResponseWrapper.empty("요청이 성공적으로 처리되었습니다.");
        }

        List<MemberDto> response = new ArrayList<>();

        for (LikedMemberDto likedMember : slice.getContent()) {
            response.add(new MemberDto(
                likedMember.getName(),
                likedMember.getGender(),
                likedMember.getAge(),
                likedMember.getUserUid(),
                likedMember.getIsUserNew(),
                likedMember.getProfileImage(),
                likedMember.getHashtag()
            ));
        }
        Long nextCursorId = null;
        if (slice.hasNext()) {
            response.remove(response.size() - 1);
            nextCursorId = slice.getContent().get(size).getBookmarkId();
        }

        return CursorResponseWrapper.success(
            "요청이 성공적으로 처리되었습니다.",
            response,
            nextCursorId,
            slice.hasNext()
        );
    }

    public Long getMyLikedMembersCount(Long memberId) {
        return memberRepository.countMyLikedMembers(memberId);
    }

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "존재하지 않는 회원입니다."));
    }
}
