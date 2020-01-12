package ca.bc.gov.educ.api.document.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor 
public class DocumentRequirementEntity {

	private int maxSize;

	private List<String> extensions;

}