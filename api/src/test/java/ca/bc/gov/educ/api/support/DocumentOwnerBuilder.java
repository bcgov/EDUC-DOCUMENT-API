package ca.bc.gov.educ.api.support;

import java.util.Date;
import java.util.UUID;

import ca.bc.gov.educ.api.document.model.DocumentEntity;
import ca.bc.gov.educ.api.document.model.DocumentOwnerEntity;

public class DocumentOwnerBuilder {
    DocumentEntity document;

    String documentOwnerTypeCode = "PENRETRIEV";
    
    UUID documentOwnerID = UUID.randomUUID();

    String createUser = "API";

    Date createDate = new Date();

    String updateUser = "API";

    Date updateDate = new Date();

    public DocumentOwnerBuilder withDocument(DocumentEntity document) {
        this.document = document;
        return this;
    }

    public DocumentOwnerBuilder withOwnerTypeCode(String ownerTypeCode) {
        this.documentOwnerTypeCode = ownerTypeCode;
        return this;
    }

    public DocumentOwnerBuilder withOwnerID(UUID ownerID) {
        this.documentOwnerID = ownerID;
        return this;
    }

    public DocumentOwnerBuilder withoutCreateAndUpdateUser() {
        this.createUser = null;
        this.createDate = null;
        this.updateUser = null;
        this.updateDate = null;
        return this;
    }

    public DocumentOwnerEntity build() {
        DocumentOwnerEntity owner = new DocumentOwnerEntity();
        owner.setDocument(this.document);
        owner.setDocumentOwnerTypeCode(this.documentOwnerTypeCode);
        owner.setDocumentOwnerID(this.documentOwnerID);
        owner.setCreateUser(this.createUser);
        owner.setCreateDate(this.createDate);
        owner.setUpdateUser(this.updateUser);
        owner.setUpdateDate(this.updateDate);
        return owner;
    }

    
}