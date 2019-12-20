package ca.bc.gov.educ.api.document.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@RestController
@RequestMapping("/")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableResourceServer
public class DocumentController {
    
    @GetMapping("/{documentID}")
    @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT')")
    public String readDocument(@PathVariable UUID documentID) {
        return "Document: " + documentID.toString();
    }
}
