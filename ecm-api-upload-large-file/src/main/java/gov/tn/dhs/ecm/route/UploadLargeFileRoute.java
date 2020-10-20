package gov.tn.dhs.ecm.route;

import gov.tn.dhs.ecm.model.UploadLargefileResponse;
import gov.tn.dhs.ecm.service.UploadLargefileService;
import org.apache.camel.Exchange;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
class UploadLargeFileRoute extends BaseRoute {

    private final UploadLargefileService uploadLargefileService;

    public UploadLargeFileRoute(UploadLargefileService uploadLargefileService) {
        this.uploadLargefileService = uploadLargefileService;
    }

    @Override
    public void configure() {

        super.configure();

        rest()
                .bindingMode(RestBindingMode.off)
                .post("/upload_largefile")
                .outType(UploadLargefileResponse.class)
                .to("direct:uploadlargefile")
        ;
        from("direct:uploadlargefile")
                .unmarshal()
                .mimeMultipart()
                .bean(uploadLargefileService)
                .endRest()
        ;

    }

}
