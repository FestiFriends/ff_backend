package site.festifriends.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import site.festifriends.entity.Post;
import site.festifriends.entity.QGroup;
import site.festifriends.entity.QMember;
import site.festifriends.entity.QPost;
import site.festifriends.entity.QPostImage;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Post> findPostsByGroupIdWithSlice(Long groupId, Long cursorId, Pageable pageable) {
        QPost post = QPost.post;
        QMember author = QMember.member;
        QGroup group = QGroup.group;
        QPostImage image = QPostImage.postImage;

        // If groupId is null, we want to return no results
        if (groupId == null) {
            return new SliceImpl<>(Collections.emptyList(), pageable, false);
        }

        BooleanExpression groupCondition = post.group.id.eq(groupId);
        BooleanExpression notDeletedCondition = post.deleted.isNull();
        BooleanExpression cursorCondition = cursorIdLt(cursorId, post);
        
        BooleanExpression pinnedCondition = null;
        if (cursorId != null) {
            pinnedCondition = post.isPinned.eq(false);
        }

        JPAQuery<Post> query = queryFactory
                .selectFrom(post)
                .join(post.author, author).fetchJoin()
                .join(post.group, group).fetchJoin()
                .leftJoin(post.images, image).fetchJoin()
                .where(
                        groupCondition,
                        notDeletedCondition,
                        cursorCondition,
                        pinnedCondition
                )
                .orderBy(post.isPinned.desc(), post.id.desc());

        // Slice는 size + 1 개를 조회해서 hasNext를 판단
        int size = pageable.getPageSize();
        List<Post> results = query
                .limit(size + 1)
                .fetch();

        boolean hasNext = results.size() > size;
        if (hasNext) {
            results = results.subList(0, size);
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    private BooleanExpression cursorIdLt(Long cursorId, QPost post) {
        return cursorId != null ? post.id.lt(cursorId) : post.id.isNotNull();
    }
}