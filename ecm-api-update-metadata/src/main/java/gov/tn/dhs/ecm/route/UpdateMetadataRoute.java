package gov.tn.dhs.ecm.route;

import gov.tn.dhs.ecm.exception.MetadataUpdationErrorException;
import gov.tn.dhs.ecm.model.MetadataUpdationRequest;
import gov.tn.dhs.ecm.model.MetadataUpdationResponse;
import gov.tn.dhs.ecm.service.UpdateMetadataService;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
class UpdateMetadataRoute extends BaseRoute {

    public final UpdateMetadataService updateMetadataService;

    public UpdateMetadataRoute(UpdateMetadataService updateMetadataService) {
        this.updateMetadataService = updateMetadataService;
    }

    public void configure() {

        onException(MetadataUpdationErrorException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("${exception.code}"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(simple("${exception.message}"))
                ;

        super.configure();

        rest()
                .post("/update_metadata")
                .type(MetadataUpdationRequest.class)
                .outType(MetadataUpdationResponse.class)
                .to("direct:updateMetadataService")
        ;
        from("direct:updateMetadataService")
                .bean(updateMetadataService, "process")
                .endRest()
        ;

    }

}
