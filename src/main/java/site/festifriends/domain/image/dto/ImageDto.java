package site.festifriends.domain.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ImageDto {

    private String id;
    private String src;
    private String alt;
}
