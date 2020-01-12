package ca.bc.gov.educ.api.document.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.junit4.SpringRunner;

import ca.bc.gov.educ.api.support.DocumentBuilder;


@RunWith(SpringRunner.class)
@AutoConfigureJsonTesters
public class DocumentEntityJsonTests {
    @Autowired
    private JacksonTester<DocumentEntity> jsonTester;

    private DocumentEntity document;

    private UUID penReqID;

    @Before
    public void setUp() {
        this.document = new DocumentBuilder().build();
        this.penReqID = this.document.getDocumentOwners().get(0).getDocumentOwnerID();
    }

    @Test
    public void documentSerializeTest() throws Exception { 
        JsonContent<DocumentEntity> json = this.jsonTester.forView(Views.Document.class)
                                                .write(this.document); 

        assertThat(json).hasJsonPathStringValue("@.documentID");
        assertThat(json).extractingJsonPathStringValue("@.documentTypeCode")
            .isEqualToIgnoringCase("BCSCPHOTO");
        assertThat(json).extractingJsonPathStringValue("@.documentData")
            .isEqualToIgnoringCase("TXkgY2FyZCE=");
        
        assertThat(json).extractingJsonPathNumberValue("@.documentOwners.length()")
            .isEqualTo(1);
        assertThat(json).doesNotHaveJsonPathValue("@.documentOwners[0].documentID");
        assertThat(json).extractingJsonPathStringValue("@.documentOwners[0].documentOwnerTypeCode")
            .isEqualToIgnoringCase("PENRETRIEV");
        assertThat(json).extractingJsonPathStringValue("@.documentOwners[0].documentOwnerID")
            .isEqualToIgnoringCase(this.penReqID.toString());
    }

    @Test
    public void documentMetadataSerializeTest() throws Exception { 
        JsonContent<DocumentEntity> json = this.jsonTester.forView(Views.DocumentMetadata.class)
                                                .write(this.document); 

        assertThat(json).hasJsonPathStringValue("@.documentID");
        assertThat(json).extractingJsonPathStringValue("@.documentTypeCode")
            .isEqualToIgnoringCase("BCSCPHOTO");
        assertThat(json).doesNotHaveJsonPathValue("@.documentData");
        
        assertThat(json).extractingJsonPathNumberValue("@.documentOwners.length()")
            .isEqualTo(1);
        assertThat(json).doesNotHaveJsonPathValue("@.documentOwners[0].documentID");
        assertThat(json).extractingJsonPathStringValue("@.documentOwners[0].documentOwnerTypeCode")
            .isEqualToIgnoringCase("PENRETRIEV");
        assertThat(json).extractingJsonPathStringValue("@.documentOwners[0].documentOwnerID")
            .isEqualToIgnoringCase(this.penReqID.toString());
    }

    @Test
    public void documentDeserializeTest() throws Exception {
        DocumentEntity document = this.jsonTester.readObject("document.json");
        assertThat(document.getDocumentData()).isEqualTo("My card!".getBytes());
        assertThat(document.getDocumentOwners().size()).isEqualTo(2);
        assertThat(document.getDocumentOwners().get(0).getDocumentOwnerTypeCode()).isEqualTo("PENRETRIEV");
    }

    @Test
    public void documentDeserializeWithExtraTest() throws Exception {
        DocumentEntity document = this.jsonTester.readObject("document-extra-properties.json");
        assertThat(document.getDocumentOwners().size()).isEqualTo(2);
        assertThat(document.getDocumentOwners().get(0).getDocumentOwnerTypeCode()).isEqualTo("PENRETRIEV");

        assertThat(document.getDocumentOwners().get(0).getDocument()).isNull();
        assertThat(document.getDocumentOwners().get(0).getCreateUser()).isNull();
        assertThat(document.getDocumentOwners().get(0).getCreateDate()).isNull();
        assertThat(document.getDocumentOwners().get(0).getUpdateUser()).isNull();
        assertThat(document.getDocumentOwners().get(0).getUpdateDate()).isNull();
    }

}