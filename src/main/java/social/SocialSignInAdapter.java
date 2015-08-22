package social;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Created by Adrian on 08/08/2015.
 */
public class SocialSignInAdapter implements SignInAdapter {
    @Override
    public String signIn(String localUserId, Connection<?> connection, NativeWebRequest nativeWebRequest) {
        System.out.println("SIGN IN!!!!!!!");
        return null;
    }
}
