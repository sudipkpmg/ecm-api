package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetadataUpdationResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("SUCCESS: ")
    List<String> successValue;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getSuccessValue() {
        return successValue;
    }

    public void setSuccessValue(List<String> successValue) {
        this.successValue = successValue;
    }
}
