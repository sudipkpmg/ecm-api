package gov.tn.dhs.ecm.service;

import com.box.sdk.*;
import gov.tn.dhs.ecm.config.AppProperties;
import gov.tn.dhs.ecm.model.CitizenMetadata;
import gov.tn.dhs.ecm.model.FileInfo;
import gov.tn.dhs.ecm.model.SearchRequest;
import gov.tn.dhs.ecm.model.SearchResult;
import gov.tn.dhs.ecm.util.ConnectionHelper;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private final AppProperties appProperties;

    public SearchService(ConnectionHelper connectionHelper, AppProperties appProperties) {
        super(connectionHelper);
        this.appProperties = appProperties;
    }

    public void process(Exchange exchange) {
        BoxDeveloperEditionAPIConnection api = getBoxApiConnection();

        SearchRequest searchRequest = exchange.getIn().getBody(SearchRequest.class);
        String folderId = searchRequest.getFolderId();
        String fileName = searchRequest.getFileName();
        long offset = searchRequest.getOffset();
        long limit = searchRequest.getLimit();

        if (folderId.isEmpty()) {
            setupError("400", "Folder Id not specified");
        }
        if (limit <= 0) {
            setupError("400", "limit cannot be zero or negative");
        }
        if (offset < 0) {
            setupError("400", "offset cannot be zero");
        }

        if (fileName.trim().isEmpty()) { // No filename has been passed
            try {
                BoxFolder folder = new BoxFolder(api, folderId);
                Metadata folderMetadata = folder.getMetadata(appProperties.getCitizenFolderMetadataTemplateName(), appProperties.getCitizenFolderMetadataTemplateScope());
                logger.info(folderMetadata.toString());
                List<FileInfo> files = new ArrayList<>();
                PartialCollection<BoxItem.Info> items = folder.getChildrenRange(offset, limit);
                for (BoxItem.Info itemInfo : items) {
                    FileInfo fileInfo = getItemInfo(itemInfo, folderMetadata);
                    files.add(fileInfo);
                }
                long allItemCount = items.fullSize();
                boolean complete = (allItemCount > (offset + limit));
                prepareSearchResult(exchange, files, complete);
            } catch (BoxAPIException e) {
                int responseCode = e.getResponseCode();
                switch (responseCode) {
                    case 404: {
                        setupError("404", "Folder not found");
                    }
                    default: {
                        setupError("500", "Search error");
                    }
                }
            }
        } else {
            BoxFolder folder = new BoxFolder(api, folderId);
            Metadata folderMetadata = folder.getMetadata(appProperties.getCitizenFolderMetadataTemplateName(), appProperties.getCitizenFolderMetadataTemplateScope());
            long allItemCount = (folder.getChildrenRange(0, 1)).fullSize();
            boolean matchNotObtained = true;
            long matchCount = 0;
            long effectiveMatchCount = 0;
            List<FileInfo> files = new ArrayList<>();
            for (BoxItem.Info info : folder) {
                String itemName = info.getName();
                if (itemName.toLowerCase().contains(fileName.toLowerCase())) {
                    matchNotObtained = false;
                    if (matchCount > offset) {
                        effectiveMatchCount++;
                        FileInfo fileInfo = getItemInfo(info, folderMetadata);
                        files.add(fileInfo);
                        if (effectiveMatchCount == limit) {
                            break;
                        }
                    }
                }
            }
            if (matchNotObtained) {
                setupError("404", "File not found");
            }
            boolean complete = ( (effectiveMatchCount < limit) || (allItemCount == matchCount) );
            prepareSearchResult(exchange, files, complete);
        }

    }

    private void prepareSearchResult(Exchange exchange, List<FileInfo> files, boolean complete) {
        SearchResult searchResult = new SearchResult();
        searchResult.setFileData(files);
        searchResult.setComplete(Boolean.toString(complete));
        setupResponse(exchange, "200", searchResult, SearchResult.class);
    }

    private FileInfo getItemInfo(BoxItem.Info itemInfo, Metadata folderMetadata) {
        FileInfo fileInfo = new FileInfo();
        String fileId = itemInfo.getID();
        String name = itemInfo.getName();
        String itemType = itemInfo.getType();
        fileInfo.setFileId(fileId);
        fileInfo.setFileName(name);
        fileInfo.setItemType(itemType);
        CitizenMetadata citizenMetadata = getCitizenMetadata(folderMetadata);
        fileInfo.setCitizenMetadata(citizenMetadata);
        return fileInfo;
    }

    private CitizenMetadata getCitizenMetadata(Metadata folderMetadata) {
        CitizenMetadata citizenMetadata = new CitizenMetadata();
        citizenMetadata.setFirstName(getMetadataStringField(folderMetadata, "/FirstName"));
        citizenMetadata.setLastName(getMetadataStringField(folderMetadata, "/LastName"));
        citizenMetadata.setSsn4(getMetadataStringField(folderMetadata, "/last4ofssn"));
        citizenMetadata.setLogonUserId(getMetadataStringField(folderMetadata, "/logonuserid"));
        citizenMetadata.setMpiId(getMetadataStringField(folderMetadata, "/mpiid"));
        citizenMetadata.setSysId(getMetadataStringField(folderMetadata, "/sysid"));
        citizenMetadata.setDob(getMetadataDateField(folderMetadata, "/dob1"));
        return citizenMetadata;
    }

    private String getMetadataStringField(Metadata metadata, String fieldPath) {
        String fieldValue = null;
        try {
            fieldValue = metadata.getString(fieldPath);
        } catch (Exception e) {
        }
        return fieldValue;
    }

    private LocalDate getMetadataDateField(Metadata metadata, String fieldPath) {
        LocalDate fieldValue = null;
        try {
            String fieldValueAsString = metadata.getString(fieldPath);
            String dateValueAsString = fieldValueAsString.substring(0, fieldValueAsString.indexOf('T'));
            fieldValue = LocalDate.parse(dateValueAsString);
        } catch (Exception e) {
        }
        return fieldValue;
    }

}
