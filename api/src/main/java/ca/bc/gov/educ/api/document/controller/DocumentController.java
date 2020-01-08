package ca.bc.gov.educ.api.document.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.educ.api.document.model.DocumentEntity;
import ca.bc.gov.educ.api.document.model.Views;
import ca.bc.gov.educ.api.document.service.DocumentService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableResourceServer
public class DocumentController {

    @Autowired
    private DocumentService service;

    DocumentController(DocumentService service) {
        this.service = service;
    }
    
    @GetMapping("/{documentID}")
    @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT')")
    @JsonView(Views.Document.class)
    public DocumentEntity readDocument(@PathVariable UUID documentID) {
        return service.retrieveDocument(documentID);
    }

    @PostMapping()
    @PreAuthorize("#oauth2.hasAnyScope('WRITE_DOCUMENT')")
    @JsonView(Views.DocumentMetadata.class)
    public DocumentEntity createDocument(@Validated @RequestBody DocumentEntity document) throws Exception {
        return service.createDocument(document);
    }

    @DeleteMapping("/{documentID}")
    @PreAuthorize("#oauth2.hasScope('DELETE_DOCUMENT')")
    @JsonView(Views.DocumentMetadata.class)
    public DocumentEntity deleteDocument(@PathVariable UUID documentID) {
        return service.deleteDocument(documentID);
    }

    @GetMapping("/{documentID}/metadata")
    @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT')")
    @JsonView(Views.DocumentMetadata.class)
    public DocumentEntity readDocumentMetadata(@PathVariable UUID documentID) {
        return service.retrieveDocument(documentID);
    }
}
