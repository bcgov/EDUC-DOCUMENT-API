package ca.bc.gov.educ.api.document.rest;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.stereotype.Component;

import ca.bc.gov.educ.api.document.props.ApplicationProperties;

/**
 * This class is used for REST calls
 * 
 * @author Marco Villeneuve
 *
 */
@Component
public class RestUtils {

	private static Logger logger = Logger.getLogger(RestUtils.class);

	@Autowired
	private ApplicationProperties props;

	private OAuth2RestTemplate restTemplate;

	public OAuth2RestTemplate getSingletonRestTemplate() {
		if(this.restTemplate == null) {
			synchronized (this) {
				if(this.restTemplate == null) {
					this.restTemplate = getNewRestTemplate(null);
				}
			}
		}
		return this.restTemplate;
	}
	
	public OAuth2RestTemplate getNewRestTemplate(List<String> scopes) {
		logger.info("Calling get token method");

		ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
		resourceDetails.setClientId(props.getClientID());
		resourceDetails.setClientSecret(props.getClientSecret());
		resourceDetails.setAccessTokenUri(props.getTokenURL());
		if(scopes != null) {
			resourceDetails.setScope(scopes);
		}
		OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext());

		return restTemplate;
	}
}
