package oauth2.pixelpin.api;

import org.springframework.social.ApiBinding;

/**
 * Created by Adrian on 08/08/2015.
 */
public interface PixelPin extends ApiBinding {

    UserOperations userOperations();

}
