package com.storage.ImageManagement.service;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.storage.ImageManagement.exceptions.FileDownloadException;
import com.storage.ImageManagement.exceptions.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageManagementService {
  String uploadFile(MultipartFile multipartFile) throws FileUploadException, IOException;

  Object downloadFile(String fileName) throws FileDownloadException, IOException;

  boolean delete(String fileName);

  List<S3ObjectSummary> allFiles();
}
