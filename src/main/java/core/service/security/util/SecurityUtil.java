package core.service.security.util;

import core.authentication.AccountUserDetails;
import core.entity.Account;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by Adrian on 03/07/2015.
 */
public class SecurityUtil {
    public static void logInUser(Account acc) {
        AccountUserDetails userDetails = AccountUserDetails.getBuilder()
                .id(acc.getId())
                .password(acc.getPassword())
                .roles(acc.getAccRoles())
                .socialSignInProviders(acc.getAccSocialProviders())
                .username(acc.getEmail())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
