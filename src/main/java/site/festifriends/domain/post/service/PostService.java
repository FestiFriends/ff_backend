package site.festifriends.domain.post.service;

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
import site.festifriends.domain.group.repository.GroupRepository;
import site.festifriends.domain.member.repository.MemberRepository;
import site.festifriends.domain.post.dto.PostCreateRequest;
import site.festifriends.domain.post.dto.PostCreateResponse;
import site.festifriends.domain.post.dto.PostListRequest;
import site.festifriends.domain.post.dto.PostPinRequest;
import site.festifriends.domain.post.dto.PostReactionRequest;
import site.festifriends.domain.post.dto.PostResponse;
import site.festifriends.domain.post.dto.PostUpdateDeleteResponse;
import site.festifriends.domain.post.dto.PostUpdateRequest;
import site.festifriends.domain.post.repository.PostImageRepository;
import site.festifriends.domain.post.repository.PostReactionRepository;
import site.festifriends.domain.post.repository.PostRepository;
import site.festifriends.entity.Group;
import site.festifriends.entity.Member;
import site.festifriends.entity.Post;
import site.festifriends.entity.PostImage;
import site.festifriends.entity.PostReaction;
import site.festifriends.entity.enums.Role;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostReactionRepository postReactionRepository;
    private final ApplicationRepository applicationRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final PostImageRepository postImageRepository;

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

        Long cursorId = request.getCursorId();

        int size = request.getSize() != null ? request.getSize() : 20;
        PageRequest pageable = PageRequest.of(0, size);

        Slice<Post> postSlice = postRepository.findPostsByGroupIdWithSlice(groupId, cursorId, pageable);

        List<PostResponse> postResponses = postSlice.getContent().stream()
            .map(post -> {
                boolean hasReactioned = postReactionRepository.existsByPostIdAndMemberId(post.getId(), memberId);
                return PostResponse.from(post, memberId, hasReactioned);
            })
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

    /**
     * 모임 내 게시글 등록
     */
    @Transactional
    public PostCreateResponse createPost(Long groupId, Long memberId, PostCreateRequest request) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 회원을 찾을 수 없습니다."));

        boolean isMember = applicationRepository.existsByGroupIdAndMemberIdAndRole(groupId, memberId, Role.MEMBER) ||
            applicationRepository.existsByGroupIdAndMemberIdAndRole(groupId, memberId, Role.HOST);

        if (!isMember) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "해당 모임에 속한 회원만 게시글을 등록할 수 있습니다.");
        }

        Post post = Post.builder()
            .group(group)
            .author(member)
            .content(request.getContent())
            .build();

        if (Boolean.TRUE.equals(request.getIsPinned())) {
            postRepository.unpinAllPostsInGroup(groupId);
            post.setPinned(true);
        }

        Post savedPost = postRepository.save(post);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<PostImage> images = request.getImages().stream()
                .map(image -> PostImage.builder()
                    .post(savedPost)
                    .src(image.getSrc())
                    .alt(image.getAlt())
                    .build())
                .collect(Collectors.toList());

            postImageRepository.saveAll(images);
        }

        return PostCreateResponse.success();
    }

    /**
     * 모임 내 게시글 수정
     */
    @Transactional
    public PostUpdateDeleteResponse updatePost(Long groupId, Long postId, Long memberId, PostUpdateRequest request) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));

        if (!post.getGroup().getId().equals(groupId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "해당 모임에 속한 게시글이 아닙니다.");
        }

        if (!post.isMine(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "게시글 수정 권한이 없습니다.");
        }

        if (request.getContent() != null) {
            post.updateContent(request.getContent());
        }

        if (request.getIsPinned() != null) {
            if (Boolean.TRUE.equals(request.getIsPinned())) {
                postRepository.unpinAllPostsInGroup(groupId);
            }
            post.setPinned(request.getIsPinned());
        }

        if (request.getImages() != null) {
            postImageRepository.deleteByPostId(postId);

            if (!request.getImages().isEmpty()) {
                List<PostImage> images = request.getImages().stream()
                    .map(image -> PostImage.builder()
                        .post(post)
                        .src(image.getSrc())
                        .alt(image.getAlt())
                        .build())
                    .collect(Collectors.toList());

                postImageRepository.saveAll(images);
            }
        }

        return PostUpdateDeleteResponse.success();
    }

    /**
     * 모임 내 게시글 삭제
     */
    @Transactional
    public PostUpdateDeleteResponse deletePost(Long groupId, Long postId, Long memberId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));

        if (!post.getGroup().getId().equals(groupId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "해당 모임에 속한 게시글이 아닙니다.");
        }

        if (!post.isMine(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "게시글 삭제 권한이 없습니다.");
        }

        postImageRepository.deleteByPostId(postId);
        post.delete();

        return PostUpdateDeleteResponse.success();
    }

    /**
     * 모임 내 게시글 고정/해제
     */
    @Transactional
    public void pinPost(Long groupId, Long postId, Long memberId, PostPinRequest request) {
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
            throw new BusinessException(ErrorCode.FORBIDDEN, "해당 모임에 속한 회원만 게시글을 고정/해제할 수 있습니다.");
        }

        boolean isPinned = Boolean.TRUE.equals(request.getIsPinned());

        if (isPinned) {
            postRepository.unpinAllPostsInGroup(groupId);
        }
        post.setPinned(isPinned);
    }

    /**
     * 모임 내 게시글 반응 등록/취소
     */
    @Transactional
    public void togglePostReaction(Long groupId, Long postId, Long memberId, PostReactionRequest request) {
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
            throw new BusinessException(ErrorCode.FORBIDDEN, "해당 모임에 속한 회원만 게시글에 반응할 수 있습니다.");
        }

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 회원을 찾을 수 없습니다."));

        boolean hasReactioned = Boolean.TRUE.equals(request.getHasReactioned());
        boolean currentlyReacted = postReactionRepository.existsByPostIdAndMemberId(postId, memberId);

        if (hasReactioned && !currentlyReacted) {
            PostReaction reaction = PostReaction.builder()
                .post(post)
                .member(member)
                .build();
            postReactionRepository.save(reaction);
            post.incrementReactionCount();
        } else if (!hasReactioned && currentlyReacted) {
            postReactionRepository.deleteByPostIdAndMemberId(postId, memberId);
            post.decrementReactionCount();
        }
    }
}
