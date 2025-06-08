package site.festifriends.domain.comment.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import site.festifriends.entity.Comments;

import java.util.List;

import static site.festifriends.entity.QComments.comments;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Comments> findCommentsByPostIdWithSlice(Long postId, Long cursorId, Pageable pageable) {
        List<Comments> commentsList = queryFactory
            .selectFrom(comments)
            .leftJoin(comments.author).fetchJoin()
            .where(
                comments.post.id.eq(postId),
                comments.deleted.isNull(),
                cursorCondition(cursorId)
            )
            .orderBy(comments.id.asc())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        boolean hasNext = false;
        if (commentsList.size() > pageable.getPageSize()) {
            commentsList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(commentsList, pageable, hasNext);
    }

    private BooleanExpression cursorCondition(Long cursorId) {
        return cursorId != null ? comments.id.gt(cursorId) : null;
    }
}
