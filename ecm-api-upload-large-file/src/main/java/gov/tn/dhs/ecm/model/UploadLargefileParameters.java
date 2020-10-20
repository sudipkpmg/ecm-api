package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadLargefileParameters {

    @JsonProperty("boxFolderId")
    private String boxFolderId;

    @JsonProperty("appUserId")
    private String appUserId;

    @JsonProperty("fileSize")
    private Long fileSize;

    public String getBoxFolderId() {
        return boxFolderId;
    }

    public void setBoxFolderId(String boxFolderId) {
        this.boxFolderId = boxFolderId;
    }

    public String getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(String appUserId) {
        this.appUserId = appUserId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

}
