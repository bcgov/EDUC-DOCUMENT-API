package ca.bc.gov.educ.api.document.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import ca.bc.gov.educ.api.document.repository.DocumentRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DocumentJpaTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DocumentRepository repository;

    private UUID documentID;

    @Before
    public void initialize() {
        DocumentEntity document = new DocumentEntity();
        document.setDocumentTypeCode("BCSCPHOTO");
        document.setFileName("card");
        document.setFileExtension("jpg");
        document.setFileSize(4000);
        document.setDocumentData("My card!".getBytes());
        document.setCreateUser("API");
        document.setCreateDate(new Date());
        document.setUpdateUser("API");
        document.setUpdateDate(new Date());

        DocumentOwnerEntity owner = new DocumentOwnerEntity();
        UUID penReqID = UUID.randomUUID();
        owner.setDocumentOwnerTypeCode("PENRETRIEV");
        owner.setDocumentOwnerID(penReqID);
        owner.setCreateUser("API");
        owner.setCreateDate(new Date());
        owner.setUpdateUser("API");
        owner.setUpdateDate(new Date());

        document.addOwner(owner);
        
        this.entityManager.persist(document);
        this.entityManager.flush();
        //document = this.repository.save(document);
        this.documentID = document.getDocumentID();
        this.entityManager.clear();
    }

    @Test
    public void documentDataTest() {
        Optional<DocumentEntity> myDocument = this.repository.findById(this.documentID);
        assertThat(myDocument.isPresent()).isTrue();
        assertThat(myDocument.get().getDocumentOwners().size()).isEqualTo(1);
        assertThat(myDocument.get().getDocumentOwners().get(0).getDocumentOwnerTypeCode()).isEqualTo("PENRETRIEV");
    }
}