package com.storage.ImageManagement.controller.customExceptionHandler;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.storage.ImageManagement.exceptions.CustomUploadException;
import com.storage.ImageManagement.exceptions.FileDownloadException;
import com.storage.ImageManagement.exceptions.FileEmptyException;
import com.storage.ImageManagement.exceptions.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {FileEmptyException.class})
  protected ResponseEntity<Object> handleFileEmptyException(FileEmptyException ex, WebRequest webRequest){
    String exMessage = ex.getMessage();
    return handleExceptionInternal(ex, exMessage, new HttpHeaders(), HttpStatus.NO_CONTENT, webRequest);
  }

  @ExceptionHandler(value={FileUploadException.class})
  protected ResponseEntity<Object> handleUploadException(FileUploadException ex, WebRequest webRequest){
    String exMessage = ex.getMessage();
    return handleExceptionInternal(ex, exMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
  }

  @ExceptionHandler(value={FileDownloadException.class})
  protected ResponseEntity<Object> handleFileDownloadException(FileDownloadException ex, WebRequest webRequest){
    String exMessage = ex.getMessage();
    return handleExceptionInternal(ex, exMessage, new HttpHeaders(), HttpStatus.NOT_FOUND, webRequest);
  }

  @ExceptionHandler(value={CustomUploadException.class})
  protected ResponseEntity<Object> handleCustomUploadException(CustomUploadException ex, WebRequest webRequest){
    String exMessage = ex.getMessage();
    return handleExceptionInternal(ex, exMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
  }

  //Exception when call transmitted successfully but s3 couldn't process, so it return an error response
  @ExceptionHandler(value = {AmazonServiceException.class})
  protected ResponseEntity<Object> handleAmazonServiceException(RuntimeException ex, WebRequest webRequest){
    String exMessage = ex.getMessage();
    return handleExceptionInternal(ex, exMessage, new HttpHeaders(), HttpStatus.CONFLICT, webRequest);
  }

  //Exception for some reason when we couldn't be contact s3 or client for a response
  @ExceptionHandler(value = {SdkClientException.class})
  protected ResponseEntity<Object> handleSDKClientException(RuntimeException ex, WebRequest webRequest){
    String exMessage = ex.getMessage();
    return handleExceptionInternal(ex, exMessage, new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, webRequest);
  }

  @ExceptionHandler(value = {IOException.class, MultipartException.class, FileNotFoundException.class})
  protected ResponseEntity<Object> handleMultipleExceptions(Exception ex, WebRequest webRequest){
    String exMessage = ex.getMessage();
    return handleExceptionInternal(ex, exMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
  }

  @ExceptionHandler(value={Exception.class})
  protected ResponseEntity<Object> handleUnexpectedException(RuntimeException ex, WebRequest webRequest){
    String exMessage = ex.getMessage();
    log.info("Exception ==> ", ex);
    log.info("Message ==> ", exMessage);
    return handleExceptionInternal(ex, "Apologize, something is not right", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
  }

}
