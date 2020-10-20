package gov.tn.dhs.ecm.route;

import gov.tn.dhs.ecm.model.SearchRequest;
import gov.tn.dhs.ecm.model.SearchResult;
import gov.tn.dhs.ecm.service.SearchService;
import org.springframework.stereotype.Component;

@Component
class SearchRoute extends BaseRoute {

    public final SearchService searchService;

    public SearchRoute(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public void configure() {

        super.configure();

        rest()
                .post("/search")
                .type(SearchRequest.class)
                .outType(SearchResult.class)
                .to("direct:searchService")
        ;
        from("direct:searchService")
                .bean(searchService)
                .endRest()
        ;

    }

}
