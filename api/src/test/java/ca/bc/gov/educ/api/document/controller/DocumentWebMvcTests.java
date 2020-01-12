package ca.bc.gov.educ.api.document.controller;

import java.nio.file.Files;
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

import ca.bc.gov.educ.api.document.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.document.model.DocumentEntity;
import ca.bc.gov.educ.api.document.props.ApplicationProperties;
import ca.bc.gov.educ.api.document.repository.DocumentRepository;
import ca.bc.gov.educ.api.document.rest.RestUtils;
import ca.bc.gov.educ.api.support.DocumentBuilder;
import ca.bc.gov.educ.api.support.MockRestServer;
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

    @Autowired
    private RestUtils restUtils;

    @Autowired
    private ApplicationProperties props;

    private UUID documentID;

    private UUID penReqID = UUID.randomUUID();


    @Before
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(documentController)
                .setControllerAdvice(new RestExceptionHandler()).build();

        MockRestServer.createServer(restUtils, props);

        DocumentEntity document = new DocumentBuilder()
                                        .withoutDocumentID()
                                        //.withoutCreateAndUpdateUser()
                                        .withTypeCode("CAPASSPORT")
                                        .build();
        this.penReqID = document.getDocumentOwners().get(0).getDocumentOwnerID();
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
    @WithMockOAuth2Scope(scope = "WRITE_DOCUMENT")
    public void createDocumentWithInvalidFileSizeTest() throws Exception {
        this.mvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .content(Files.readAllBytes(new ClassPathResource(
                "../model/document-req-invalid-filesize.json", DocumentWebMvcTests.class).getFile().toPath()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.message", containsStringIgnoringCase("documentData")));
    }

    @Test
    @WithMockOAuth2Scope(scope = "WRITE_DOCUMENT")
    public void createDocumentWithoutDocumentDataTest() throws Exception {
        this.mvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .content(Files.readAllBytes(new ClassPathResource(
                "../model/document-req-without-doc-data.json", DocumentWebMvcTests.class).getFile().toPath()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.subErrors[0].field", is("documentData")));
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

    @Test
    @WithMockOAuth2Scope(scope = "READ_DOCUMENT_REQUIREMENTS")
    public void getDocumentRequirementsTest() throws Exception {
        this.mvc.perform(get("/file-requirements")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.maxSize", is(props.getMaxFileSize())))
            .andExpect(jsonPath("$.extensions.length()", is(props.getFileExtensions().size())))
            .andExpect(jsonPath("$.extensions[0]", is(props.getFileExtensions().get(0))));
    }

    @Test
    @WithMockOAuth2Scope(scope = "WRITE_DOCUMENT_OWNER")
    public void createDocumentOwnerTest() throws Exception {
        this.mvc.perform(post("/" +  this.documentID.toString() + "/owners")
            .contentType(MediaType.APPLICATION_JSON)
            .content(Files.readAllBytes(new ClassPathResource(
                "../model/document-owner-req.json", DocumentWebMvcTests.class).getFile().toPath()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.documentID", is(this.documentID.toString())))
            .andExpect(jsonPath("$.documentOwners.length()", is(2)))
            .andExpect(jsonPath("$.documentOwners[*].documentOwnerTypeCode", hasItem("STUDENT")))
            .andExpect(jsonPath("$.documentOwners[*].documentOwnerID", hasItem("cef0cbf3-6458-4f13-a418-ee4d7e7505df")));
    }

    @Test
    @WithMockOAuth2Scope(scope = "WRITE_DOCUMENT_OWNER")
    public void createDocumentOwnerWithInvalidDocumentIdTest() throws Exception {
        this.mvc.perform(post("/" +  UUID.randomUUID().toString() + "/owners")
            .contentType(MediaType.APPLICATION_JSON)
            .content(Files.readAllBytes(new ClassPathResource(
                "../model/document-owner-req.json", DocumentWebMvcTests.class).getFile().toPath()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andDo(print())
            .andExpect(jsonPath("$.message", containsStringIgnoringCase("DocumentEntity")));
    }
}