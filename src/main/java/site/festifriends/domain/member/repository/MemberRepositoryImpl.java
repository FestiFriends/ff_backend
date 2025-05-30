package site.festifriends.domain.member.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.member.dto.MemberDto;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public CursorResponseWrapper<MemberDto> getMyLikedMembers(Long memberId, Long cursorId, Pageable pageable) {
        int pageSize = pageable.getPageSize() + 1;

        List<Long> bookmarkIds = new ArrayList<>();

        String sql = """
            SELECT m.nickname, m.gender, m.age, m.member_id, m.profile_image_url, GROUP_CONCAT(mt.tag) as tags, b.bookmark_id
            FROM bookmark b
            JOIN member m ON b.member_id = m.member_id
            LEFT JOIN member_tags mt ON m.member_id = mt.member_id
            WHERE b.member_id = :memberId
            AND b.type = 'MEMBER'
            AND (:cursorId IS NULL OR b.bookmark_id < :cursorId)
            GROUP BY m.nickname, m.gender, m.age, m.member_id, m.profile_image_url, b.bookmark_id
            ORDER BY b.bookmark_id DESC
            LIMIT :pageSize
            """;

        Query query = em.createNativeQuery(sql);

        query.setParameter("memberId", memberId);
        query.setParameter("cursorId", cursorId);
        query.setParameter("pageSize", pageSize);

        List<Object[]> resultList = query.getResultList();

        List<MemberDto> members = resultList.stream()
            .map(row -> {
                bookmarkIds.add(((Number) row[6]).longValue());
                return new MemberDto(
                    (String) row[0],
                    (String) row[1],
                    (Integer) row[2],
                    row[3].toString(),
                    false,
                    (String) row[4],
                    row[5] == null ? new ArrayList<>() :
                        Arrays.stream(((String) row[5]).split(","))
                            .map(tag -> "#" + tag)
                            .collect(Collectors.toList())
                );
            })
            .collect(Collectors.toList());

        Long nextCursorId = bookmarkIds.size() == pageSize ? bookmarkIds.get(pageSize - 1) : null;
        boolean hasNext = members.size() == pageSize;
        if (hasNext) {
            members.remove(pageSize - 1);
        }

        return CursorResponseWrapper.success(
            "요청이 성공적으로 처리되었습니다.",
            members,
            nextCursorId,
            hasNext
        );
    }
}
