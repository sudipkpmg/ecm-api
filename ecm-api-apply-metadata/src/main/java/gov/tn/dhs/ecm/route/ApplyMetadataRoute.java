package gov.tn.dhs.ecm.route;

import gov.tn.dhs.ecm.model.MetadataAdditionRequest;
import gov.tn.dhs.ecm.model.MetadataAdditionResponse;
import gov.tn.dhs.ecm.service.ApplyMetadataService;
import org.springframework.stereotype.Component;

@Component
class ApplyMetadataRoute extends BaseRoute {

    public final ApplyMetadataService applyMetadataService;

    public ApplyMetadataRoute(ApplyMetadataService applyMetadataService) {
        this.applyMetadataService = applyMetadataService;
    }

    @Override
    public void configure() {

        super.configure();

        rest()
                .post("/apply_metadata")
                .type(MetadataAdditionRequest.class)
                .outType(MetadataAdditionResponse.class)
                .to("direct:applyMetadataService")
        ;
        from("direct:applyMetadataService")
                .bean(applyMetadataService)
                .endRest()
        ;

    }

}
