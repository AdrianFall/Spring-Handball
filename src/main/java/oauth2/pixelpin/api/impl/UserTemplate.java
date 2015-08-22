package oauth2.pixelpin.api.impl;

import oauth2.pixelpin.api.PixelPinUserProfile;
import oauth2.pixelpin.api.UserOperations;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Adrian on 08/08/2015.
 */
public class UserTemplate extends AbstractOAuth2ApiBinding implements UserOperations {

    private final RestTemplate restTemplate;
    private static final String API_URL = "https://ws3.pixelpin.co.uk/index.php/api/userdata";

    public UserTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*@Override
    public PixelPinUserProfile getUserProfile(String userId) {
        return restTemplate.getForObject(API_URL, PixelPinUserProfile.class);
    }*/

    @Override
    public PixelPinUserProfile getUserProfile() {
        System.out.println("Obtaining PixelPin UserProfile");
        PixelPinUserProfile pixelPinUserProfile = restTemplate.getForObject(API_URL, PixelPinUserProfile.class);
        System.out.println("pixel pin user profile = " + pixelPinUserProfile.getEmail());
        return pixelPinUserProfile;
    }
}
