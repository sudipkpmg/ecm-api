package gov.tn.dhs.ecm.service;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.tn.dhs.ecm.model.UploadFileParameters;
import gov.tn.dhs.ecm.model.UploadFileResponse;
import gov.tn.dhs.ecm.util.ConnectionHelper;
import gov.tn.dhs.ecm.util.JsonUtil;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import java.io.IOException;
import java.io.InputStream;

@Service
public class UploadFileService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(UploadFileService.class);

    public UploadFileService(ConnectionHelper connectionHelper) {
        super(connectionHelper);
    }

    public void process(Exchange exchange) {
        String parameterJson = exchange.getIn().getBody(String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            UploadFileParameters uploadFileParameters = objectMapper.readValue(parameterJson, UploadFileParameters.class);
            String boxFolderId = uploadFileParameters.getBoxFolderId();
            String appUserId = uploadFileParameters.getAppUserId();
            DataHandler[] attachments = exchange.getIn().getAttachments().values().toArray(new DataHandler[0]);
            DataHandler dh = attachments[0];
            try (InputStream fileStream = dh.getInputStream()) {
                String fileName = dh.getName();
                UploadFileResponse uploadFileResponse = uploadFileToBox(fileStream, fileName, boxFolderId, appUserId);
                setupResponse(exchange, "200", JsonUtil.toJson(uploadFileResponse), String.class);
            } catch (IOException e) {
                setupError("500", "File stream for uploaded file could not be read");
            }
        } catch (JsonProcessingException e) {
            setupError("400", "Invalid parameter(s)");
        }
    }

    private UploadFileResponse uploadFileToBox(InputStream inputStream, String fileName, String boxFolderId, String appUserId) {
        BoxDeveloperEditionAPIConnection api = getBoxApiConnection();
//        api.asUser(appUserId);
        BoxFolder parentFolder = null;
        try {
            parentFolder = new BoxFolder(api, boxFolderId);
            BoxFolder.Info info = parentFolder.getInfo();
            logger.info("Parent Folder with ID {} and name {} found", boxFolderId, info.getName());
        } catch (BoxAPIException e) {
            setupError("400", "Folder not found");
        }
        BoxFile.Info newFileInfo = null;
        try {
            newFileInfo = parentFolder.uploadFile(inputStream, fileName);
        } catch (BoxAPIException e) {
            setupError("409", "File with the same name already exists");
        }
        String fileId = newFileInfo.getID();
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        uploadFileResponse.setStatus("File upload completed");
        uploadFileResponse.setFileId(fileId);
        return uploadFileResponse;
    }

}
