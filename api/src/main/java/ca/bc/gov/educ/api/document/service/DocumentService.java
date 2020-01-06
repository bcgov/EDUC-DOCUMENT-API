package ca.bc.gov.educ.api.document.service;

import ca.bc.gov.educ.api.document.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.document.exception.InvalidParameterException;
import ca.bc.gov.educ.api.document.model.DocumentEntity;
import ca.bc.gov.educ.api.document.model.DocumentOwnerEntity;
import ca.bc.gov.educ.api.document.props.ApplicationProperties;
import ca.bc.gov.educ.api.document.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository repository;

    /**
     * Search for DocumentEntity by id
     *
     * @param documentID
     * @return
     * @throws EntityNotFoundException
     */
    public DocumentEntity retrieveDocument(UUID documentID) throws EntityNotFoundException {
        Optional<DocumentEntity> result =  repository.findById(documentID);
        if(result.isPresent()) {
            return result.get();
        } else {
            throw new EntityNotFoundException(DocumentEntity.class, "documentID", documentID.toString());
        }
    }

    /**
     * Creates a DocumentEntity
     *
     * @param document
     * @return
     * @throws InvalidParameterException
     */
    public DocumentEntity createDocument(DocumentEntity document) throws InvalidParameterException {

        validateParameters(document);

        if(document.getDocumentID()!=null){
            throw new InvalidParameterException("documentID");
        }
        document.getDocumentOwners().forEach(owner -> setCreateAndUpdateInfo(owner));
        document.setUpdateUser(ApplicationProperties.CLIENT_ID);
        document.setUpdateDate(new Date());
        document.setCreateUser(ApplicationProperties.CLIENT_ID);
        document.setCreateDate(new Date());

        return repository.save(document);
    }

    /**
     * Updates a DocumentEntity
     *
     * @param document
     * @return
     * @throws Exception
     */
    public DocumentEntity updateDocument(DocumentEntity document) throws EntityNotFoundException, InvalidParameterException {

        validateParameters(document);

        Optional<DocumentEntity> curDocumentEntity = repository.findById(document.getDocumentID());

        if(curDocumentEntity.isPresent())
        {
            final DocumentEntity newDocumentEntity = curDocumentEntity.get();
            newDocumentEntity.setDocumentTypeCode(document.getDocumentTypeCode());
            newDocumentEntity.setFileName(document.getFileName());
            newDocumentEntity.setFileExtension(document.getFileExtension());
            newDocumentEntity.setFileSize(document.getFileSize());
            newDocumentEntity.setDocumentData(document.getDocumentData());

            newDocumentEntity.getDocumentOwners().clear();
            document.getDocumentOwners().forEach(owner -> {
                setCreateAndUpdateInfo(owner);
                newDocumentEntity.addOwner(owner);
            });

            newDocumentEntity.setUpdateUser(ApplicationProperties.CLIENT_ID);
            newDocumentEntity.setUpdateDate(new Date());

            return repository.save(newDocumentEntity);
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
        DocumentEntity document = retrieveDocument(documentID);
        repository.delete(document);
        return document;
    }

    private void validateParameters(DocumentEntity document) throws InvalidParameterException {

        if(document.getCreateDate()!=null)
            throw new InvalidParameterException("createDate");
        if(document.getCreateUser()!=null)
            throw new InvalidParameterException("createUser");
        if(document.getUpdateDate()!=null)
            throw new InvalidParameterException("updateDate");
        if(document.getUpdateUser()!=null)
            throw new InvalidParameterException("updateUser");
    }

    private void setCreateAndUpdateInfo(DocumentOwnerEntity owner) {
        owner.setUpdateUser(ApplicationProperties.CLIENT_ID);
        owner.setUpdateDate(new Date());
        owner.setCreateUser(ApplicationProperties.CLIENT_ID);
        owner.setCreateDate(new Date());
    }
}
