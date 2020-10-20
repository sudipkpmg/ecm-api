package gov.tn.dhs.ecm.exception;

import gov.tn.dhs.ecm.model.MetadataUpdationErrorResponse;
import gov.tn.dhs.ecm.util.JsonUtil;

public class MetadataUpdationErrorException extends RuntimeException {

    private String code;

    private MetadataUpdationErrorResponse metadataUpdationErrorResponse;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MetadataUpdationErrorResponse getMetadataUpdationErrorResponse() {
        return metadataUpdationErrorResponse;
    }

    public void setMetadataUpdationErrorResponse(MetadataUpdationErrorResponse metadataUpdationErrorResponse) {
        this.metadataUpdationErrorResponse = metadataUpdationErrorResponse;
    }

    public String getMessage() {
        return JsonUtil.toJson(metadataUpdationErrorResponse);
    }

}
