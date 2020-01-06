package ca.bc.gov.educ.api.document.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@AutoConfigureJsonTesters
public class DocumentEntityJsonTests {
    @Autowired
    private JacksonTester<DocumentEntity> jsonTester;

    private DocumentEntity document;

    private UUID penReqID = UUID.randomUUID();

    @Before
    public void initilize() {
        this.document = new DocumentEntity();
        UUID documentID = UUID.randomUUID();
        document.setDocumentID(documentID);
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
        
        //owner.setDocument(document);
        owner.setDocumentOwnerTypeCode("PENRETRIEV");
        owner.setDocumentOwnerID(this.penReqID);
        owner.setCreateUser("API");
        owner.setCreateDate(new Date());
        owner.setUpdateUser("API");
        owner.setUpdateDate(new Date());

        document.addOwner(owner);
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

}