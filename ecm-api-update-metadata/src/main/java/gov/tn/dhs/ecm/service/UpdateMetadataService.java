package gov.tn.dhs.ecm.service;

import gov.tn.dhs.ecm.exception.MetadataUpdationErrorException;
import gov.tn.dhs.ecm.model.MetadataUpdationErrorResponse;
import gov.tn.dhs.ecm.model.MetadataUpdationRequest;
import gov.tn.dhs.ecm.model.MetadataUpdationResponse;
import gov.tn.dhs.ecm.util.ConnectionHelper;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UpdateMetadataService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(UpdateMetadataService.class);

    public UpdateMetadataService(ConnectionHelper connectionHelper) {
        super(connectionHelper);
    }

    public void process(Exchange exchange) {
        MetadataUpdationRequest metadataUpdationRequest = exchange.getIn().getBody(MetadataUpdationRequest.class);
        String documentId = metadataUpdationRequest.getDocumentId();

        switch (documentId) {
            case "1": {
                MetadataUpdationResponse metadataUpdationResponse = new MetadataUpdationResponse();
                metadataUpdationResponse.setMessage("First Response");
                setupResponse(exchange, "200", metadataUpdationResponse, MetadataUpdationResponse.class);
                break;
            }
            case "2": {
                MetadataUpdationResponse metadataUpdationResponse = new MetadataUpdationResponse();
                metadataUpdationResponse.setSuccessValue(Arrays.asList("status one", "status two", "status three"));
                setupResponse(exchange, "207", metadataUpdationResponse, MetadataUpdationResponse.class);
                break;
            }
            case "3": {
                MetadataUpdationErrorResponse metadataUpdationErrorResponse = new MetadataUpdationErrorResponse();
                metadataUpdationErrorResponse.setPreConditionFailure(Arrays.asList("reason 1", "reason 2"));
                MetadataUpdationErrorException exception = new MetadataUpdationErrorException();
                exception.setCode("412");
                exception.setMetadataUpdationErrorResponse(metadataUpdationErrorResponse);
                throw exception;
            }
            case "4": {
                MetadataUpdationErrorResponse metadataUpdationErrorResponse = new MetadataUpdationErrorResponse();
                metadataUpdationErrorResponse.setSuccessValue(Arrays.asList("reason 1", "reason 2"));
                metadataUpdationErrorResponse.setPreConditionFailure(Arrays.asList("reason 1", "reason 2", "reason 3"));
                MetadataUpdationErrorException exception = new MetadataUpdationErrorException();
                exception.setCode("412");
                exception.setMetadataUpdationErrorResponse(metadataUpdationErrorResponse);
                throw exception;
            }
            default: {
                setupError("400", "default error");
            }
        }
    }

}
