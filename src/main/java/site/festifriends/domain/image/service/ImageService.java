package site.festifriends.domain.image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.common.s3.S3Uploader;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Uploader s3Uploader;

    public String getPreSignedUrl(String fileName) {
        if (!validateExtension(fileName)) {
            throw new BusinessException(ErrorCode.INVALID_FILE_EXTENSION, "지원하지 않는 파일 확장자입니다.");
        }
        return s3Uploader.getPreSignedUrl(fileName);
    }

    private boolean validateExtension(String fileName) {
        String[] validExtensions = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

        if (fileName == null || !fileName.contains(".")) {
            return false;
        }

        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        if (extension.isEmpty()) {
            return false;
        }

        for (String validExtension : validExtensions) {
            if (validExtension.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
