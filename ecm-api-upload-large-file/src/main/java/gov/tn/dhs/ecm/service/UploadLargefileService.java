package gov.tn.dhs.ecm.service;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.tn.dhs.ecm.model.UploadLargefileParameters;
import gov.tn.dhs.ecm.model.UploadLargefileResponse;
import gov.tn.dhs.ecm.util.ConnectionHelper;
import gov.tn.dhs.ecm.util.JsonUtil;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import java.io.IOException;
import java.io.InputStream;

// (oc) uploaded having size 78,294,928 bytes
// (amq-broker-7.6.0-bin.zip) uploaded having 57,725,452 bytes

@Service
public class UploadLargefileService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(UploadLargefileService.class);

    public UploadLargefileService(ConnectionHelper connectionHelper) {
        super(connectionHelper);
    }

    public void process(Exchange exchange) {
        String parameterJson = exchange.getIn().getBody(String.class);
        logger.info(parameterJson);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            UploadLargefileParameters uploadLargeFileParameters = objectMapper.readValue(parameterJson, UploadLargefileParameters.class);
            String boxFolderId = uploadLargeFileParameters.getBoxFolderId();
            String appUserId = uploadLargeFileParameters.getAppUserId();
            long fileSize = uploadLargeFileParameters.getFileSize();
            DataHandler[] attachments = exchange.getIn().getAttachments().values().toArray(new DataHandler[0]);
            DataHandler dh = attachments[0];
            try (InputStream fileStream = dh.getInputStream()) {
                String fileName = dh.getName();
                UploadLargefileResponse uploadLargefileResponse = uploadLargefileToBox(fileStream, fileName, boxFolderId, appUserId, fileSize);
                setupResponse(exchange, "200", JsonUtil.toJson(uploadLargefileResponse), String.class);
            } catch (IOException e) {
                setupError("500", "File stream for uploaded file could not be read");
            }
        } catch (JsonProcessingException e) {
            setupError("400", "Invalid parameter(s)");
        }
    }

    private UploadLargefileResponse uploadLargefileToBox(InputStream inputStream, String fileName, String boxFolderId, String appUserId, long fileSize) {
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
            newFileInfo = parentFolder.uploadLargeFile(inputStream, fileName, fileSize);
        } catch (Exception e) {
            setupError("409", "File with the same name already exists");
        }
        String fileId = newFileInfo.getID();
        UploadLargefileResponse uploadLargeFileResponse = new UploadLargefileResponse();
        uploadLargeFileResponse.setStatus("File upload completed");
        uploadLargeFileResponse.setFileId(fileId);
        return uploadLargeFileResponse;
    }

}
