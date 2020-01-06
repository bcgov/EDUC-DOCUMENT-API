package ca.bc.gov.educ.api.document.service;

import ca.bc.gov.educ.api.document.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.document.model.DocumentEntity;
import ca.bc.gov.educ.api.document.model.DocumentOwnerEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class DocumentServiceTests {

    @Autowired
    DocumentService service;

    private DocumentEntity document;

    @Before
    public void initialize() {
        this.document = new DocumentEntity();
        this.document.setDocumentTypeCode("BCSCPHOTO");
        this.document.setFileName("card");
        this.document.setFileExtension("jpg");
        this.document.setFileSize(4000);
        this.document.setDocumentData("My card!".getBytes());

        DocumentOwnerEntity owner = new DocumentOwnerEntity();
        UUID penReqID = UUID.randomUUID();
        owner.setDocumentOwnerTypeCode("PENRETRIEV");
        owner.setDocumentOwnerID(penReqID);

        this.document.addOwner(owner);
    }

    @Test
    public void createValidDocumentTest() throws ParseException {
        DocumentEntity savedDocument = service.createDocument(this.document);

        assertNotNull(savedDocument);
        assertThat(savedDocument.getDocumentID()).isNotNull();
        assertThat(savedDocument).isEqualTo(savedDocument.getDocumentOwners().get(0).getDocument());
    }

    @Test
    public void retrieveValidDocumentIdTest() throws ParseException{
        DocumentEntity savedDocument = service.createDocument(this.document);
        assertNotNull(savedDocument);

        DocumentEntity retrievedDocument = service.retrieveDocument(savedDocument.getDocumentID());
        assertNotNull(retrievedDocument);
        assertThat(retrievedDocument.getDocumentTypeCode()).isEqualTo("BCSCPHOTO");
        
        assertThat(retrievedDocument.getDocumentOwners().size()).isEqualTo(1);
        assertThat(retrievedDocument.getDocumentOwners().get(0).getDocumentOwnerTypeCode()).isEqualTo("PENRETRIEV");
    }

    @Test
    public void updateValidDocumentTest() throws ParseException{
        DocumentEntity savedDocument = service.createDocument(this.document);
        assertNotNull(savedDocument);

        DocumentEntity newDocument = new DocumentEntity();
        newDocument.setDocumentID(savedDocument.getDocumentID());
        newDocument.setDocumentTypeCode("BCSCPHOTO");
        newDocument.setFileName("newCard");
        newDocument.setFileExtension("png");
        newDocument.setFileSize(5000);
        newDocument.setDocumentData("My new card!".getBytes());

        DocumentOwnerEntity owner = new DocumentOwnerEntity();
        UUID penReqID = UUID.randomUUID();
        owner.setDocumentOwnerTypeCode("PENRETRIEV");
        owner.setDocumentOwnerID(penReqID);

        newDocument.addOwner(owner);

        savedDocument = service.updateDocument(newDocument);
        assertNotNull(savedDocument);

        assertThat(savedDocument.getFileName()).isEqualTo("newCard");
        assertThat(savedDocument.getFileExtension()).isEqualTo("png");
        assertThat(savedDocument.getFileSize()).isEqualTo(5000);
        assertThat(savedDocument.getDocumentData()).isEqualTo("My new card!".getBytes());
        
        assertThat(savedDocument.getDocumentOwners().size()).isEqualTo(1);
        assertThat(savedDocument.getDocumentOwners().get(0).getDocumentOwnerID()).isEqualTo(penReqID);
    }

    @Test
    public void deleteDocumentIdTest() throws ParseException{
        DocumentEntity savedDocument = service.createDocument(this.document);
        assertNotNull(savedDocument);

        DocumentEntity deletedDocument = service.deleteDocument(savedDocument.getDocumentID());
        assertNotNull(deletedDocument);

        assertThrows(EntityNotFoundException.class, () -> {
            service.retrieveDocument(deletedDocument.getDocumentID());
        });
    }
}
