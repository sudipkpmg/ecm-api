package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchRequest {

  @JsonProperty("fileName")
  private String fileName;

  @JsonProperty("folderId")
  private String folderId;

  @JsonProperty("offset")
  private int offset;

  @JsonProperty("limit")
  private int limit;

  public SearchRequest() {
    fileName = "";
    folderId = "";
    offset = 0;
    limit = 20;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFolderId() {
    return folderId;
  }

  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }
}

