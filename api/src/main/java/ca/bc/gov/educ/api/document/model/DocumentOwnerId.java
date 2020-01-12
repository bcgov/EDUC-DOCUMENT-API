package ca.bc.gov.educ.api.document.model;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentOwnerId implements Serializable {

    private static final long serialVersionUID = -6311883875768442141L;

    UUID document;

    String documentOwnerTypeCode;
    
    UUID documentOwnerID;

    public DocumentOwnerId() {
    }
}