package site.festifriends.domain.image.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.image.dto.UploadImageRequest;
import site.festifriends.domain.image.dto.UploadImageResponse;
import site.festifriends.domain.image.service.ImageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/presigned")
    public ResponseEntity<?> getPreSignedUrl(@Valid @RequestBody UploadImageRequest request) {
        return ResponseEntity.ok().body(ResponseWrapper.success(
            "url이 성공적으로 생성되었습니다.",
            new UploadImageResponse(imageService.getPreSignedUrl(request.getFileName())))
        );
    }
}
