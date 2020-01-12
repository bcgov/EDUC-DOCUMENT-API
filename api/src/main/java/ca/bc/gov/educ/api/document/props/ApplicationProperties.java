package ca.bc.gov.educ.api.document.props;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {
    public static final String CLIENT_ID = "DOCUMENT-API";

    @Value("${client.id}")
	private String clientID;
	@Value("${client.secret}")
	private String clientSecret;
	@Value("${token.url}")
	private String tokenURL;
	@Value("${codetable.api.url}")
	private String codetableApiURL;

	@Value("${file.maxsize}")
	private int maxFileSize;

	@Value("${file.extensions}")
	private List<String> fileExtensions;

	public String getClientID() {
		return clientID;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public String getTokenURL() {
		return tokenURL;
	}

	public String getCodetableApiURL() {
		return codetableApiURL;
	}

	public int getMaxFileSize() {
		return maxFileSize;
	}

	public List<String> getFileExtensions() {
		return fileExtensions;
	}
}
