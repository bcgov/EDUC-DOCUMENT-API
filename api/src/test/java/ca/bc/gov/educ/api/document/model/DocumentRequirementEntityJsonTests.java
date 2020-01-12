package ca.bc.gov.educ.api.document.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@AutoConfigureJsonTesters
public class DocumentRequirementEntityJsonTests {
    @Autowired
    private JacksonTester<DocumentRequirementEntity> jsonTester;

    @Test
    public void requirementSerializeTest() throws Exception {
        int maxSize = 100;
        List<String> extensions = new ArrayList<String>(Arrays.asList("jpg", "png", "pdf"));
        DocumentRequirementEntity requirement = new DocumentRequirementEntity(maxSize, extensions);

        JsonContent<DocumentRequirementEntity> json = this.jsonTester.write(requirement); 

        assertThat(json).extractingJsonPathNumberValue("@.maxSize")
            .isEqualTo(maxSize);
        
        assertThat(json).extractingJsonPathNumberValue("@.extensions.length()")
            .isEqualTo(extensions.size());
        assertThat(json).extractingJsonPathStringValue("@.extensions[0]")
            .isEqualToIgnoringCase(extensions.get(0));
    }

    @Test
    public void documentDeserializeTest() throws Exception {
        DocumentRequirementEntity document = this.jsonTester.readObject("requirement.json");
        assertThat(document.getMaxSize()).isEqualTo(20);
        assertThat(document.getExtensions().size()).isEqualTo(2);
        assertThat(document.getExtensions().get(0)).isEqualTo("pdf");
    }


}