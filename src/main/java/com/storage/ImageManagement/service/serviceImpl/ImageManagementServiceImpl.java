package com.storage.ImageManagement.service.serviceImpl;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.storage.ImageManagement.exceptions.FileDownloadException;
import com.storage.ImageManagement.service.ImageManagementService;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
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
    if(isBucketEmpty()){
      throw new FileDownloadException("Request bucket does not exist or empty");
    }

    S3Object s3Object = s3Client.getObject(bucketName, fileName);
    try(S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent()){
      try(FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
        byte[] read_buf = new byte[1024];
        int read_len = 0;
        while ((read_len = s3ObjectInputStream.read(read_buf)) > 0) {
          fileOutputStream.write(read_buf, 0, read_len);
        }
      }
      Path path = Paths.get(fileName);
      Resource resource = new UrlResource(path.toUri());

      if(resource.exists() || resource.isReadable()){
        return resource;
      } else {
        throw new FileDownloadException("Could not download the file.");
      }
    }
  }

  @Override
  public boolean delete(String fileName) {
    return false;
  }

  public List<S3ObjectSummary> allFiles(){
    List<S3ObjectSummary> s3ObjectSummaries = new ArrayList<>();
    ListObjectsV2Result result = s3Client.listObjectsV2(bucketName);
    List<S3ObjectSummary> objectSummaries = result.getObjectSummaries();
    for(S3ObjectSummary os : objectSummaries){
      log.info("S3ObjectSummaries => ", os);
      s3ObjectSummaries.add(os);
    }

    return s3ObjectSummaries;
  }


  private boolean isBucketEmpty(){
    ListObjectsV2Result result = s3Client.listObjectsV2(this.bucketName);
    if(result == null){
      return false;
    }

    List<S3ObjectSummary> objects = result.getObjectSummaries();
    return objects.isEmpty();
  }

  private String generateFileName(MultipartFile multipartFile){
    return new Date().getTime()+"-"+multipartFile.getOriginalFilename().replace(" ", "-");
  }
}
