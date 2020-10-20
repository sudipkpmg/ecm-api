package gov.tn.dhs.ecm.route;

import gov.tn.dhs.ecm.model.UploadFileResponse;
import gov.tn.dhs.ecm.service.UploadFileService;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
class UploadFileRoute extends BaseRoute {

    private final UploadFileService uploadFileService;

    public UploadFileRoute(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    @Override
    public void configure() {

        super.configure();

        rest()
                .bindingMode(RestBindingMode.off)
                .post("/upload_file")
                .outType(UploadFileResponse.class)
                .to("direct:uploadFile")
        ;
        from("direct:uploadFile")
                .unmarshal()
                .mimeMultipart()
                .bean(uploadFileService)
                .endRest()
        ;

    }

}
