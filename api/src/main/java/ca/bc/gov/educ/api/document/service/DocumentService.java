package ca.bc.gov.educ.api.document.service;

import ca.bc.gov.educ.api.document.codetable.CodeTableUtils;
import ca.bc.gov.educ.api.document.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.document.exception.InvalidParameterException;
import ca.bc.gov.educ.api.document.exception.InvalidValueException;
import ca.bc.gov.educ.api.document.model.DocumentEntity;
import ca.bc.gov.educ.api.document.model.DocumentOwnerEntity;
import ca.bc.gov.educ.api.document.model.DocumentRequirementEntity;
import ca.bc.gov.educ.api.document.props.ApplicationProperties;
import ca.bc.gov.educ.api.document.repository.DocumentRepository;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

@Service
public class DocumentService {

    private static Logger logger = Logger.getLogger(DocumentService.class);

    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private CodeTableUtils codeTableUtils;
    
    @Autowired
    private ApplicationProperties properties;

    /**
     * Search for Document Metadata by id
     *
     * @param documentID
     * @return
     * @throws EntityNotFoundException
     */
    public DocumentEntity 
    retrieveDocumentMetadata(UUID documentID) throws EntityNotFoundException {
        logger.info("retrieving Document Metadata, documentID: " + documentID.toString());

        Optional<DocumentEntity> result =  documentRepository.findById(documentID);
        if(result.isPresent()) {
            DocumentEntity document = result.get();
            return document;
        } else {
            throw new EntityNotFoundException(DocumentEntity.class, "documentID", documentID.toString());
        }
    }

    /**
     * Search for Document with data by id
     *
     * @param documentID
     * @return
     * @throws EntityNotFoundException
     */
    @Transactional
    public DocumentEntity retrieveDocument(UUID documentID) throws EntityNotFoundException {
        logger.info("retrieving Document, documentID: " + documentID.toString());

        DocumentEntity document = retrieveDocumentMetadata(documentID);
        //triger lazy loading 
        if(document.getDocumentData().length == 0) {
            document.setFileSize(0);
        }
        return document;
    }

    /**
     * Creates a DocumentEntity
     *
     * @param document
     * @return
     * @throws InvalidParameterException
     */
    public DocumentEntity createDocument(DocumentEntity document) throws InvalidParameterException {
        logger.info("creating Document, document: " + document.toString());

        validateParameters(document);

        if(document.getDocumentID()!=null) {
            throw new InvalidParameterException("documentID");
        }

        addDocumentOwners(document, document.getDocumentOwners());
        
        document.setCreateUser(ApplicationProperties.CLIENT_ID);
        document.setCreateDate(new Date());

        return documentRepository.save(document);
    }

    /**
     * Updates a DocumentEntity
     *
     * @param document
     * @return
     * @throws Exception
     */
    public DocumentEntity updateDocument(DocumentEntity document) throws EntityNotFoundException, InvalidParameterException {
        logger.info("updating Document, document: " + document.toString());

        validateParameters(document);

        Optional<DocumentEntity> curDocumentEntity = documentRepository.findById(document.getDocumentID());

        if(curDocumentEntity.isPresent())
        {
            final DocumentEntity newDocumentEntity = curDocumentEntity.get();
            newDocumentEntity.setDocumentTypeCode(document.getDocumentTypeCode());
            newDocumentEntity.setFileName(document.getFileName());
            newDocumentEntity.setFileExtension(document.getFileExtension());
            newDocumentEntity.setFileSize(document.getFileSize());
            newDocumentEntity.setDocumentData(document.getDocumentData());

            newDocumentEntity.getDocumentOwners().clear();

            addDocumentOwners(newDocumentEntity, document.getDocumentOwners());

            return documentRepository.save(newDocumentEntity);
        } else {
            throw new EntityNotFoundException(DocumentEntity.class, "documentID", document.getDocumentID().toString());
        }
    }

    /**
     * Delete DocumentEntity by id
     *
     * @param documentID
     * @return
     * @throws EntityNotFoundException
     */
    public DocumentEntity deleteDocument(UUID documentID) throws EntityNotFoundException {
        logger.info("deleting Document, documentID: " + documentID.toString());

        DocumentEntity document = retrieveDocumentMetadata(documentID);
        documentRepository.delete(document);
        return document;
    }
    
    /**
     * Create a DocumentOwnerEntity
     *
     * @param document
     * @return
     * @throws Exception
     */
    public DocumentEntity createDocumentOwner(UUID documentID, DocumentOwnerEntity ownerEntity) 
        throws EntityNotFoundException, InvalidParameterException {
        logger.info("creating Document Owner, documentID: " + documentID.toString() + ", owner: " + ownerEntity.toString());
        
        validateParameters(ownerEntity);

        Optional<DocumentEntity> option = documentRepository.findById(documentID);

        if(option.isPresent())
        {
            DocumentEntity document = option.get();
            if(document.getDocumentOwners().contains(ownerEntity)) {
                throw new InvalidValueException("duplicate DocumentOwnerEntity", ownerEntity.toString());
            }
            addDocumentOwner(document, ownerEntity);

            return documentRepository.save(document);
        } else {
            throw new EntityNotFoundException(DocumentEntity.class, "documentID", documentID.toString());
        }
    }

    /**
     * Get File Upload Requirement
     *
     * @return DocumentRequirementEntity
     */
    public DocumentRequirementEntity getDocumentRequirements() {
        logger.info("retrieving Document Requirements");

        return new DocumentRequirementEntity(properties.getMaxFileSize(), properties.getFileExtensions());
    }

    private void validateParameters(DocumentOwnerEntity owner) throws InvalidParameterException {

        if(owner.getCreateDate()!=null) {
            throw new InvalidParameterException("createDate");
        }
            
        if(owner.getCreateUser()!=null) {
            throw new InvalidParameterException("createUser");
        }
            
        if(owner.getUpdateDate()!=null) {
            throw new InvalidParameterException("updateDate");
        }
            
        if(owner.getUpdateUser()!=null) {
            throw new InvalidParameterException("updateUser");
        }

        if(! codeTableUtils.getAllDocumentOwnerCodes().containsKey(owner.getDocumentOwnerTypeCode())) {
            throw new InvalidValueException("documentOwnerTypeCode", owner.getDocumentOwnerTypeCode());
        }
    }

    private void validateParameters(DocumentEntity document) throws InvalidParameterException {

        if(document.getCreateDate()!=null) {
            throw new InvalidParameterException("createDate");
        }
            
        if(document.getCreateUser()!=null) {
            throw new InvalidParameterException("createUser");
        }
            
        if(document.getUpdateDate()!=null) {
            throw new InvalidParameterException("updateDate");
        }
            
        if(document.getUpdateUser()!=null) {
            throw new InvalidParameterException("updateUser");
        }

        if(! properties.getFileExtensions().contains(document.getFileExtension())) {
            throw new InvalidValueException("fileExtension", document.getFileSize().toString());
        }

        if(document.getFileSize() > properties.getMaxFileSize()) {
            throw new InvalidValueException("fileSize", document.getFileSize().toString(), 
                "Max fileSize", String.valueOf(properties.getMaxFileSize()));
        }

        if(document.getFileSize() != document.getDocumentData().length) {
            throw new InvalidValueException("fileSize", document.getFileSize().toString(), 
                "documentData length", String.valueOf(document.getDocumentData().length));
        }

        if(! codeTableUtils.getAllDocumentTypeCodes().containsKey(document.getDocumentTypeCode())) {
            throw new InvalidValueException("documentTypeCode", document.getDocumentTypeCode());
        }

        document.getDocumentOwners().forEach(owner -> {
            validateParameters(owner);
        });
    }

    private void addDocumentOwner(DocumentEntity document, DocumentOwnerEntity owner) {
        Date curDate = new Date();
        document.addOwner(owner);

        owner.setUpdateUser(ApplicationProperties.CLIENT_ID);
        owner.setUpdateDate(curDate);
        owner.setCreateUser(ApplicationProperties.CLIENT_ID);
        owner.setCreateDate(curDate);
    }

    private void addDocumentOwners(DocumentEntity document, List<DocumentOwnerEntity> owners) {
        owners.forEach(owner -> {
            addDocumentOwner(document, owner);
        });

        Date curDate = new Date();
        document.setUpdateUser(ApplicationProperties.CLIENT_ID);
        document.setUpdateDate(curDate);
    }
}
