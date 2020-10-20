package gov.tn.dhs.ecm.route;

import gov.tn.dhs.ecm.model.DocumentDeletionRequest;
import gov.tn.dhs.ecm.model.DocumentDeletionResult;
import gov.tn.dhs.ecm.service.DeleteDocumentService;
import org.springframework.stereotype.Component;

@Component
class DeleteDocumentRoute extends BaseRoute {

    private final DeleteDocumentService deleteDocumentService;

    public DeleteDocumentRoute(DeleteDocumentService deleteDocumentService) {
        this.deleteDocumentService = deleteDocumentService;
    }

    @Override
    public void configure() {

        super.configure();

        rest()
                .post("/delete_document")
                .type(DocumentDeletionRequest.class)
                .outType(DocumentDeletionResult.class)
                .to("direct:deleteDocumentService")
        ;
        from("direct:deleteDocumentService")
                .bean(deleteDocumentService)
                .endRest()
        ;

    }

}
