package site.festifriends.domain.member.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import site.festifriends.domain.member.dto.LikedMemberResponse;
import site.festifriends.domain.member.dto.LikedPerformanceDto;
import site.festifriends.domain.member.dto.LikedPerformanceResponse;
import site.festifriends.domain.member.repository.BookmarkRepository;
import site.festifriends.domain.member.repository.MemberImageRepository;
import site.festifriends.domain.member.repository.MemberRepository;
import site.festifriends.domain.performance.repository.PerformanceRepository;
import site.festifriends.entity.Member;
import site.festifriends.entity.MemberImage;
import site.festifriends.entity.enums.Gender;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BlackListTokenService blackListTokenService;
    private final PerformanceRepository performanceRepository;
    private final BookmarkRepository bookmarkRepository;
    private final MemberImageRepository memberImageRepository;

    public Member loginOrSignUp(KakaoUserInfo userInfo) {

        Member member = memberRepository.findBySocialId(userInfo.getSocialId()).orElse(null);

        if (member == null) {
            Member newMember = memberRepository.save(Member.builder()
                .socialId(userInfo.getSocialId())
                .nickname(userInfo.getName())
                .email(userInfo.getEmail())
                .age(0)
                .gender(Gender.ALL)
                .introduction("")
                .build());

            MemberImage memberImage = MemberImage.builder()
                .member(newMember)
                .src(userInfo.getProfileImage())
                .alt("멤버 이미지")
                .build();

            memberImageRepository.save(memberImage);
        }

        return member;
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

    public CursorResponseWrapper<LikedMemberResponse> getMyLikedMembers(Long memberId, Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        Slice<LikedMemberDto> slice = memberRepository.getMyLikedMembers(memberId, cursorId, pageable);

        if (slice.isEmpty()) {
            return CursorResponseWrapper.empty("요청이 성공적으로 처리되었습니다.");
        }

        List<LikedMemberResponse> response = new ArrayList<>();

        for (LikedMemberDto likedMember : slice.getContent()) {
            response.add(new LikedMemberResponse(
                likedMember.getName(),
                likedMember.getGender(),
                likedMember.getAge(),
                likedMember.getUserUid(),
                likedMember.getProfileImage(),
                likedMember.getHashtag(),
                true
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

    public CursorResponseWrapper<LikedPerformanceResponse> getMyLikedPerformances(Long memberId, Long cursorId,
        int size) {
        Pageable pageable = PageRequest.of(0, size);

        Slice<LikedPerformanceDto> slice = memberRepository.getMyLikedPerformances(memberId, cursorId, pageable);

        if (slice.isEmpty()) {
            return CursorResponseWrapper.empty("요청이 성공적으로 처리되었습니다.");
        }

        List<Long> performanceIds = slice.getContent().stream()
            .map(LikedPerformanceDto::getId)
            .collect(Collectors.toList());

        Map<Long, Integer> groupCounts = performanceRepository.getGroupCountsByPerformanceIds(performanceIds);
        Map<Long, Integer> favoriteCounts = bookmarkRepository.getCountByPerformanceIds(performanceIds);

        List<LikedPerformanceResponse> response = new ArrayList<>();

        for (LikedPerformanceDto likedPerformance : slice.getContent()) {
            response.add(new LikedPerformanceResponse(
                likedPerformance.getId(),
                likedPerformance.getTitle(),
                likedPerformance.getStartDate(),
                likedPerformance.getEndDate(),
                likedPerformance.getLocation(),
                likedPerformance.getCast(),
                likedPerformance.getCrew(),
                likedPerformance.getRuntime(),
                likedPerformance.getAge(),
                likedPerformance.getProductionCompany(),
                likedPerformance.getAgency(),
                likedPerformance.getHost(),
                likedPerformance.getOrganizer(),
                likedPerformance.getPrice(),
                likedPerformance.getPoster(),
                likedPerformance.getState(),
                likedPerformance.getVisit(),
                likedPerformance.getImages(),
                likedPerformance.getTime(),
                groupCounts.getOrDefault(likedPerformance.getId(), 0),
                favoriteCounts.getOrDefault(likedPerformance.getId(), 0),
                true
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

    public void updateMemberProfile(Long memberId, String name, Integer age, String description, List<String> hashtag,
        String sns) {
        Member member = getMemberById(memberId);

        member.updateProfile(name, age, description, hashtag, sns);
    }

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "존재하지 않는 회원입니다."));
    }
}