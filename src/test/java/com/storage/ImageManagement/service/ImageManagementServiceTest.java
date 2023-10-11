package com.storage.ImageManagement.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.storage.ImageManagement.service.serviceImpl.ImageManagementServiceImpl;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Date;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ImageManagementServiceTest {

  @InjectMocks
  private ImageManagementServiceImpl imageManagementService;

  @Mock
  private AmazonS3 amazonS3;

  @Test
  public void testFileUpload_WhenValidInputThenReturn_fileName() throws Exception{
    //arrange
    S3Mock api = new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
    api.start();
    AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration("http://localhost:8001", "us-west-2");
    amazonS3 = AmazonS3ClientBuilder
        .standard()
        .withPathStyleAccessEnabled(true)
        .withEndpointConfiguration(endpoint)
        .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials())).build();
    amazonS3.createBucket("testbucket");

    String fileName = "map-onepiece.jpeg";
    String expectedValue = new Date().getTime()+"-"+fileName.replace(" ","-");
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", fileName,
        "image/jpeg", "Some bytes".getBytes());
    //act
    String returnValue = imageManagementService.uploadFile(mockMultipartFile);
    //assert
    Assertions.assertEquals(expectedValue.contains(fileName), returnValue.contains(fileName));
    api.stop();
  }
}
