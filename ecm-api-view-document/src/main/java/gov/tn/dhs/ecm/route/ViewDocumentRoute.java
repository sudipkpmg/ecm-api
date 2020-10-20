package gov.tn.dhs.ecm.route;

import gov.tn.dhs.ecm.model.DocumentViewRequest;
import gov.tn.dhs.ecm.model.DocumentViewResult;
import gov.tn.dhs.ecm.service.ViewDocumentService;
import org.springframework.stereotype.Component;

@Component
class ViewDocumentRoute extends BaseRoute {

    private final ViewDocumentService viewDocumentService;

    public ViewDocumentRoute(ViewDocumentService viewDocumentService) {
        this.viewDocumentService = viewDocumentService;
    }

    @Override
    public void configure() {

        super.configure();

        rest()
                .post("/view_document")
                .type(DocumentViewRequest.class)
                .outType(DocumentViewResult.class)
                .to("direct:viewDocumentService")
        ;
        from("direct:viewDocumentService")
                .bean(viewDocumentService)
                .endRest()
        ;

    }

}
