package site.festifriends.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import site.festifriends.common.config.AuditConfig;
import site.festifriends.common.config.QueryDslConfig;
import site.festifriends.domain.member.dto.LikedMemberDto;
import site.festifriends.entity.Bookmark;
import site.festifriends.entity.Member;
import site.festifriends.entity.enums.BookmarkType;
import site.festifriends.entity.enums.Gender;

@DataJpaTest
@Import({QueryDslConfig.class, AuditConfig.class})
@ActiveProfiles("test")
public class MemberRepositoryTest {

    @Autowired
    private MemberRepositoryImpl memberRepositoryImpl; // 혹은 MemberRepositoryCustom

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Test
    @DisplayName("[성공] 내가 찜한 사용자 목록 조회")
    void getMyLikedMembers_test() {
        // given
        Member member = memberRepository.save(Member.builder()
            .email("test1@example.com")
            .nickname("테스트")
            .profileImageUrl("https://example.com/profiles/test1.jpg")
            .age(28)
            .gender(Gender.MALE)
            .introduction("안녕하세요.")
            .tags(List.of("음악", "여행"))
            .socialId("socialId1")
            .build());

        Member target1 = memberRepository.save(Member.builder()
            .email("test2@example.com")
            .nickname("테스트2")
            .profileImageUrl("https://example.com/profiles/test2.jpg")
            .age(20)
            .gender(Gender.MALE)
            .introduction("안녕하세요.")
            .tags(List.of("음악", "여행"))
            .socialId("socialId2")
            .build());

        Member target2 = memberRepository.save(Member.builder()
            .email("test3@example.com")
            .nickname("테스트3")
            .profileImageUrl("https://example.com/profiles/test3.jpg")
            .age(21)
            .gender(Gender.MALE)
            .introduction("안녕하세요.")
            .tags(List.of("음악", "여행"))
            .socialId("socialId3")
            .build());

        bookmarkRepository.save(
            Bookmark.builder()
                .member(member)
                .type(BookmarkType.MEMBER)
                .targetId(target1.getId())
                .build()
        );

        bookmarkRepository.save(
            Bookmark.builder()
                .member(member)
                .type(BookmarkType.MEMBER)
                .targetId(target2.getId())
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<LikedMemberDto> result = memberRepositoryImpl.getMyLikedMembers(member.getId(), null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.hasNext()).isFalse();
        List<LikedMemberDto> content = result.getContent();
        assertThat(content).isNotEmpty();

        LikedMemberDto dto1 = content.get(0);
        assertThat(dto1.getName()).isEqualTo("테스트3");
        assertThat(dto1.getGender()).isEqualTo("MALE");
        assertThat(dto1.getAge()).isEqualTo(21);
        assertThat(dto1.getProfileImage()).isEqualTo("https://example.com/profiles/test3.jpg");

        LikedMemberDto dto2 = content.get(1);
        assertThat(dto2.getName()).isEqualTo("테스트2");
        assertThat(dto2.getGender()).isEqualTo("MALE");
        assertThat(dto2.getAge()).isEqualTo(20);
        assertThat(dto2.getProfileImage()).isEqualTo("https://example.com/profiles/test2.jpg");
    }

    @Test
    @DisplayName("[성공] 내가 찜한 사용자 수 조회")
    void getMyLikedMembersCount_test() {
        // given
        Member member = memberRepository.save(Member.builder()
            .email("test1@example.com")
            .nickname("테스트")
            .profileImageUrl("https://example.com/profiles/test1.jpg")
            .age(28)
            .gender(Gender.MALE)
            .introduction("안녕하세요.")
            .tags(List.of("음악", "여행"))
            .socialId("socialId1")
            .build());

        Member target1 = memberRepository.save(Member.builder()
            .email("test2@example.com")
            .nickname("테스트2")
            .profileImageUrl("https://example.com/profiles/test2.jpg")
            .age(20)
            .gender(Gender.MALE)
            .introduction("안녕하세요.")
            .tags(List.of("음악", "여행"))
            .socialId("socialId2")
            .build());

        Member target2 = memberRepository.save(Member.builder()
            .email("test3@example.com")
            .nickname("테스트3")
            .profileImageUrl("https://example.com/profiles/test3.jpg")
            .age(21)
            .gender(Gender.MALE)
            .introduction("안녕하세요.")
            .tags(List.of("음악", "여행"))
            .socialId("socialId3")
            .build());

        bookmarkRepository.save(
            Bookmark.builder()
                .member(member)
                .type(BookmarkType.MEMBER)
                .targetId(target1.getId())
                .build()
        );

        bookmarkRepository.save(
            Bookmark.builder()
                .member(member)
                .type(BookmarkType.MEMBER)
                .targetId(target2.getId())
                .build()
        );

        // when
        Long count = memberRepositoryImpl.countMyLikedMembers(member.getId());

        // then
        assertThat(count).isEqualTo(2L);
    }
}
