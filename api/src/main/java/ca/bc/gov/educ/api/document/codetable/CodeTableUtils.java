package ca.bc.gov.educ.api.document.codetable;

import java.util.Collections;
import java.util.HashMap;
import java.util.function.Function;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.educ.api.codetable.model.DocumentTypeCodeEntity;
import ca.bc.gov.educ.api.codetable.model.DocumentOwnerCodeEntity;
import ca.bc.gov.educ.api.document.props.ApplicationProperties;
import ca.bc.gov.educ.api.document.rest.RestUtils;

@Service
public class CodeTableUtils {
	
	private static Logger logger = Logger.getLogger(CodeTableUtils.class);

	@Autowired
	private RestUtils restUtils;
	
	@Autowired
	private ApplicationProperties props;

	@Cacheable("documentTypeCodes")
	public HashMap<String, DocumentTypeCodeEntity> getAllDocumentTypeCodes() {
		logger.info("Fetching all document type codes");
		
		return getAllCodes("/documentType", DocumentTypeCodeEntity[].class, 
			(DocumentTypeCodeEntity entity) -> entity.getDocumentTypeCode());
	}
	
	@Cacheable("documentOwnerCodes")
	public HashMap<String, DocumentOwnerCodeEntity> getAllDocumentOwnerCodes() {
		logger.info("Fetching all document owner codes");

		return getAllCodes("/documentOwner", DocumentOwnerCodeEntity[].class, 
			(DocumentOwnerCodeEntity entity) -> entity.getDocumentOwnerCode());
	}

	public <E> HashMap<String, E> getAllCodes(String endpoint, Class<E[]> codeType, Function<E, String> getCode) {
		RestTemplate restTemplate = restUtils.getSingletonRestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		ResponseEntity<E[]> response =
			restTemplate.exchange(
				props.getCodetableApiURL() + endpoint, HttpMethod.GET,
				new HttpEntity<>("parameters", headers), codeType);

		HashMap<String, E> map = new HashMap<String, E>();
		if(response != null && response.getBody() != null) {
			for(E entity: response.getBody()) {
				map.put(getCode.apply(entity), entity);
			}
		}
		
		return map;
	}
	
}
