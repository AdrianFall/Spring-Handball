package oauth2.pixelpin.connect;

import oauth2.pixelpin.api.PixelPin;
import oauth2.pixelpin.api.PixelPinUserProfile;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Created by Adrian on 08/08/2015.
 */
public class PixelPinAdapter implements ApiAdapter<PixelPin> {

    @Override
    public boolean test(PixelPin pixelPin) {
        try {
            pixelPin.userOperations().getUserProfile();
            System.out.println("PixelPinAdapter Test PASSED");
            return true;
        } catch (HttpClientErrorException e) {
            System.out.println("PixelPinAdapter Test FAILED");
            return false;
        }
    }

    @Override
    public void setConnectionValues(PixelPin pixelPin, ConnectionValues values) {
        System.out.println("PixelPinAdapter setConnectionValues()");
        PixelPinUserProfile profile = pixelPin.userOperations().getUserProfile();
        values.setProviderUserId(String.valueOf(profile.getId()));
        values.setDisplayName(profile.getFirstName());
        System.out.println("PixelPinAdapter setConnectionValues() : profile id = " + profile.getId() + " profile email = " + profile.getEmail());
    }

    @Override
    public UserProfile fetchUserProfile(PixelPin pixelPin) {
        System.out.println("PixelPinAdapter - fetchingUserProfile");
        PixelPinUserProfile profile = pixelPin.userOperations().getUserProfile();
        if (profile != null) System.out.println("Fetched a profile");
        return new UserProfileBuilder()
                .setName(profile.getFirstName())
                .setEmail(profile.getEmail())
                .build();
    }

    @Override
    public void updateStatus(PixelPin pixelPin, String s) {

    }
}
