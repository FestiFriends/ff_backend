package site.festifriends.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.application.repository.ApplicationRepository;
import site.festifriends.domain.comment.dto.CommentListRequest;
import site.festifriends.domain.comment.dto.CommentResponse;
import site.festifriends.domain.comment.repository.CommentRepository;
import site.festifriends.domain.group.repository.GroupRepository;
import site.festifriends.domain.post.repository.PostRepository;
import site.festifriends.entity.Comments;
import site.festifriends.entity.Group;
import site.festifriends.entity.Post;
import site.festifriends.entity.enums.Role;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final GroupRepository groupRepository;
    private final ApplicationRepository applicationRepository;

    /**
     * 게시글의 댓글 목록 조회
     */
    @Transactional(readOnly = true)
    public CursorResponseWrapper<CommentResponse> getCommentsByPostId(Long groupId, Long postId, Long memberId, CommentListRequest request) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));

        if (!post.getGroup().getId().equals(groupId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "해당 모임에 속한 게시글이 아닙니다.");
        }

        boolean isMember = applicationRepository.existsByGroupIdAndMemberIdAndRole(groupId, memberId, Role.MEMBER) ||
            applicationRepository.existsByGroupIdAndMemberIdAndRole(groupId, memberId, Role.HOST);

        if (!isMember) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "해당 모임에 속한 회원만 댓글을 조회할 수 있습니다.");
        }

        Long cursorId = request.getCursorId();
        int size = request.getSize() != null ? request.getSize() : 20;
        PageRequest pageable = PageRequest.of(0, size);

        Slice<Comments> commentSlice = commentRepository.findCommentsByPostIdWithSlice(postId, cursorId, pageable);

        List<CommentResponse> commentResponses = commentSlice.getContent().stream()
            .map(comment -> CommentResponse.from(comment, memberId))
            .collect(Collectors.toList());

        if (commentResponses.isEmpty()) {
            return CursorResponseWrapper.empty("댓글 목록이 정상적으로 조회되었습니다.");
        }

        Long nextCursorId = null;
        if (commentSlice.hasNext()) {
            nextCursorId = commentResponses.get(commentResponses.size() - 1).getId();
        }

        return CursorResponseWrapper.success(
            "댓글 목록을 불러오는데 성공하였습니다.",
            commentResponses,
            nextCursorId,
            commentSlice.hasNext()
        );
    }
}
