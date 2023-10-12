package com.storage.ImageManagement.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.storage.ImageManagement.exceptions.FileDownloadException;
import com.storage.ImageManagement.exceptions.FileEmptyException;
import com.storage.ImageManagement.exceptions.FileUploadException;
import com.storage.ImageManagement.responseData.APIResponse;
import com.storage.ImageManagement.service.serviceImpl.ImageManagementServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/image")
public class ImageManagementController {

  @Autowired
  ImageManagementServiceImpl imageManagementService;

  @PostMapping("/upload")
  public ResponseEntity<?> uploadImage(@RequestParam("file")MultipartFile multipartFile) throws FileEmptyException,
      FileUploadException, IOException {
    if(multipartFile.isEmpty()){
      throw new FileEmptyException("File cannot be empty. Cannot save an empty file!");
//      throw new FileUploadException("File cannot be empty");
    }

    List<String> allowedExtensions = new ArrayList<String>(Arrays.asList("jpg", "jpeg", "pdf", "png"));

    if(allowedExtensions.contains(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))){
      String fileName = imageManagementService.uploadFile(multipartFile);
      APIResponse apiResponse = APIResponse.builder()
          .message("File upload successfully => " + fileName )
          .isSuccessful(true)
          .statusCode(200)
          .build();
      return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    else {
      APIResponse apiResponse = APIResponse.builder()
          .message("Invalid File. File extension or File name is not supported")
          .statusCode(400)
          .isSuccessful(false)
          .build();

      return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/download")
  public ResponseEntity<?> downloadFile(@RequestParam("fileName") @NonNull @NotBlank String fileName) throws IOException, FileDownloadException {
    Object response = imageManagementService.downloadFile(fileName);
    if(response != null){
      return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=\"" + fileName + "\"").body(response);
    }
    else{
      APIResponse apiResponse = APIResponse.builder()
          .message("File could not be downloaded")
          .isSuccessful(false)
          .statusCode(400)
          .build();
      return new ResponseEntity(apiResponse, HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/allFiles")
  public ResponseEntity<?> getAllFiles(){
    List<S3ObjectSummary> allFilesResponse = imageManagementService.allFiles();
    if(!allFilesResponse.isEmpty()) {
      APIResponse apiResponse = APIResponse.builder()
          .message("No of files retrieved: " + allFilesResponse.size())
          .statusCode(200)
          .isSuccessful(true)
          .data(allFilesResponse)
          .build();

      return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    } else {
      APIResponse apiResponse = APIResponse.builder()
          .message("No data available")
          .statusCode(204)
          .isSuccessful(false)
          .build();

      return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
    }

  }
}
