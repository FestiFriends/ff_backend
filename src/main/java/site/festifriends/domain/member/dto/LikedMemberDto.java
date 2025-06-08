package site.festifriends.domain.member.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import site.festifriends.domain.image.dto.ImageDto;

@Getter
@AllArgsConstructor
public class LikedMemberDto {

    private String name;
    private String gender;
    private Integer age;
    private String userUid;
    private List<ImageDto> profileImage;
    private List<String> hashtag;
    private Long bookmarkId;

}
