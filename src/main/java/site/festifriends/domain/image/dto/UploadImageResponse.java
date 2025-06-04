package site.festifriends.domain.image.dto;

import lombok.Getter;

@Getter
public class UploadImageResponse {

    private String presignedUrl;

    public UploadImageResponse(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }
}
