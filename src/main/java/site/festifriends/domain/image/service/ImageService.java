package site.festifriends.domain.image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.festifriends.common.s3.S3Uploader;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Uploader s3Uploader;

    public String getPreSignedUrl(String fileName) {
        return s3Uploader.getPreSignedUrl(fileName);
    }
}
