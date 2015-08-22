package oauth2.pixelpin.api.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import oauth2.pixelpin.api.PixelPin;
import oauth2.pixelpin.api.UserOperations;
import oauth2.pixelpin.api.impl.json.PixelPinModule;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.OAuth2Version;
import org.springframework.social.support.HttpRequestDecorator;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Created by Adrian on 08/08/2015.
 */
public class PixelPinTemplate extends AbstractOAuth2ApiBinding implements PixelPin {

    private UserOperations userOperations;

    private ObjectMapper objectMapper;

    private static boolean interceptorsSupported = ClassUtils.isPresent("org.springframework.http.client.ClientHttpRequestInterceptor", PixelPinTemplate.class.getClassLoader());


    public PixelPinTemplate() {
        super();
        System.out.println("PixelPinTemplate Constructor.");
        initSubApis();
    }


    public PixelPinTemplate(String access_token) {
        super(access_token);
        System.out.println("PixelPinTemplate Constructor. with access_token = " + access_token);
        registerOAuth2Interceptor(access_token);
        registerPixelPinJsonModule();
        registerJsonFormatInterceptor();
        initSubApis();
    }


    private void registerOAuth2Interceptor(String accessToken) {
        List<ClientHttpRequestInterceptor> interceptors = getRestTemplate().getInterceptors();
        interceptors.add(new OAuth2TokenParameterRequestInterceptor(accessToken));
        getRestTemplate().setInterceptors(interceptors);
    }


   /* @Override
    protected OAuth2Version getOAuth2Version() {
        return OAuth2Version.DRAFT_10;
    }*/

    public UserOperations userOperations() {
        return userOperations;
    }

    private void initSubApis() {
        this.userOperations = new UserTemplate(getRestTemplate());
    }

    // private helpers

    private void registerPixelPinJsonModule() {
        List<HttpMessageConverter<?>> converters = getRestTemplate().getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if(converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
                objectMapper = new ObjectMapper();
                objectMapper.registerModule(new PixelPinModule());
                objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
                objectMapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                jsonConverter.setObjectMapper(objectMapper);
            }
        }
    }

    /*
     * Have to register custom interceptor to
     * set  "x-li-format: "json" header as
     * otherwise we appear to get error from linkedin
     * which suggests its expecting xml rather than json.
     * API appears to ignore Content-Type header
     */
    private void registerJsonFormatInterceptor() {
        RestTemplate restTemplate = getRestTemplate();
        if (interceptorsSupported) {
            List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
            interceptors.add(new JsonFormatInterceptor());
        }
    }

    private static final class JsonFormatInterceptor implements ClientHttpRequestInterceptor {
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            HttpRequest contentTypeResourceRequest = new HttpRequestDecorator(request);
            contentTypeResourceRequest.getHeaders().add("x-li-format", "json");
            return execution.execute(contentTypeResourceRequest, body);
        }

    }

    private static final class OAuth2TokenParameterRequestInterceptor implements ClientHttpRequestInterceptor {
        private final String accessToken;

        public OAuth2TokenParameterRequestInterceptor(String accessToken) {
            this.accessToken = accessToken;
        }

        public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, ClientHttpRequestExecution execution) throws IOException {
            HttpRequest protectedResourceRequest = new HttpRequestDecorator(request) {
                @Override
                public URI getURI() {
                    System.out.println("Class (OAuth2TokenParameterRequestInterceptor) getURI()" );
                    return URI.create(super.getURI().toString() + (((super.getURI().getQuery() == null) ? "?" : "&") + "access_token=" + accessToken));
                }
            };

            // TODO Experiment whether it accepts the OAuth2 token authorization header.
            protectedResourceRequest.getHeaders().remove("Authorization");
            return execution.execute(protectedResourceRequest, body);
        }

    }
}
