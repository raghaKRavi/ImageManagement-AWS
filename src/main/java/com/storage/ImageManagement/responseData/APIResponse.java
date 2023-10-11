package com.storage.ImageManagement.responseData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class APIResponse {
  public String message;
  public boolean isSuccessful;
  public int statusCode;
  public Object data;
}
