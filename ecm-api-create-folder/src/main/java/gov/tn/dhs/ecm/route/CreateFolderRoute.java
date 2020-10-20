package gov.tn.dhs.ecm.route;

import gov.tn.dhs.ecm.model.CitizenMetadata;
import gov.tn.dhs.ecm.model.FolderCreationSuccessResponse;
import gov.tn.dhs.ecm.service.CreateFolderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class CreateFolderRoute extends BaseRoute {

    private final CreateFolderService createFolderService;

    public CreateFolderRoute(CreateFolderService createFolderService) {
        this.createFolderService = createFolderService;
    }

    @Override
    public void configure() {

        super.configure();

        rest()
                .post("/create_folder")
                .type(CitizenMetadata.class)
                .outType(FolderCreationSuccessResponse.class)
                .to("direct:createFolderService")
        ;
        from("direct:createFolderService")
                .bean(createFolderService)
                .endRest()
        ;

    }

}
