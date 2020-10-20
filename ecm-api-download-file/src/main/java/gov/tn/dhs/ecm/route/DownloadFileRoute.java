package gov.tn.dhs.ecm.route;

import gov.tn.dhs.ecm.model.FileDownloadRequest;
import gov.tn.dhs.ecm.service.DownloadFileService;
import org.springframework.stereotype.Component;

@Component
class DownloadFileRoute extends BaseRoute {

    private final DownloadFileService downloadFileService;

    public DownloadFileRoute(DownloadFileService downloadFileService) {
        this.downloadFileService = downloadFileService;
    }

    @Override
    public void configure() {

        super.configure();

        rest()
                .post("/download_file")
                .type(FileDownloadRequest.class)
                .outType(byte[].class)
                .to("direct:downloadFileService")
        ;
        from("direct:downloadFileService")
                .bean(downloadFileService)
                .endRest()
                ;

    }

}
