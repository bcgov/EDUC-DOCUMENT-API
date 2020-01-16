package ca.bc.gov.educ.api.document.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import ca.bc.gov.educ.api.document.repository.DocumentOwnerRepository;
import ca.bc.gov.educ.api.document.repository.DocumentRepository;
import ca.bc.gov.educ.api.support.DocumentBuilder;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DocumentJpaTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DocumentRepository repository;

    @Autowired
    private DocumentOwnerRepository ownerRepository;

    private DocumentEntity document;

    @Before
    public void setUp() {
        this.document = new DocumentBuilder().withoutDocumentID().build();

        this.entityManager.persist(document);
        this.entityManager.flush();
        //document = this.repository.save(document);
        this.entityManager.clear();
    }

    @Test
    public void findDocumentTest() {
        Optional<DocumentEntity> myDocument = this.repository.findById(this.document.getDocumentID());
        assertThat(myDocument.isPresent()).isTrue();
        assertThat(myDocument.get().getDocumentOwners().size()).isEqualTo(1);
        assertThat(myDocument.get().getDocumentOwners().get(0).getDocumentOwnerTypeCode()).isEqualTo("PENRETRIEV");
    }

    @Test
    public void saveDocumentTest() {
        DocumentEntity myDocument = new DocumentBuilder().withoutDocumentID().build();
        DocumentEntity savedDocument = this.repository.save(myDocument);
        assertThat(savedDocument.getDocumentID()).isNotEqualTo(this.document.getDocumentID());

        assertThat(this.repository.findById(savedDocument.getDocumentID()).isPresent()).isTrue();

        DocumentOwnerId ownerId = new DocumentOwnerId();
        ownerId.setDocument(savedDocument.getDocumentID());
        DocumentOwnerEntity owner = savedDocument.getDocumentOwners().get(0);
        ownerId.setDocumentOwnerTypeCode(owner.getDocumentOwnerTypeCode());
        ownerId.setDocumentOwnerID(owner.getDocumentOwnerID());

        assertThat(this.ownerRepository.findById(ownerId).isPresent()).isTrue();
    }

    @Test
    public void deleteDocumentTest() {
        this.repository.deleteById(this.document.getDocumentID());
        assertThat(this.repository.findById(this.document.getDocumentID()).isPresent()).isFalse();

        DocumentOwnerId ownerId = new DocumentOwnerId();
        ownerId.setDocument(this.document.getDocumentID());
        DocumentOwnerEntity owner = this.document.getDocumentOwners().get(0);
        ownerId.setDocumentOwnerTypeCode(owner.getDocumentOwnerTypeCode());
        ownerId.setDocumentOwnerID(owner.getDocumentOwnerID());

        assertThat(this.ownerRepository.findById(ownerId).isPresent()).isFalse();
    }
}