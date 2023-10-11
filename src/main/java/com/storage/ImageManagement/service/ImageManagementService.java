package com.storage.ImageManagement.service;

import com.storage.ImageManagement.exceptions.FileDownloadException;
import com.storage.ImageManagement.exceptions.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageManagementService {
  String uploadFile(MultipartFile multipartFile) throws FileUploadException, IOException;

  Object downloadFile(String fileName) throws FileDownloadException, IOException;

  boolean delete(String fileName);
}
