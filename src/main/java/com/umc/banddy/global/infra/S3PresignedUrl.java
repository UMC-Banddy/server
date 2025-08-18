package com.umc.banddy.global.infra;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Profile("s3")
@Component
@RequiredArgsConstructor
public class S3PresignedUrl {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * Presigned PUT URL 발급 (업로드 용)
     *
     * @param keyPrefix        저장 경로 prefix (예: voices/123/2025/08/19)
     * @param originalFilename 원본 파일명 (UI 표시용)
     * @param contentType      업로드할 Content-Type (audio/m4a 등)
     */
    public PresignResult presignUrl(String keyPrefix, String originalFilename, String contentType) {
        try {
            // 입력값 검증
            if (originalFilename == null || originalFilename.isBlank()) {
                throw new IllegalArgumentException("파일명이 비어 있습니다.");
            }
            if (contentType == null || contentType.isBlank()) {
                throw new IllegalArgumentException("Content-Type이 비어 있습니다.");
            }
            if (keyPrefix == null || keyPrefix.isBlank()) {
                throw new IllegalArgumentException("keyPrefix가 비어 있습니다.");
            }

            // 확장자 추출 (없으면 예외)
            int dot = originalFilename.lastIndexOf(".");
            if (dot == -1 || dot == originalFilename.length() - 1) {
                throw new IllegalArgumentException("파일 확장자가 없습니다: " + originalFilename);
            }
            String ext = originalFilename.substring(dot).toLowerCase();

            // key = prefix/UUID + ext
            String key = keyPrefix + "/" + UUID.randomUUID() + ext;

            // URL 만료 (10분)
            Date expiration = new Date(System.currentTimeMillis() + 10 * 60 * 1000);

            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, key)
                    .withMethod(HttpMethod.PUT)
                    .withExpiration(expiration);
            req.addRequestParameter("Content-Type", contentType);

            URL url = amazonS3.generatePresignedUrl(req);

            return new PresignResult(
                    url.toString(),
                    key,
                    originalFilename,
                    contentType,
                    expiration.getTime()
            );

        } catch (AmazonS3Exception e) {
            // S3 관련 에러
            throw new RuntimeException("S3 Presigned URL 생성 실패: " + e.getErrorMessage(), e);
        } catch (IllegalArgumentException e) {
            // 잘못된 입력값
            throw e;
        } catch (Exception e) {
            // 기타 예외
            throw new RuntimeException("Presigned URL 생성 중 알 수 없는 오류 발생", e);
        }
    }

    public record PresignResult(
            String uploadUrl,
            String key,
            String originalFilename,
            String contentType,
            long expiresAt
    ) {}
}
