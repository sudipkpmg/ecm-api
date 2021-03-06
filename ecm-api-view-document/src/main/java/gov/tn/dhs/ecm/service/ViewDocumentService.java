package gov.tn.dhs.ecm.service;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import gov.tn.dhs.ecm.model.DocumentViewRequest;
import gov.tn.dhs.ecm.model.DocumentViewResult;
import gov.tn.dhs.ecm.util.ConnectionHelper;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class ViewDocumentService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(ViewDocumentService.class);

    public ViewDocumentService(ConnectionHelper connectionHelper) {
        super(connectionHelper);
    }

    public void process(Exchange exchange) {
        BoxDeveloperEditionAPIConnection api = getBoxApiConnection();

        DocumentViewRequest documentViewRequest = exchange.getIn().getBody(DocumentViewRequest.class);
        String documentId = documentViewRequest.getDocumentId();
        logger.info("view document with id {}", documentId);

        try {
            BoxFile file = new BoxFile(api, documentId);
            URL previewUrl = file.getPreviewLink();
            DocumentViewResult documentViewResult = new DocumentViewResult();
            documentViewResult.setPreviewUrl(previewUrl.toString());
            setupResponse(exchange, "200", documentViewResult, DocumentViewResult.class);
        } catch (BoxAPIException e) {
            switch (e.getResponseCode()) {
                case 404: {
                    setupError("409", "Document not found");
                }
                default: {
                    setupError("500", "Document view error");
                }
            }
        }
    }

}



