package ca.bc.gov.educ.api.support;

import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import ca.bc.gov.educ.api.document.props.ApplicationProperties;
import ca.bc.gov.educ.api.document.rest.RestUtils;

public class MockRestServer {

    public static MockRestServiceServer createServer(RestUtils restUtils, ApplicationProperties props) {
        OAuth2RestTemplate oAuth2RestTemplate = restUtils.getSingletonRestTemplate();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(oAuth2RestTemplate).ignoreExpectOrder(true).build();
            
        mockServer.expect(requestTo(props.getCodetableApiURL() + "/documentType"))
                .andRespond(
                    withSuccess("[{\"documentTypeCode\":\"CAPASSPORT\"},{\"documentTypeCode\":\"BCSCPHOTO\"}]",
                            MediaType.APPLICATION_JSON));
            
        mockServer.expect(requestTo(props.getCodetableApiURL() + "/documentOwner"))
                .andRespond(
                    withSuccess("[{\"documentOwnerCode\":\"STUDENT\"},{\"documentOwnerCode\":\"PENRETRIEV\"}]",
                            MediaType.APPLICATION_JSON));
            
        oAuth2RestTemplate.getOAuth2ClientContext()
            .setAccessToken(new DefaultOAuth2AccessToken("accesstoken"));

        return mockServer;
    }

}




