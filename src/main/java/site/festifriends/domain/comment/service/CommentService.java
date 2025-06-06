package site.festifriends.domain.comment.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.application.repository.ApplicationRepository;
import site.festifriends.domain.comment.dto.CommentCreateRequest;
import site.festifriends.domain.comment.dto.CommentListRequest;
import site.festifriends.domain.comment.dto.CommentResponse;
import site.festifriends.domain.comment.dto.CommentUpdateRequest;
import site.festifriends.domain.comment.repository.CommentRepository;
import site.festifriends.domain.group.repository.GroupRepository;
import site.festifriends.domain.member.repository.MemberRepository;
import site.festifriends.domain.post.repository.PostRepository;
import site.festifriends.entity.Comments;
import site.festifriends.entity.Group;
import site.festifriends.entity.Member;
import site.festifriends.entity.Post;
import site.festifriends.entity.enums.Role;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final ApplicationRepository applicationRepository;

    /**
     * 게시글의 댓글 목록 조회
     */
    @Transactional(readOnly = true)
    public CursorResponseWrapper<CommentResponse> getCommentsByPostId(Long groupId, Long postId, Long memberId,
        CommentListRequest request) {
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

    /**
     * 게시글에 댓글 작성
     */
    @Transactional
    public void createComment(Long groupId, Long postId, Long memberId, CommentCreateRequest request) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));

        if (!post.getGroup().getId().equals(groupId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "잘못된 요청입니다.");
        }

        Member author = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        boolean isMember = applicationRepository.existsByGroupIdAndMemberIdAndRole(groupId, memberId, Role.MEMBER) ||
            applicationRepository.existsByGroupIdAndMemberIdAndRole(groupId, memberId, Role.HOST);

        if (!isMember) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "댓글 등록 권한이 없습니다.");
        }

        Comments comment = Comments.builder()
            .post(post)
            .author(author)
            .content(request.getContent())
            .build();

        commentRepository.save(comment);
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public void updateComment(Long groupId, Long postId, Long commentId, Long memberId, CommentUpdateRequest request) {
        validateCommentAccess(groupId, postId, commentId);

        Comments comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."));

        if (!comment.isMine(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "댓글 수정 권한이 없습니다.");
        }

        comment.updateContent(request.getContent());
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long groupId, Long postId, Long commentId, Long memberId) {
        validateCommentAccess(groupId, postId, commentId);

        Comments comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."));

        if (!comment.isMine(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "댓글 삭제 권한이 없습니다.");
        }

        comment.delete();
    }

    /**
     * 댓글 접근 권한 검증 (공통 로직)
     */
    private void validateCommentAccess(Long groupId, Long postId, Long commentId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));

        if (!post.getGroup().getId().equals(groupId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "잘못된 요청입니다.");
        }

        Comments comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."));

        if (!comment.getPost().getId().equals(postId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "잘못된 요청입니다.");
        }
    }
}
