package ca.bc.gov.educ.api.support;

import java.util.Date;
import java.util.UUID;

import ca.bc.gov.educ.api.document.model.DocumentEntity;
import ca.bc.gov.educ.api.document.model.DocumentOwnerEntity;

public class DocumentBuilder {
    UUID documentID = UUID.randomUUID();

    String documentTypeCode = "BCSCPHOTO";

    String fileName = "card";

    String fileExtension = "jpg";

    int fileSize = 8;

    DocumentOwnerEntity documentOwner = new DocumentOwnerBuilder().build();

    String createUser = "API";

    Date createDate = new Date();

    String updateUser = "API";

    Date updateDate = new Date();

    byte[] documentData = "My card!".getBytes();


    public DocumentBuilder withDocumentID(UUID documentID) {
        this.documentID = documentID;
        return this;
    }

    public DocumentBuilder withoutDocumentID() {
        this.documentID = null;
        return this;
    }

    public DocumentBuilder withTypeCode(String typeCode) {
        this.documentTypeCode = typeCode;
        return this;
    }

    public DocumentBuilder withFileName(String fileNmae) {
        this.fileName = fileNmae;
        return this;
    }

    public DocumentBuilder withFileExtension(String fileExtention) {
        this.fileExtension = fileExtention;
        return this;
    }

    public DocumentBuilder withFileSize(int fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public DocumentBuilder withOwner(DocumentOwnerEntity owner) {
        this.documentOwner = owner;
        return this;
    }

    public DocumentBuilder withData(byte[] data) {
        this.documentData = data;
        return this;
    }

    public DocumentBuilder withoutCreateAndUpdateUser() {
        this.createUser = null;
        this.createDate = null;
        this.updateUser = null;
        this.updateDate = null;

        if(this.documentOwner != null) {
            this.documentOwner.setCreateUser(null);
            this.documentOwner.setCreateDate(null);
            this.documentOwner.setUpdateUser(null);
            this.documentOwner.setUpdateDate(null);
        }
        return this;
    }

    public DocumentEntity build() {
        DocumentEntity doc = new DocumentEntity();
        doc.setDocumentID(this.documentID);
        doc.setDocumentTypeCode(this.documentTypeCode);
        doc.setFileName(this.fileName);
        doc.setFileExtension(this.fileExtension);
        doc.setFileSize(this.fileSize);
        doc.setDocumentData(this.documentData);
        doc.setCreateUser(this.createUser);
        doc.setCreateDate(this.createDate);
        doc.setUpdateUser(this.updateUser);
        doc.setUpdateDate(this.updateDate);

        if(this.documentOwner != null) {
            doc.addOwner(this.documentOwner);
        } 
        return doc;
    }

    
}