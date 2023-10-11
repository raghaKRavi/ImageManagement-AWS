package com.storage.ImageManagement.exceptions;

public class FileEmptyException extends CustomUploadException{
  public FileEmptyException(String message) {
    super(message);
  }
}
