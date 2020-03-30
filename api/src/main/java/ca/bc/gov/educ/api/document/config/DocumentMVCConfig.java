package ca.bc.gov.educ.api.document.config;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class DocumentMVCConfig implements WebMvcConfigurer {

    @Getter(AccessLevel.PRIVATE)
    private final DocumentRequestInterceptor documentRequestInterceptor;

    @Autowired
    public DocumentMVCConfig(final DocumentRequestInterceptor documentRequestInterceptor){
        this.documentRequestInterceptor = documentRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(documentRequestInterceptor).addPathPatterns("/**/**/");
    }
}
