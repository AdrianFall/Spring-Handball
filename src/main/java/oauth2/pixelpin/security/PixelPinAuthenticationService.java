package oauth2.pixelpin.security;

import oauth2.pixelpin.api.PixelPin;
import oauth2.pixelpin.connect.PixelPinConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.security.provider.OAuth2AuthenticationService;

/**
 * Created by Adrian on 08/08/2015.
 */
public class PixelPinAuthenticationService extends OAuth2AuthenticationService<PixelPin> {
    public PixelPinAuthenticationService(String apiKey, String appSecret) {
        super(new PixelPinConnectionFactory(apiKey, appSecret));
    }
}
