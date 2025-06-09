package site.festifriends.domain.member.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.festifriends.domain.group.dto.GroupSummaryDto;
import site.festifriends.domain.group.repository.GroupRepository;
import site.festifriends.domain.image.dto.ImageDto;
import site.festifriends.domain.member.dto.GetProfileResponse;
import site.festifriends.domain.member.repository.MemberRepository;
import site.festifriends.domain.review.dto.ReviewSummaryDto;
import site.festifriends.domain.review.repository.ReviewRepository;
import site.festifriends.entity.enums.ReviewTag;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final GroupRepository groupRepository;

    public GetProfileResponse getMemberProfile(Long targetId, Long memberId) {
        Object[] memberData = memberRepository.getMemberProfile(targetId);
        Object[] extraData = memberRepository.getMemberExtraData(targetId, memberId);
        Object[] reviewData = reviewRepository.getMemberReviewCount(targetId);
        List<Object[]> reviewTagData = reviewRepository.countEachReviewTag(targetId);
        List<String> reviewContents = reviewRepository.getMemberReviewContent(targetId);
        Object[] groupData = groupRepository.getMemberGroupCount(targetId);
        
        Map<ReviewTag, Integer> countMap = new EnumMap<>(ReviewTag.class);
        for (Object[] row : reviewTagData) {
            ReviewTag tag = ReviewTag.valueOf((String) row[0]);
            Integer count = ((Number) row[1]).intValue();
            countMap.put(tag, count);
        }

        GroupSummaryDto groupDto = GroupSummaryDto.builder()
            .joinedCount(groupData != null && groupData[0] != null ? ((Number) groupData[0]).intValue() : 0)
            .totalJoinedCount(groupData != null && groupData[1] != null ? ((Number) groupData[1]).intValue() : 0)
            .createdCount(groupData != null && groupData[2] != null ? ((Number) groupData[2]).intValue() : 0)
            .build();

        ReviewSummaryDto reviewSummaryDto = ReviewSummaryDto.builder()
            .PUNCTUAL(countMap.getOrDefault(ReviewTag.PUNCTUAL, 0))
            .POLITE(countMap.getOrDefault(ReviewTag.POLITE, 0))
            .COMFORTABLE(countMap.getOrDefault(ReviewTag.COMFORTABLE, 0))
            .COMMUNICATIVE(countMap.getOrDefault(ReviewTag.COMMUNICATIVE, 0))
            .CLEAN(countMap.getOrDefault(ReviewTag.CLEAN, 0))
            .RESPONSIVE(countMap.getOrDefault(ReviewTag.RESPONSIVE, 0))
            .RECOMMEND(countMap.getOrDefault(ReviewTag.RECOMMEND, 0))
            .build();

        return GetProfileResponse.builder()
            .id(memberData[0].toString())
            .name((String) memberData[1])
            .age((Integer) memberData[2])
            .gender((String) memberData[3])
            .profileImage(parseImage((String) memberData[4]))
            .description((String) memberData[5])
            .hashtags(memberData[6] == null ? new ArrayList<>() :
                Arrays.stream(((String) memberData[6]).split(","))
                    .map(tag -> "#" + tag)
                    .collect(Collectors.toList()))
            .sns((String) memberData[7])
            .isLiked(((Number) extraData[0]).intValue() == 1)
            .isReported(((Number) extraData[1]).intValue() == 1)
            .isMine(((Number) extraData[2]).intValue() == 1)
            .groupSummary(groupDto)
            .reviewSummary(reviewSummaryDto)
            .rating(reviewData != null && reviewData[0] != null ? ((Number) reviewData[0]).doubleValue() : 0.0)
            .reviewCount(reviewData != null && reviewData[1] != null ? ((Number) reviewData[1]).intValue() : 0)
            .reviewList(reviewContents != null ? reviewContents : new ArrayList<>())
            .build();
    }

    private ImageDto parseImage(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String[] parts = value.split("\\|");
        String id = parts.length > 0 ? parts[0] : null;
        String src = parts.length > 1 ? parts[1] : null;
        String alt = parts.length > 2 ? parts[2] : null;
        return ImageDto.builder()
            .id(id)
            .src(src)
            .alt(alt)
            .build();
    }
}
