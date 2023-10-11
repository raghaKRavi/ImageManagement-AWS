package com.storage.ImageManagement.service.serviceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.storage.ImageManagement.exceptions.FileDownloadException;
import com.storage.ImageManagement.service.ImageManagementService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Service
@Log4j2
public class ImageManagementServiceImpl implements ImageManagementService {

  @Value("${aws.bucket.name}")
  private String bucketName;

  private final AmazonS3 s3Client;

  public ImageManagementServiceImpl(AmazonS3 s3Client) {
    this.s3Client = s3Client;
  }

  @Override
  public String uploadFile(MultipartFile multipartFile) throws IOException {
    //Convert multipart file to a file
    File file = new File(multipartFile.getOriginalFilename());
    try(FileOutputStream fileOutputStream = new FileOutputStream(file)){
      fileOutputStream.write(multipartFile.getBytes());
    }

    // generate filename
    String fileName = generateFileName(multipartFile);

    //upload file
    PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file);
    s3Client.putObject(putObjectRequest);

    //delete file
    file.delete();

    return fileName;
  }

  @Override
  public Object downloadFile(String fileName) throws
      FileDownloadException,
      IOException {
    return null;
  }

  @Override
  public boolean delete(String fileName) {
    return false;
  }

  private String generateFileName(MultipartFile multipartFile){
    return new Date().getTime()+"-"+multipartFile.getOriginalFilename().replace(" ", "-");
  }
}
