package oauth2.pixelpin.connect;

import oauth2.pixelpin.api.PixelPin;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

/**
 * Created by Adrian on 08/08/2015.
 */
public class PixelPinConnectionFactory extends OAuth2ConnectionFactory<PixelPin> {
    public PixelPinConnectionFactory(String client_id, String client_secret) {
        super("pixelpin", new PixelPinServiceProvider(client_id,client_secret), new PixelPinAdapter());
        System.out.println("PixelPinConnectionFactory");
    }
}
