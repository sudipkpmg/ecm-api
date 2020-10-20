package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetadataUpdationErrorResponse {

    @JsonProperty("SUCCESS: ")
    List<String> successValue;

    @JsonProperty("PRE-CONDITION_FAILED: ")
    List<String> preConditionFailure;

    public List<String> getSuccessValue() {
        return successValue;
    }

    public void setSuccessValue(List<String> successValue) {
        this.successValue = successValue;
    }

    public List<String> getPreConditionFailure() {
        return preConditionFailure;
    }

    public void setPreConditionFailure(List<String> preConditionFailure) {
        this.preConditionFailure = preConditionFailure;
    }

}
