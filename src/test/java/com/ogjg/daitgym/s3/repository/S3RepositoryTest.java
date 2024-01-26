package com.ogjg.daitgym.s3.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
class S3RepositoryTest {

    @InjectMocks
    S3Repository s3Repository;
    @Mock
    S3Client s3Client;

    @Test
    @DisplayName("[S3 Bucket 이미지 업로드] S3 버킷에 이미지 업로드하기")
    void saveProfileImage() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test data".getBytes());

        String expectedUrl = "https://example.com/test-image.jpg";

        S3Utilities s3UtilitiesMock = mock(S3Utilities.class);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(mock(PutObjectResponse.class));

        given(s3Client.utilities()).willReturn(s3UtilitiesMock);
        given(s3UtilitiesMock.getUrl(any(GetUrlRequest.class)))
                .willReturn(new URL(expectedUrl));

        ReflectionTestUtils.setField(s3Repository, "bucketName", "test-bucket");

        String resultUrl = s3Repository.uploadImageToS3(mockFile);

        assertThat(resultUrl).isNotNull();
        assertThat(resultUrl).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("[S3 Image 삭제] S3에 저장된 사진 삭제하기")
    void deleteImageFromS3() {
        String fileUrl = "https://example.com/test-image.jpg";

        String expectedKey = "test-image.jpg";
        DeleteObjectResponse deleteObjectResponse = mock(DeleteObjectResponse.class);

        ReflectionTestUtils.setField(s3Repository, "bucketName", "test-bucket");
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(deleteObjectResponse);

        s3Repository.deleteImageFromS3(fileUrl);
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));

        ArgumentCaptor<DeleteObjectRequest> deleteObjectRequestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(deleteObjectRequestCaptor.capture());
        DeleteObjectRequest capturedDeleteObjectRequest = deleteObjectRequestCaptor.getValue();

        assertThat(expectedKey).isEqualTo(capturedDeleteObjectRequest.key());
    }
}