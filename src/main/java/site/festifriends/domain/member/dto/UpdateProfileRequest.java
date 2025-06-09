package site.festifriends.domain.member.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.festifriends.domain.image.dto.ImageDto;

@Getter
@NoArgsConstructor
public class UpdateProfileRequest {

    private String name;
    private Integer age;
    private ImageDto profileImage;
    private String description;
    private List<String> hashtag;
    private String sns;
}
