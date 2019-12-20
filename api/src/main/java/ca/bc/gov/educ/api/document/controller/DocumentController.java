package ca.bc.gov.educ.api.document.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
//@RequestMapping
public class DocumentController {
    @GetMapping
    public String index() {
        return "Document API";
    }
}
