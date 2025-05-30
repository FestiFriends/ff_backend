package site.festifriends.domain.member.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikedMemberDto {

    private String name;
    private String gender;
    private Integer age;
    private String userUid;
    private Boolean isUserNew;
    private String profileImage;
    private List<String> hashtag;
    private Long bookmarkId;

}
