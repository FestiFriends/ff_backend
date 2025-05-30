package site.festifriends.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.application.repository.ApplicationRepository;
import site.festifriends.domain.post.dto.PostGroupResponse;
import site.festifriends.domain.post.dto.PostListRequest;
import site.festifriends.domain.post.dto.PostListResponse;
import site.festifriends.domain.post.dto.PostResponse;
import site.festifriends.domain.post.repository.PostRepository;
import site.festifriends.entity.Group;
import site.festifriends.entity.Post;
import site.festifriends.entity.enums.Role;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ApplicationRepository applicationRepository;

    /**
     * 모임 내 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public CursorResponseWrapper<PostResponse> getPostsByGroupId(Long groupId, Long memberId, PostListRequest request) {
        // 모임에 속한 회원인지 확인
        boolean isMember = applicationRepository.existsByGroupIdAndMemberIdAndRole(groupId, memberId, Role.MEMBER) ||
                applicationRepository.existsByGroupIdAndMemberIdAndRole(groupId, memberId, Role.HOST);

        if (!isMember) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "해당 모임에 속한 회원만 게시글을 조회할 수 있습니다.");
        }

        Long cursorId = null;
        if (request.getCursorId() != null && !request.getCursorId().isEmpty()) {
            try {
                cursorId = Long.parseLong(request.getCursorId());
            } catch (NumberFormatException e) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "유효하지 않은 커서 ID입니다.");
            }
        }

        int size = request.getSize() != null ? request.getSize() : 20;
        PageRequest pageable = PageRequest.of(0, size);

        Slice<Post> postSlice = postRepository.findPostsByGroupIdWithSlice(groupId, cursorId, pageable);

        List<PostResponse> postResponses = postSlice.getContent().stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());

        if (postResponses.isEmpty()) {
            return CursorResponseWrapper.empty("게시글 목록이 정상적으로 조회되었습니다.");
        }

        Long nextCursorId = null;
        if (postSlice.hasNext()) {
            nextCursorId = postResponses.get(postResponses.size() - 1).getId();
        }

        return CursorResponseWrapper.success(
                "게시글 목록 조회 성공.",
                postResponses,
                nextCursorId,
                postSlice.hasNext()
        );
    }
}
