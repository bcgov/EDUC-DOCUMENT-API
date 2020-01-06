package ca.bc.gov.educ.api.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan("ca.bc.gov.educ.api.document")
@ComponentScan("ca.bc.gov.educ.api.document")
public class DocumentApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentApiApplication.class, args);
	}

}
