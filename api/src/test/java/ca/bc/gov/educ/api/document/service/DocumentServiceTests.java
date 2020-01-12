package ca.bc.gov.educ.api.document.service;

import ca.bc.gov.educ.api.document.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.document.exception.InvalidParameterException;
import ca.bc.gov.educ.api.document.exception.InvalidValueException;
import ca.bc.gov.educ.api.document.model.DocumentEntity;
import ca.bc.gov.educ.api.document.model.DocumentOwnerEntity;
import ca.bc.gov.educ.api.document.props.ApplicationProperties;
import ca.bc.gov.educ.api.document.repository.DocumentRepository;
import ca.bc.gov.educ.api.document.rest.RestUtils;
import ca.bc.gov.educ.api.support.DocumentBuilder;
import ca.bc.gov.educ.api.support.DocumentOwnerBuilder;
import ca.bc.gov.educ.api.support.MockRestServer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;


@RunWith(SpringRunner.class)
@SpringBootTest
//@Transactional
public class DocumentServiceTests {

    @Autowired
    DocumentService service;

    @Autowired
    private DocumentRepository repository;

    @Autowired
    private RestUtils restUtils;

    @Autowired
    private ApplicationProperties props;

    private DocumentEntity bcscPhoto;

    @Before
    public void setUp() throws JsonMappingException, JsonProcessingException {
        this.bcscPhoto = new DocumentBuilder()
                            .withoutDocumentID()
                            .build();
        this.bcscPhoto = this.repository.save(this.bcscPhoto);

        MockRestServer.createServer(restUtils, props);
    }

    @Test
    public void createValidDocumentTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withoutCreateAndUpdateUser()
                                        .build();
        document = service.createDocument(document);

        assertThat(document).isNotNull();
        assertThat(document.getDocumentID()).isNotNull();
        assertThat(document).isEqualTo(document.getDocumentOwners().get(0).getDocument());
    }

    @Test
    public void createDocumentThrowsExceptionWhenIDGivenTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutCreateAndUpdateUser()
                                        .build();
        assertThatThrownBy(() -> service.createDocument(document))
            .isInstanceOf(InvalidParameterException.class)
            .hasMessageContaining("documentID");
    }

    @Test
    public void createDocumentThrowsExceptionWhenCreateUserGivenTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .build();
        assertThatThrownBy(() -> service.createDocument(document))
            .isInstanceOf(InvalidParameterException.class)
            .hasMessageContaining("createDate");
    }

    @Test
    public void createDocumentThrowsExceptionWhenInvalidDocTypeGivenTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withoutCreateAndUpdateUser()
                                        .withTypeCode("typeCode")
                                        .build();
        assertThatThrownBy(() -> service.createDocument(document))
            .isInstanceOf(InvalidValueException.class)
            .hasMessageContaining("documentTypeCode");
    }

    @Test
    public void createDocumentThrowsExceptionWhenInvalidDocOwnerTypeGivenTest() throws ParseException {
        DocumentOwnerEntity owner = new DocumentOwnerBuilder()
                                                .withOwnerTypeCode("ownerTypeCode")
                                                .withoutCreateAndUpdateUser()
                                                .build();
        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withoutCreateAndUpdateUser()
                                        .withOwner(owner)
                                        .build();
        assertThatThrownBy(() -> service.createDocument(document))
            .isInstanceOf(InvalidValueException.class)
            .hasMessageContaining("documentOwnerTypeCode");
    }

    @Test
    public void createDocumentThrowsExceptionWhenWrongFileSizeGivenTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withoutCreateAndUpdateUser()
                                        .withFileSize(2)
                                        .build();
        assertThatThrownBy(() -> service.createDocument(document))
            .isInstanceOf(InvalidValueException.class)
            .hasMessageContaining("fileSize");
    }

    @Test
    public void createDocumentThrowsExceptionWhenLargeFileGivenTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withoutCreateAndUpdateUser()
                                        .withFileSize(props.getMaxFileSize() + 1)
                                        .build();
        assertThatThrownBy(() -> service.createDocument(document))
            .isInstanceOf(InvalidValueException.class)
            .hasMessageContaining("Max fileSize");
    }

    @Test
    public void createDocumentThrowsExceptionWhenInvalidFileExtensionGivenTest() throws ParseException {
        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withoutCreateAndUpdateUser()
                                        .withFileExtension("txt")
                                        .build();
        assertThatThrownBy(() -> service.createDocument(document))
            .isInstanceOf(InvalidValueException.class)
            .hasMessageContaining("fileExtension");
    }

    @Test
    public void retrieveDocumentMetadataTest() throws ParseException{
        DocumentEntity retrievedDocument = service.retrieveDocumentMetadata(bcscPhoto.getDocumentID());
        assertThat(retrievedDocument).isNotNull();
        assertThat(retrievedDocument.getDocumentTypeCode()).isEqualTo("BCSCPHOTO");
        
        assertThat(retrievedDocument.getDocumentOwners().size()).isEqualTo(1);
        assertThat(retrievedDocument.getDocumentOwners().get(0).getDocumentOwnerTypeCode()).isEqualTo("PENRETRIEV");
    }

    @Test
    public void retrieveDocumentMetadataThrowsExceptionWhenInvalidIdGivenTest() throws ParseException{
        assertThatThrownBy(() -> service.retrieveDocumentMetadata(UUID.randomUUID()))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("DocumentEntity");
    }

    @Test
    public void retrieveDocumentDataTest() throws ParseException{
        DocumentEntity retrievedDocument = service.retrieveDocument(bcscPhoto.getDocumentID());
        assertThat(retrievedDocument).isNotNull();
        assertThat(retrievedDocument.getDocumentTypeCode()).isEqualTo("BCSCPHOTO");
        
        assertThat(retrievedDocument.getDocumentOwners().size()).isEqualTo(1);
        assertThat(retrievedDocument.getDocumentOwners().get(0).getDocumentOwnerTypeCode()).isEqualTo("PENRETRIEV");

        assertThat(retrievedDocument.getDocumentData()).isEqualTo(bcscPhoto.getDocumentData());
    }

    @Test
    public void updateValidDocumentTest() throws ParseException{
        DocumentEntity newDocument = new DocumentBuilder()
                                        .withDocumentID(bcscPhoto.getDocumentID())
                                        .withFileName("newCard")
                                        .withFileExtension("png")
                                        .withFileSize(12)
                                        .withData("My new card!".getBytes())
                                        .withoutCreateAndUpdateUser()
                                        .build();

        UUID penReqID = newDocument.getDocumentOwners().get(0).getDocumentOwnerID();

        DocumentEntity savedDocument = service.updateDocument(newDocument);
        assertThat(savedDocument).isNotNull();

        assertThat(savedDocument.getFileName()).isEqualTo("newCard");
        assertThat(savedDocument.getFileExtension()).isEqualTo("png");
        assertThat(savedDocument.getFileSize()).isEqualTo(12);
        assertThat(savedDocument.getDocumentData()).isEqualTo("My new card!".getBytes());
        
        assertThat(savedDocument.getDocumentOwners().size()).isEqualTo(1);
        assertThat(savedDocument.getDocumentOwners().get(0).getDocumentOwnerID()).isEqualTo(penReqID);
    }

    @Test
    public void deleteDocumentTest() throws ParseException{
        DocumentEntity deletedDocument = service.deleteDocument(this.bcscPhoto.getDocumentID());
        assertThat(deletedDocument).isNotNull();

        assertThatThrownBy(() -> service.retrieveDocumentMetadata(deletedDocument.getDocumentID()))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void deleteDocumentThrowsExceptionWhenInvalidIdGivenTest() throws ParseException{
        assertThatThrownBy(() -> service.deleteDocument(UUID.randomUUID()))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("DocumentEntity");
    }

    @Test
    public void createValidDocumentOwnerTest() throws ParseException {
        UUID ownerID = UUID.randomUUID();
        String ownerType = "STUDENT";
        DocumentOwnerEntity owner = new DocumentOwnerBuilder()
                                        .withOwnerTypeCode(ownerType)
                                        .withOwnerID(ownerID)
                                        .withoutCreateAndUpdateUser()
                                        .build();
        DocumentEntity savedDocument = service.createDocumentOwner(this.bcscPhoto.getDocumentID(), owner);

        assertThat(savedDocument).isNotNull();

        List<DocumentOwnerEntity> owners = savedDocument.getDocumentOwners();
        assertThat(owners.size()).isEqualTo(2);
        assertThat(owners).contains(owner);
    }

    @Test
    public void createDocumentOwnerThrowsExceptionWhenInvalidIDGivenTest() throws ParseException {
        UUID ownerID = UUID.randomUUID();
        String ownerType = "STUDENT";
        DocumentOwnerEntity owner = new DocumentOwnerBuilder()
                                        .withOwnerTypeCode(ownerType)
                                        .withOwnerID(ownerID)
                                        .withoutCreateAndUpdateUser()
                                        .build();

        assertThatThrownBy(() -> service.createDocumentOwner(UUID.randomUUID(), owner))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("DocumentEntity");
    }

    @Test
    public void createDocumentOwnerThrowsExceptionWhenInvalidOwnerTypeGivenTest() throws ParseException {
        UUID ownerID = UUID.randomUUID();
        DocumentOwnerEntity owner = new DocumentOwnerBuilder()
                                        .withOwnerTypeCode("ownerTypeCode")
                                        .withOwnerID(ownerID)
                                        .withoutCreateAndUpdateUser()
                                        .build();

        assertThatThrownBy(() -> service.createDocumentOwner(this.bcscPhoto.getDocumentID(), owner))
            .isInstanceOf(InvalidValueException.class)
            .hasMessageContaining("documentOwnerTypeCode");
    }

    @Test
    public void createDocumentOwnerThrowsExceptionWhenDuplicateOwnerGivenTest() throws ParseException {
        DocumentOwnerEntity owner = this.bcscPhoto.getDocumentOwners().get(0);
        DocumentOwnerEntity newOwner = new DocumentOwnerBuilder()
                                        .withOwnerTypeCode(owner.getDocumentOwnerTypeCode())
                                        .withOwnerID(owner.getDocumentOwnerID())
                                        .withoutCreateAndUpdateUser()
                                        .build();

        assertThatThrownBy(() -> service.createDocumentOwner(this.bcscPhoto.getDocumentID(), newOwner))
            .isInstanceOf(InvalidValueException.class)
            .hasMessageContaining("DocumentOwnerEntity");
    }

}
