package ca.bc.gov.educ.api.document.controller;

import java.nio.file.Files;
import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.bc.gov.educ.api.document.model.DocumentEntity;
import ca.bc.gov.educ.api.document.model.DocumentOwnerEntity;
import ca.bc.gov.educ.api.document.repository.DocumentRepository;
import ca.bc.gov.educ.api.support.WithMockOAuth2Scope;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
//@AutoConfigureMockMvc
public class DocumentWebMvcTests {
    //@Autowired
    private MockMvc mvc;

    @Autowired
    DocumentController documentController;

    @Autowired
    private DocumentRepository repository;

    private UUID documentID;

    private UUID penReqID = UUID.randomUUID();


    @Before
    public void initialize() {
        mvc = MockMvcBuilders.standaloneSetup(documentController).build();

        DocumentEntity document = new DocumentEntity();
        document.setDocumentTypeCode("CAPASSPORT");
        document.setFileName("card");
        document.setFileExtension("jpg");
        document.setFileSize(4000);
        document.setDocumentData("My card!".getBytes());
        document.setCreateUser("API");
        document.setCreateDate(new Date());
        document.setUpdateUser("API");
        document.setUpdateDate(new Date());

        DocumentOwnerEntity owner = new DocumentOwnerEntity();
        this.penReqID = UUID.randomUUID();
        owner.setDocumentOwnerTypeCode("PENRETRIEV");
        owner.setDocumentOwnerID(this.penReqID);
        owner.setCreateUser("API");
        owner.setCreateDate(new Date());
        owner.setUpdateUser("API");
        owner.setUpdateDate(new Date());

        document.addOwner(owner);
        
        document = this.repository.save(document);
        this.documentID = document.getDocumentID();
    }

    @Test
    @WithMockOAuth2Scope(scope = "READ_DOCUMENT")
    public void readDocumentTest() throws Exception {
        this.mvc.perform(get("/" + this.documentID.toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.documentID", is(this.documentID.toString())))
            .andExpect(jsonPath("$.documentTypeCode", is("CAPASSPORT")))
            .andExpect(jsonPath("$.documentData", is("TXkgY2FyZCE=")))
            .andExpect(jsonPath("$.documentOwners.length()", is(1)))
            .andExpect(jsonPath("$.documentOwners[0].documentID").doesNotExist())
            .andExpect(jsonPath("$.documentOwners[0].documentOwnerTypeCode", is("PENRETRIEV")))
            .andExpect(jsonPath("$.documentOwners[0].documentOwnerID", is(this.penReqID.toString())));
    }

    @Test
    @WithMockOAuth2Scope(scope = "WRITE_DOCUMENT")
    public void createDocumentTest() throws Exception {
        this.mvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .content(Files.readAllBytes(new ClassPathResource(
                "../model/document-req.json", DocumentWebMvcTests.class).getFile().toPath()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.documentID", not(is(this.documentID.toString()))))
            .andExpect(jsonPath("$.documentTypeCode", is("BCSCPHOTO")))
            .andExpect(jsonPath("$.documentData").doesNotExist())
            .andExpect(jsonPath("$.documentOwners.length()", is(2)))
            .andExpect(jsonPath("$.documentOwners[1].documentID").doesNotExist())
            .andExpect(jsonPath("$.documentOwners[1].documentOwnerTypeCode", is("STUDENT")))
            .andExpect(jsonPath("$.documentOwners[1].documentOwnerID", is("cef0cbf3-6458-4f13-a418-ee4d7e7505df")));
    }

    @Test
    @WithMockOAuth2Scope(scope = "DELETE_DOCUMENT")
    public void deleteDocumentTest() throws Exception {
        this.mvc.perform(delete("/" + this.documentID.toString())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.documentID", is(this.documentID.toString())))
            .andExpect(jsonPath("$.documentTypeCode", is("CAPASSPORT")))
            .andExpect(jsonPath("$.documentData").doesNotExist())
            .andExpect(jsonPath("$.documentOwners.length()", is(1)))
            .andExpect(jsonPath("$.documentOwners[0].documentID").doesNotExist())
            .andExpect(jsonPath("$.documentOwners[0].documentOwnerTypeCode", is("PENRETRIEV")))
            .andExpect(jsonPath("$.documentOwners[0].documentOwnerID", is(this.penReqID.toString())));


        assertThat(repository.findById(this.documentID).isEmpty()).isTrue();
    }

    @Test
    @WithMockOAuth2Scope(scope = "READ_DOCUMENT")
    public void readDocumentMetadataTest() throws Exception {
        this.mvc.perform(get("/" + this.documentID.toString()+"/metadata").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.documentID", is(this.documentID.toString())))
            .andExpect(jsonPath("$.documentTypeCode", is("CAPASSPORT")))
            .andExpect(jsonPath("$.documentData").doesNotExist())
            .andExpect(jsonPath("$.documentOwners.length()", is(1)))
            .andExpect(jsonPath("$.documentOwners[0].documentID").doesNotExist())
            .andExpect(jsonPath("$.documentOwners[0].documentOwnerTypeCode", is("PENRETRIEV")))
            .andExpect(jsonPath("$.documentOwners[0].documentOwnerID", is(this.penReqID.toString())));
    }
}